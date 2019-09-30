package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.connection.objectPoolTest.ObjectPool;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.models.HttpParser;
import com.jasper.model.socket.models.SocketMessageParser;
import com.jasper.model.socket.models.SocketResponse;
import com.jasper.model.socket.models.entity.Frame;

import javax.annotation.Nonnull;
import java.io.*;
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
public class ConnectionManager {

    private Controller controller;
    private RequestHandler requestHandler = null;
    ExecutorService executor;

    public ConnectionManager(Controller controller) {
        this.controller = controller;
        executor = Executors.newWorkStealingPool();
    }

    public void addConnection(Client client) {
        executor.execute(new ConnectionHandler(client, controller));
    }

    public void tearDown() {
        executor.shutdown();
    }

}
