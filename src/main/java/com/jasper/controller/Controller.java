package com.jasper.controller;

import com.jasper.model.Client;
import com.jasper.model.IRequestHandler;
import com.jasper.model.MultiThreadedServer;
import com.jasper.model.connection.ConnectionManager;
import com.jasper.view.View;

import javax.annotation.Nonnull;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 */
public class Controller {

    private MultiThreadedServer multiThreadedServer;
    private Thread multiThreadedServerThread;
    private int portNumber;

    private Map<String, IRequestHandler> getMap;
    private Map<String, IRequestHandler> postMap;
    private Map<String, IRequestHandler> socketMap;

    private static Boolean isRestartProcessLocked = false;
    private boolean guiVisible = false;

    private static ConnectionManager connectionManager;
    private View view;

    /**
     * Java.Controller class
     *
     * @param portNumber portNumber
     */
    public Controller(Integer portNumber,
                      @Nonnull Map<String, IRequestHandler> getMap,
                      @Nonnull Map<String, IRequestHandler> postMap,
                      @Nonnull Map<String, IRequestHandler> socketMap,
                      boolean guiVisible) {

        if (guiVisible) {
            this.guiVisible = true;
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
            view.getLogTextArea().setCaretPosition(view.getLogTextArea().getDocument().getLength());
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

            if (!isRestartProcessLocked) {
                isRestartProcessLocked = true;
                if (multiThreadedServer != null) {
                    stopMultiThreadedServer(multiThreadedServer);
                    multiThreadedServer = null; //reset the server as well.

                }

                stopMultiServerThread();
                startServer(portNumber);
                isRestartProcessLocked = false;
            }
        };
    }

    private void stopMultiServerThread() {
        try {
            multiThreadedServerThread.join();
            multiThreadedServerThread = null;
        } catch (InterruptedException e) {
            e.getStackTrace();
        }
    }

    /**
     * Stop the server.
     *
     * @param multiThreadedServer server multithreaded object.
     */
    private void stopMultiThreadedServer(MultiThreadedServer multiThreadedServer) {
        try {
            //end the server.
            multiThreadedServer.setRunning(false);
            if (multiThreadedServer.getServerSocket() != null) {
                multiThreadedServer.getServerSocket().close();
                multiThreadedServer.ShutdownConnectionManager();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        view.refresh();
    }

    /**
     * Stop application.
     *
     * @return Stop actinlistener
     */
    @Nonnull
    private ActionListener getStopListener() {
        return actionEvent -> System.exit(0);
    }

    /**
     * Add 'clientWorkerRunnable' connections to the list.
     */
    public void addConnection(@Nonnull Client client) {
        connectionManager.addConnection(client);
    }

    @Nonnull
    public Map<String, IRequestHandler> getGetMap() { return getMap; }

    @Nonnull
    public Map<String, IRequestHandler> getPostMap() {
        return postMap;
    }

    @Nonnull
    public Map<String, IRequestHandler> getSocketMap() {
        return socketMap;
    }
}
