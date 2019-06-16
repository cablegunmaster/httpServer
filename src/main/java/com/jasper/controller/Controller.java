package com.jasper.controller;

import com.jasper.model.Client;
import com.jasper.model.Model;
import com.jasper.model.MultiThreadedServer;
import com.jasper.model.request.RequestHandler;
import com.jasper.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 */
public class Controller {

    private final static Logger LOG = LoggerFactory.getLogger(Controller.class);
    private static Boolean restartProcess = false;
    private Boolean guiVisible;

    private Model model;
    private View view;

    private MultiThreadedServer multiThreadedServer;
    private Thread multiThreadedServerThread;
    private int portNumber;

    private Map<String, RequestHandler> getMap;
    private Map<String, RequestHandler> postMap;
    private Map<String, RequestHandler> socketMap;

    /**
     * Java.Controller class
     *
     * @param portNumber portNumber
     */
    public Controller(Integer portNumber,
                      Map<String, RequestHandler> getMap,
                      Map<String, RequestHandler> postMap,
                      Map<String, RequestHandler> socketMap,
                      Boolean guiVisible) {

        this.guiVisible = guiVisible;

        if (guiVisible) {
            this.view = new View();
        }

        this.model = new Model();
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
            multiThreadedServer.setStopped(true);
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
        model.addConnection(client);
    }

    /**
     * Remove a single connection from the Connection list.
     *
     * @param client Connection removal.
     */
    public void removeConnection(Client client) {
        LOG.debug("removed connection.");
        model.getConnections().remove(client);
    }

    @Nonnull
    public Model getModel() {
        return model;
    }

    @CheckForNull
    public Map<String, RequestHandler> getGetMap() {
        return getMap;
    }

    public Map<String, RequestHandler> getPostMap() {
        return postMap;
    }

    public Map<String, RequestHandler> getSocketMap() {
        return socketMap;
    }
}
