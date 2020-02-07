package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.HttpRequest;
import com.jasper.model.IRequestBuilder;
import com.jasper.model.file.FileLoader;
import com.jasper.model.http.HttpResponse;
import com.jasper.model.http.HttpResponseBuilder;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.enums.StatusCode;
import com.jasper.model.http.models.HttpRequestParser;
import com.jasper.model.http.upgrade.UpgradeHttpResponse;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;


public class HttpRequestHandler {

    private Controller controller;

    public HttpRequestHandler(Controller controller) {
        this.controller = controller;
    }

    public HttpRequest getHttpRequestFromSocket(Socket socket) throws IOException {
        return readInputStream(socket.getInputStream());
    }

    @Nonnull
    public HttpResponseBuilder handleHttpRequest(HttpRequest httpRequest) throws IOException {
        HttpState state = httpRequest.getState();

        HttpResponseBuilder response = null;
        IRequestBuilder handler = getHandlerByRequestMethod(httpRequest);

        if (handler != null) {
            response = getUpgradeResponse(httpRequest);
            response.setStatusCode(StatusCode.ACCEPTED);
            response.setHttpVersion(httpRequest.getHttpVersion());
            addTimeout(httpRequest);
            addUpgradeSecureKey(httpRequest, response);
            addKeepAliveHeader(response, httpRequest.getHeaders());

            handler.handle(httpRequest, response);

        } else if (state.isErrorState()) {
            httpRequest.setStatusCode(httpRequest.getStatusCode());
        } else {
            httpRequest.setStatusCode(StatusCode.NOT_FOUND);
        }
        return response;
    }

    private void addUpgradeSecureKey(HttpRequest request, HttpResponseBuilder response) {
        if (request.getUpgradeSecureKeyAnswer() != null) {
            response.setWebsocketAcceptString(request.getUpgradeSecureKeyAnswer());
        }

        if (request.hasHeader("Sec-WebSocket-Protocol")) {
            response.addHeader("Sec-WebSocket-Protocol",
                    request.getHeaders().get("Sec-WebSocket-Protocol"));
        }
    }

    @CheckForNull
    private IRequestBuilder getHandlerByRequestMethod(HttpRequest request) {
        IRequestBuilder handler = null;

        if (request.getPath() != null) {
            String path = request.getPath();

            switch (request.getRequestMethod()) {
                case GET:
                    if (request.getStatusCode().isSwitchingProtocol() &&
                            controller.getSocketMap().containsKey(path)) {
                        handler = controller.getSocketMap().get(path);
                    } else if (controller.getGetMap().containsKey(path)) {
                        handler = controller.getGetMap().get(path);
                    }
                    break;
                case POST:
                    if (controller.getPostMap().containsKey(path)) {
                        handler = controller.getPostMap().get(path);
                    }
                    break;
                default:
                    handler = getHandlerByRequestMethod(request);
                    break;
            }
        } else {
            //throw 404 here?
        }
        return handler;
    }


    void addTimeout(@Nonnull HttpRequest request) throws SocketException {
        Socket socket = request.getSocket();
        if (request.getStatusCode() == StatusCode.SWITCHING_PROTOCOL) {
            socket.setSoTimeout(0); //timeout to make a socket Idle.
        } else {
            socket.setSoTimeout(500000); //timeout to make a socket Idle.
            socket.setSoLinger(true, 100000); //timeout to close the socket.
        }
    }

    @Nonnull
    private HttpResponseBuilder getUpgradeResponse(@Nonnull HttpRequest request) {
        if (request.getStatusCode() == StatusCode.SWITCHING_PROTOCOL) {
            return new UpgradeHttpResponse();
        }
        return new HttpResponse();
    }

    /**
     * Read and parse characters from the stream, at the same time.
     */
    @Nonnull
    private HttpRequest readInputStream(@Nonnull InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestParser requestParser = new HttpRequestParser();

        HttpRequest request = requestParser.getRequest();
        HttpState state = request.getState();

        while (!state.isErrorState() &&
                !state.isDone()) {
            try {
                request = requestParser.getRequest();
                char c = (char) reader.read();
                requestParser.nextCharacter(c);
                state = request.getState();
            } catch (IOException ex) {
                request.setState(HttpState.ERROR);
                request.setStatusCode(StatusCode.BAD_REQUEST);
                break;//on error escape.
            }
        }

        return request;
    }

    private void addKeepAliveHeader(@Nonnull HttpResponseBuilder responseHandler,
                                    @Nonnull Map<String, String> headers) {
        if (responseHandler.getHttpVersion() != null &&
                responseHandler.getHttpVersion().equals("1.1") &&
                headers.containsKey("Connection") &&
                headers.get("Connection").equals("keep-alive")) {
            responseHandler.addHeader("Connection", "keep-alive");
        }
    }

    //seperate class?
    public boolean isFileRequestedByPath(HttpRequest request) {
        return !request.getPath().isEmpty() &&
                !request.getPath().equals("/") &&
                !request.isUpgradingConnection();
    }

    //TODO debug this function to see if its working.
    private void addFileToHttpRequest(HttpResponseBuilder responHand, HttpRequest request) {
        String file = FileLoader.loadFile(request.getPath());

        if (file.length() != 0) {
            responHand.setStatusCode(StatusCode.OK);
            responHand.setHeaders(request.getHeaders());
            responHand.setContentType(getContentType(request.getPath()));
            responHand.overWriteBody(file);
        }
    }

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
    @Nonnull
    private String getContentType(String path) {
        String mimeType;

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
}
