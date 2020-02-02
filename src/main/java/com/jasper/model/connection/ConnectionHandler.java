package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.file.FileLoader;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.enums.StatusCode;
import com.jasper.model.http.models.HttpParser;
import com.jasper.model.socket.SocketResponse;
import com.jasper.model.socket.models.SocketMessageParser;
import com.jasper.model.socket.models.entity.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static com.jasper.model.http.enums.SocketMessageState.END_FRAME;
import static com.jasper.model.http.enums.StatusCode.BAD_REQUEST;
import static com.jasper.model.socket.enums.OpCode.PONG;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ConnectionHandler implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private final Client client;
    private static List<Client> clientList = Collections.synchronizedList(new ArrayList<Client>()); //threadsafe list.

    private RequestHandler requestHandler;

    ConnectionHandler(Client client, Controller controller) {
        this.requestHandler = new RequestHandler(controller);
        this.client = client;
    }

    @Override
    public void run() {
        clientList.add(client);
        processClient(client);
    }

    private void processClient(Client client) {
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

            clientList.remove(client);

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
    private void readSendSocket(@Nonnull Client client) throws IOException {
        SocketMessageParser messageParser = new SocketMessageParser();
        client.setKeepConnected(true);

        Socket socket = client.getClientSocket();
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        Stack<Frame> frameStack = client.getFrameStack();
        StringBuffer messageBuffer = client.getMessageBuffer();

        int i;
        while ((i = in.read()) != -1 && client.isKeepConnected()) {

            messageParser.parseMessage(i);
            if (messageParser.getState() == END_FRAME) {
                String message = null;

                client.getFrameStack().add(messageParser.getFrame());
                messageParser.reset();

                Frame frame = frameStack.peek();
                if (frame.isFinMessage()) {

                    if (frame.getOpCode().isText() && !frameStack.isEmpty()) {
                        while (!frameStack.isEmpty()) {
                            messageBuffer.append(frameStack.pop().getDecodedMessage());
                        }

                        message = messageBuffer.toString();
                        client.clear(messageBuffer);
                    }

                    if (frame.getOpCode().isControlFrame() && frame.getOpCode().isClose()) {
                        messageBuffer.append(frameStack.pop().getDecodedMessage());
                        message = messageBuffer.toString();
                        if (frame.getOpCode().isPing()) {
                            frame.setOpCode(PONG);
                        }
                    }

                    if ("disconnect".equals(message)) {
                        client.setKeepConnected(false);
                    } else if (message != null) {
                        //Echo writer, write the SAME message back.

                        for (Client chat : clientList) {

                            //only send to other people
                            if(!chat.getClientSocket().equals(socket)) {
                                writeOutputStream(SocketResponse.createSocketResponse(message, frame.getOpCode()), chat.getClientSocket().getOutputStream());
                            }
                        }
                    }
                }
            }
        }
    }

    //write by only 1 output at the time.
    private void writeOutputStream(byte[] bytes, OutputStream out) throws IOException {
        if (out != null) {
            out.write(bytes);
            out.flush();
        }
    }

    private HttpRequest readSendRequest(Client client) throws IOException {
        //Initial request.
        HttpRequest request = readInputStream(client.getClientSocket().getInputStream());
        OutputStream out = client.getClientSocket().getOutputStream();

        requestHandler.handleSocketHandlers(request, client.getClientSocket());
        HttpResponseHandler responseHandler = requestHandler.handleRequest(request);
        if (responseHandler.getHttpVersion() != null &&
                responseHandler.getHttpVersion().equals("1.1") &&
                request.getHeaders().get("Connection").equals("keep-alive")) {
            responseHandler.addHeader("Connection", "keep-alive");
        }

        //LOAD CSS AND JS Files.
        if (isFileRequestToBeAttached(request)) {
            String file = FileLoader.loadFile(request.getPath());

            if (file.length() != 0) {
                responseHandler.setStatusCode(StatusCode.OK);
                responseHandler.setHeaders(request.getHeaders());
                responseHandler.setContentType(getContentType(request.getPath()));
                responseHandler.overWriteBody(file);
            }
        }


        out.write(responseHandler.getResponse().getBytes(UTF_8));
        return request;
    }

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
    @Nonnull
    private String getContentType(String path) {
        String mimeType = "";
        switch (getExtension(path)) {
            case "png":
                mimeType = "image/png";
                break;
            case "css":
                mimeType = "text/css";
                break;
            case "jpeg":
            case "jpg":
                mimeType = "image/jpeg";
                break;
            case "js":
                mimeType = "text/javascript";
                break;
            case "html":
                mimeType = "text/html; charset=utf-8";
                break;
            default:
                //  is the default value for all other cases. An unknown file type should use this type.
                //  Browsers pay a particular care when manipulating these files,
                //  attempting to safeguard the user to prevent dangerous behaviors.
                mimeType = "application/octet-stream";
                break;
        }
        return mimeType;
    }

    public String getExtension(String path) {
        String[] filesplit = path.split("\\.");
        if (filesplit.length > 1) {
            return filesplit[filesplit.length - 1];
        } else {
            return null;
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

    public boolean isFileRequestToBeAttached(HttpRequest request) {
        return !request.getPath().isEmpty() &&
                !request.getPath().equals("/") &&
                !request.isUpgradingConnection();
    }

}
