package com.jasper.model;

import com.jasper.controller.Controller;
import com.jasper.model.http.HttpResponse;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.enums.RequestType;
import com.jasper.model.http.models.HttpParser;
import com.jasper.model.http.upgrade.UpgradeHttpResponse;
import com.jasper.model.socket.models.SocketMessageParser;
import com.jasper.model.socket.models.SocketResponse;
import com.jasper.model.socket.models.entity.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Stack;

import static com.jasper.model.http.enums.SocketMessageState.END_FRAME;
import static com.jasper.model.http.enums.StatusCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 */
public class Client implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(Client.class);

    private Socket clientSocket;
    private Controller controller;
    private OutputStream out;
    private InputStream in;
    private BufferedReader reader = null;
    private HttpRequest request;
    private Stack<Frame> frameStack = new Stack<>();
    private StringBuffer messageBuffer = new StringBuffer();

    public Client(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
    }

    @Override
    public void run() {
        LOG.debug("Connecting on [" + Thread.currentThread().getName() + "]");

        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            request = readSendRequest(in, out, clientSocket);
            //String connection = request.getHeaders().getOrDefault("Connection", "close"); //TODO CHECk what this is for?

            if (request.getHeaders().containsKey("Connection")) {
                if (request.isUpgradingConnection()) {
                    //do WS
                    readSendSocket(in, out, clientSocket);
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
                controller.removeConnection(this);

                in.close();
                out.flush();
                out.close();

                if (!request.getHeaders().isEmpty() &&
                        request.getHeaders().containsKey("Connection") &&
                        request.getHeaders().get("Connection").equals("close")) {
                    clientSocket.close();
                }


            } catch (IOException e) {

                LOG.warn("IOEXCEPTION:", e);
                controller.addStringToLog("[ Error ] IOException, socket is closed");
            }
        }
    }

    /**
     * Read Websocket connection.
     *
     * @param in
     * @param out
     * @param clientSocket Read bits 9-15 (inclusive) and interpret that as an unsigned integer. If it's 125 or less, then that's the length; you're done. If it's 126, go to step 2. If it's 127, go to step 3.
     *                     Read the next 16 bits and interpret those as an unsigned integer. You're done.
     *                     Read the next 64 bits and interpret those as an unsigned integer (The most significant bit MUST be 0). You're done.
     *                     https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers
     */
    private void readSendSocket(InputStream in, OutputStream out, Socket clientSocket) throws IOException {


//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        //WS protocol read byte 1,2,3?

        SocketMessageParser messageParser = new SocketMessageParser();

        int i;
        while ((i = in.read()) != -1) {

            messageParser.parseMessage(i);
            if (messageParser.getState() == END_FRAME) {
                frameStack.add(messageParser.getFrame());
                messageParser.reset();

                Frame f = frameStack.peek();

                //Text handling of Message.
                if (f.isFinMessage() && f.getOpCode().isText()) {
                    Frame frame1 = frameStack.pop();
                    messageBuffer.append(frame1.getDecodedMessage());

                    //has continuation frame.
                    if (!frameStack.isEmpty()) {
                        while (!frameStack.isEmpty() &&
                                frameStack.peek().getOpCode().isContinuation()) {
                            Frame frame2 = frameStack.pop();
                            messageBuffer.append(frame2.getDecodedMessage());
                        }
                    }
                }

                String message = messageBuffer.toString();
                if (message.equals("/c exit")) {
                    break;
                }

                out.write(SocketResponse.createSocketResponse(messageBuffer.toString()));
                clear(messageBuffer);
            }
            //keep in this loop until it needs to be ended, really test it thoroughly.
        }
    }

    public HttpRequest readSendRequest(InputStream in, OutputStream out, Socket clientSocket) throws IOException {
        HttpResponseHandler responseHandler = null;

        //Initial request.
        HttpRequest request = readInputStream(in);
        handleSocketHandlers(request, clientSocket);
        responseHandler = handleRequestHandlers(request);

        if (responseHandler.getHttpVersion() != null &&
                responseHandler.getHttpVersion().equals("1.1") &&
                request.getHeaders().get("Connection").equals("keep-alive")) {
            responseHandler.addHeader("Connection", "keep-alive");
        }
        out.write(responseHandler.getResponse().getBytes(UTF_8));
        return request;
    }

    /**
     * Read and parse characters from the stream, at the same time.
     */
    @Nonnull
    private HttpRequest readInputStream(@Nonnull InputStream inputStream) {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

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

    private void handleSocketHandlers(@Nonnull HttpRequest request, @Nonnull Socket clientSocket) throws SocketException {
        switch (request.getStatusCode()) {
            case SWITCHING_PROTOCOL:
                clientSocket.setSoTimeout(0); //timeout to make a clientsocket Idle.
                break;
            default:
                clientSocket.setSoTimeout(500000); //timeout to make a clientsocket Idle.
                clientSocket.setSoLinger(true, 100000); //timeout to close the socket.
                break;
        }
    }

    @Nonnull
    public HttpResponseHandler handleRequestHandlers(@Nonnull HttpRequest request) throws UnsupportedEncodingException {
        HttpResponseHandler response = getHandlerByRequest(request);
        RequestHandler handler = getHandlerByRequestMethod(request);

        HttpState state = request.getState();
        if (handler != null) {
            handler.handle(request, response);
            response.setStatusCode(ACCEPTED);

            if (request.getUpgradeSecureKeyAnswer() != null) {
                response.setWebsocketAcceptString(request.getUpgradeSecureKeyAnswer());

                if (request.getHeaders().containsKey("Sec-WebSocket-Protocol")) {
                    response.addHeader("Sec-WebSocket-Protocol",
                            request.getHeaders().get("Sec-WebSocket-Protocol"));
                }
            }

        } else if (state.isErrorState()) {
            response.setStatusCode(request.getStatusCode());
        } else {
            response.setStatusCode(NOT_FOUND);
        }

        response.setHttpVersion(request.getHttpVersion());
        return response;
    }

    @Nonnull
    private HttpResponseHandler getHandlerByRequest(@Nonnull HttpRequest request) {
        if (request.getStatusCode() == SWITCHING_PROTOCOL) {
            return new UpgradeHttpResponse();
        }
        return new HttpResponse();
    }

    @CheckForNull
    private RequestHandler getHandlerByRequestMethod(HttpRequest request) {
        RequestHandler handler = null;

        if (request.getPath() != null) {
            if (controller.getSocketMap() != null &&
                    controller.getSocketMap().containsKey(request.getPath()) &&
                    request.getStatusCode() == SWITCHING_PROTOCOL) {
                handler = controller.getSocketMap().get(request.getPath());
            }

            if (request.getRequestMethod().equals(RequestType.GET) &&
                    controller.getGetMap() != null &&
                    controller.getGetMap().containsKey(request.getPath())) {
                handler = controller.getGetMap().get(request.getPath());
            }

            if (request.getRequestMethod().equals(RequestType.POST) &&
                    controller.getPostMap() != null &&
                    controller.getPostMap().containsKey(request.getPath())) {
                handler = controller.getPostMap().get(request.getPath());
            }
        }
        return handler;
    }

    private void clear(StringBuffer s) {
        s.setLength(0);
    }
}
