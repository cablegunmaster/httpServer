package com.jasper.main;

import com.jasper.controller.Controller;
import com.jasper.model.IRequestHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Server {

    private HashMap<String, IRequestHandler> getMap = new HashMap<>();
    private HashMap<String, IRequestHandler> postMap = new HashMap<>();
    private HashMap<String, IRequestHandler> socketMap = new HashMap<>();

    private int port;
    private Boolean guiVisible = false;

    public Server(@Nonnull Integer portNumber) {
        port = portNumber;
    }

    void start() {
        new Controller(port, getMap, postMap, socketMap, guiVisible);
    }

    protected void get(String url, IRequestHandler o) {
        getMap.put(url, o);
    }

    void post(String url, IRequestHandler o) {
        postMap.put(url, o);
    }

    void socket(String url, IRequestHandler o) {
        socketMap.put(url, o);
    }

    void setGUIVisible(boolean guiVisible) {
        this.guiVisible = guiVisible;
    }
}
