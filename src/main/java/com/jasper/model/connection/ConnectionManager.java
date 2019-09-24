package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.models.HttpParser;
import com.jasper.model.socket.models.SocketMessageParser;
import com.jasper.model.socket.models.SocketResponse;
import com.jasper.model.socket.models.entity.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jasper.model.http.enums.SocketMessageState.END_FRAME;
import static com.jasper.model.http.enums.StatusCode.BAD_REQUEST;
import static com.jasper.model.socket.enums.OpCode.PONG;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 * All the operation done on the object.
 */
public class ConnectionManager implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private Controller controller;
    private static CopyOnWriteArrayList<Client> connections = new CopyOnWriteArrayList<>();
    private RequestHandler requestHandler = null;

    ExecutorService executor = Executors.newFixedThreadPool(4);

    public ConnectionManager(Controller controller) {
        this.controller = controller;
        this.requestHandler = new RequestHandler(controller);
    }


    //goot multiple connections while expecting one.
    @Override
    public void run() {
        while (true) {
            for (Client client : connections) {
                processClient(client);
            }
        }
    }

    public void processClient(Client client) {
        LOG.debug("Connecting on [" + Thread.currentThread().getName() + "]");

        OutputStream out = null;
        InputStream in = null;
        HttpRequest request = null;
        Socket soc = client.getClientSocket();

        try {
            out = soc.getOutputStream();
            in = soc.getInputStream();

            request = readSendRequest(client);
            if (request.getHeaders().containsKey("Connection")) {
                if (request.isUpgradingConnection()) {
                    //do WS
                    readSendSocket(client);
                }
            }

        } catch (SocketException e) {
            LOG.warn("Disconnected client by a Socket error, probably disconnected by user.");
        } catch (IOException e) {
            LOG.warn("Disconnected client by Input output error");
        } catch (Exception e) {
            LOG.warn("Disconnected client by a general exception.", e);
        } catch (Throwable e) {
            LOG.warn("Disconnected client by a Throwable exception!", e);
        } finally {
            LOG.info("End of request on [" + Thread.currentThread().getName() + "]");

            try {

                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.flush();
                    out.close();
                }

                if (request != null && !request.getHeaders().isEmpty() &&
                        request.getHeaders().containsKey("Connection")
                    //&& request.getHeaders().get("Connection").equals("close")
                ) {
                    client.getClientSocket().close();
                    removeConnection(client);
                }

                if (in == null && out == null) {
                    removeConnection(client);
                }

            } catch (IOException e) {
                LOG.warn("[ Error ] IOException, socket is closed:", e);
            }
        }
    }


    /**
     * Read Websocket connection.
     *
     * @param client stream
     *               Read bits 9-15 (inclusive) and interpret that as an unsigned integer. If it's 125 or less, then that's the length; you're done. If it's 126, go to step 2. If it's 127, go to step 3.
     *               Read the next 16 bits and interpret those as an unsigned integer. You're done.
     *               Read the next 64 bits and interpret those as an unsigned integer (The most significant bit MUST be 0). You're done.
     *               https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers
     */
    private void readSendSocket(Client client) throws IOException {
        SocketMessageParser messageParser = new SocketMessageParser();

        int i;
        client.setKeepConnected(true);
        OutputStream out = client.getClientSocket().getOutputStream();
        InputStream in = client.getClientSocket().getInputStream();
        Stack<Frame> frameStack = client.getFrameStack();
        StringBuffer messageBuffer = client.getMessageBuffer();

        while ((i = in.read()) != -1 && client.isKeepConnected()) {

            messageParser.parseMessage(i);
            if (messageParser.getState() == END_FRAME) {
                String message = null;
                client.getFrameStack().add(messageParser.getFrame());
                messageParser.reset();
                Frame f = frameStack.peek();

                if (f.isFinMessage()) {

                    //Text handling of Message.
                    if (f.getOpCode().isText() && !frameStack.isEmpty()) {

                        while (!frameStack.isEmpty()) {
                            messageBuffer.append(frameStack.pop().getDecodedMessage());
                        }

                        message = messageBuffer.toString();
                        client.clear(messageBuffer);
                    }

                    if (f.getOpCode().isControlFrame() &&
                            f.getOpCode().isClose()) {

                        messageBuffer.append(frameStack.pop().getDecodedMessage());
                        message = messageBuffer.toString();

                        if (f.getOpCode().isPing()) {
                            f.setOpCode(PONG);
                        }
                    }


                    if (message != null) {
                        writeOutputStream(SocketResponse.createSocketResponse(message, f.getOpCode()), out);
                    }

                    //close connection should be with
                    if ("/c exit".equals(message)) {
                        client.setKeepConnected(false);
                    }
                }
            }
        }
    }


    /**
     * Read and parse characters from the stream, at the same time.
     */
    @Nonnull
    private HttpRequest readInputStream(@Nonnull InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpParser requestParser = new HttpParser();
        HttpRequest request = requestParser.getRequest();
        HttpState state = request.getState();

        while (!state.isErrorState() && !state.isDone()) {
            try {
                request = requestParser.getRequest();
                char c = (char) reader.read();
                requestParser.nextCharacter(c);
                state = request.getState();
            } catch (IOException ex) {
                request.setState(HttpState.ERROR);
                request.setStatusCode(BAD_REQUEST);
                break;//on error escape.
            }
        }

        return request;
    }

    public HttpRequest readSendRequest(Client client) throws IOException {
        HttpResponseHandler responseHandler = null;

        //Initial request.
        HttpRequest request = readInputStream(client.getClientSocket().getInputStream());
        requestHandler.handleSocketHandlers(request, client.getClientSocket());
        responseHandler = requestHandler.handleRequest(request);

        if (responseHandler.getHttpVersion() != null &&
                responseHandler.getHttpVersion().equals("1.1") &&
                request.getHeaders().get("Connection").equals("keep-alive")) {
            responseHandler.addHeader("Connection", "keep-alive");
        }

        OutputStream out = client.getClientSocket().getOutputStream();
        out.write(responseHandler.getResponse().getBytes(UTF_8));

        return request;
    }

    //write by only 1 output at the time.
    public void writeOutputStream(byte[] bytes, OutputStream out) throws IOException {
        if (out != null) {
            out.write(bytes);
            out.flush();
        }
    }

    public void removeConnection(Client client) {
        connections.remove(client);
    }

    /**
     * Get the current connections.
     *
     * @return List of all connections<Client> containing
     */
    public List<Client> getConnections() {
        return connections;
    }

    public void addConnection(Client client) {
        connections.add(client);
    }
}
