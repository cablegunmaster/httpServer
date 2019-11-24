package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 * All the operation done on the object.
 */
public class ConnectionManager {

    private Controller controller;
    private ExecutorService executor;

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
