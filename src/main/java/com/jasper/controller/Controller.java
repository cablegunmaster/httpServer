package com.jasper.controller;

import com.jasper.model.Client;
import com.jasper.model.connection.ConnectionManager;
import com.jasper.model.MultiThreadedServer;
import com.jasper.model.IRequestHandler;
import com.jasper.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 */
public class Controller {

    private final static Logger LOG = LoggerFactory.getLogger(Controller.class);
    private static Boolean restartProcess = false;
    private Boolean guiVisible;

    private static ConnectionManager connectionManager;
    private View view;

    private MultiThreadedServer multiThreadedServer;
    private Thread multiThreadedServerThread;
    private int portNumber;

    private Map<String, IRequestHandler> getMap;
    private Map<String, IRequestHandler> postMap;
    private Map<String, IRequestHandler> socketMap;

    /**
     * Java.Controller class
     *
     * @param portNumber portNumber
     */
    public Controller(Integer portNumber,
                      Map<String, IRequestHandler> getMap,
                      Map<String, IRequestHandler> postMap,
                      Map<String, IRequestHandler> socketMap,
                      Boolean guiVisible) {

        this.guiVisible = guiVisible;

        if (guiVisible) {
            this.view = new View();
        }

        connectionManager = new ConnectionManager(this);
        this.getMap = getMap;
        this.postMap = postMap;
        this.socketMap = socketMap;

        this.portNumber = portNumber;

        setListeners();
        startServer(this.portNumber);
    }

    /**
     * Set all listeners.
     */
    private void setListeners() {
        if (guiVisible) {
            //Build up all the actions for the current view.
            view.getRestartMenuItem().addActionListener(getRestartListener());
            view.getstopMenuItem().addActionListener(getStopListener());
        }
    }

    /**
     * Add a String to the VIEW
     *
     * @param line String value put in the view.
     */
    public synchronized void addStringToLog(String line) {
        if (guiVisible) {
            view.getLogTextArea().append(line + "\r\n");
            int len = view.getLogTextArea().getDocument().getLength();

            view.getLogTextArea().setCaretPosition(len);
            view.refresh();
        }
    }

    /**
     * Starting the server, if no server has been started already.
     *
     * @param portNumber Integer
     */
    private synchronized void startServer(Integer portNumber) {

        multiThreadedServer = new MultiThreadedServer(portNumber, this); //gets view to send log messages.

        if (multiThreadedServerThread == null) {
            //add lines to Textarea view.
            addStringToLog("Starting up server. PID: " + ManagementFactory.getRuntimeMXBean().getName());
            multiThreadedServerThread = new Thread(multiThreadedServer);
            multiThreadedServerThread.start();

            addStringToLog("[ OK ] Server is started on portNumber: " + this.portNumber);
        }

    }

    @Nonnull
    private ActionListener getRestartListener() {
        return actionEvent -> {
            //lock to ensure only one proces can restart the server.
            if (!restartProcess) {
                restartProcess = true;
                if (multiThreadedServer != null) {
                    stopMultiThreadedServer(multiThreadedServer);
                    multiThreadedServer = null; //reset the server as well.
                }

                stopMultiServerThread();
                restartProcess = false;
                startServer(portNumber);
            }
        };
    }

    private void stopMultiServerThread() {
        try {
            //deleting leftover variables.
            multiThreadedServerThread.join(); // wait for the thread to stop
            multiThreadedServerThread = null; //this should be the place to disconnect itself.
        } catch (InterruptedException e) {
            e.getStackTrace();
        }
    }

    /**
     * Stop the server.
     *
     * @param multiThreadedServer
     */
    private void stopMultiThreadedServer(MultiThreadedServer multiThreadedServer) {
        try {
            //end the server.
            multiThreadedServer.setRunning(false);
            if (multiThreadedServer.getServerSocket() != null) {
                multiThreadedServer.getServerSocket().close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        view.refresh();
    }

    /**
     * Stop application.
     *
     * @return
     */
    @Nonnull
    private ActionListener getStopListener() {
        return actionEvent -> System.exit(0);
    }

    /**
     * Add 'clientWorkerRunnable' connections to the list.
     *
     * @param client
     */
    public void addConnection(Client client) {
        connectionManager.addConnection(client);
    }

    /**
     * Remove a single connection from the Connection list.
     *
     * @param client Connection removal.
     */
    public void removeConnection(Client client) {
        LOG.debug("removed connection.");
        connectionManager.getConnections().remove(client);
    }

    @Nonnull
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @CheckForNull
    public Map<String, IRequestHandler> getGetMap() {
        return getMap;
    }

    public Map<String, IRequestHandler> getPostMap() {
        return postMap;
    }

    public Map<String, IRequestHandler> getSocketMap() {
        return socketMap;
    }
}
