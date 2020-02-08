package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.HttpRequest;
import com.jasper.model.Request;
import com.jasper.model.SocketRequest;
import com.jasper.model.http.HttpResponseBuilder;
import com.jasper.model.socket.enums.OPCode;
import com.jasper.model.socket.models.SocketMessageParser;
import com.jasper.model.socket.models.entity.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class RequestHandler implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);
    private static List<Request> clientList = Collections.synchronizedList(new ArrayList<>()); //threadsafe list.


    private Request client;
    private HttpRequestHandler req;

    RequestHandler(Request client, Controller controller) {
        this.req = new HttpRequestHandler(controller);
        this.client = client;
    }

    @Override
    public void run() {
        clientList.add(client);
        processRequest(client);
        //sendRequest();
    }

    private void processRequest(Request request) {
        LOG.debug("Connecting on [" + Thread.currentThread().getName() + "]");

        try {
            HttpRequest httpRequest = req.getHttpRequestFromSocket(request.getSocket());
            HttpResponseBuilder responseHandler = req.handleHttpRequest(httpRequest);

            OutputStream out = httpRequest.getSocket().getOutputStream();
            out.write(responseHandler
                    .getResponse()
                    .getBytes(StandardCharsets.UTF_8));

            if (request instanceof SocketRequest &&
                    httpRequest.isUpgradingConnection()) {
                SocketRequest socketRequest = (SocketRequest) request;

                Stack<Frame> frameStack = socketRequest.getFrameStack();
                StringBuffer messageBuffer = socketRequest.getMessageBuffer();
                readSocket(socketRequest, frameStack, messageBuffer);
            }

        } catch (SocketException e) {
            LOG.info("Disconnected client by a Socket error, probably disconnected by user.");
        } catch (IOException e) {
            LOG.info("Disconnected client by Input output error");
        } catch (Exception e) {
            LOG.info("Disconnected client by a general exception.", e);
        } catch (Throwable e) {
            LOG.info("Disconnected client by a Throwable exception!", e);
        } finally {
            LOG.info("End of request on [" + Thread.currentThread().getName() + "]");
            clientList.remove(client);
        }
    }

    /**
     * Read Websocket connection.
     *
     * @param request SocketRequest
     *                Read bits 9-15 (inclusive) and interpret that as an unsigned integer. If it's 125 or less, then that's the length; you're done. If it's 126, go to step 2. If it's 127, go to step 3.
     *                Read the next 16 bits and interpret those as an unsigned integer. You're done.
     *                Read the next 64 bits and interpret those as an unsigned integer (The most significant bit MUST be 0). You're done.
     *                https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers
     */
    private void readSocket(SocketRequest request,
                            Stack<Frame> frameStack,
                            StringBuffer messageBuffer) throws IOException {

        SocketMessageParser messageParser = new SocketMessageParser();
        Socket socket = request.getSocket();
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        request.setKeepConnected(true);

        int i;
        while ((i = in.read()) != -1 && request.isKeepConnected()) {

            messageParser.parseMessage(i);
            if (messageParser.getState().isEndFrame()) {

                request.getFrameStack().add(messageParser.getFrame());
                messageParser.reset();

                Frame frame = frameStack.peek();
                String message = getFrameContent(frame);

                if (frame.isEndMessage() && message != null) {

                    //Frame decoding.

                    if ("disconnect".equals(message)) {
                        client.setKeepConnected(false);
                    }

                    //STATE PATTERN vs Strategy pattern.
                    if (message.startsWith("!")) {
                        //output = mancalaControllerCurrentPlayer.actionController("!", message.split(" "), client);
                        //writeOutputStream(SocketResponse.createSocketResponse(output, frame.getOpCode()), out);
                    }

                    for (Request chat : clientList) {

                        //only send to other people
                        if (message.startsWith("!") && !chat.equals(client)) {
                            //send message to others.
                            //  output = mancalaControllerOtherPlayer.actionController("!", message.split(" "), chat);
                            //writeOutputStream(SocketResponse.createSocketResponse(output, frame.getOpCode()), chat.getSocket().getOutputStream());
                        }

                        //Chat msgs to go for everyone.
                        //if (!chat.getClientSocket().equals(socket) && !message.startsWith("!")) {
                        //  writeOutputStream(SocketResponse.createSocketResponse(message, frame.getOpCode()), chat.getSocket().getOutputStream());
                        //}
                    }
                    //This part above should be refactored
                }
            }
        }
    }

    @CheckForNull
    private String getFrameContent(@Nonnull Frame frame) {
        String message = null;

        OPCode opCode = frame.getOpCode();
        /*if (opCode.isText() && !frameStack.isEmpty()) {
            messageBuffer.append(frameStack.pop().getDecodedMessage());
            message = messageBuffer.toString();
            request.clear(messageBuffer);
        }*/

        if (opCode.isTypeOfControlFrame()) {
            if (frame.getOpCode().isClose()) {
                frame.setOpCode(OPCode.CLOSE);
            }

            if (frame.getOpCode().isPing()) {
                frame.setOpCode(OPCode.PONG);
            }
        }
        return message;
    }

    //write by only 1 output at the time.
    private void writeOutputStream(byte[] bytes, OutputStream out) throws IOException {
        if (out != null) {
            out.write(bytes);
            out.flush();
        }
    }
}
