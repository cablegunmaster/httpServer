package com.jasper.main;

import com.jasper.controller.Controller;
import com.jasper.model.IRequestBuilder;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Server {

    private HashMap<String, IRequestBuilder> getMap = new HashMap<>();
    private HashMap<String, IRequestBuilder> postMap = new HashMap<>();
    private HashMap<String, IRequestBuilder> socketMap = new HashMap<>();

    private int port;
    private Boolean guiVisible = false;

    public Server(@Nonnull Integer portNumber) {
        port = portNumber;
    }

    void start() {
        new Controller(port, getMap, postMap, socketMap, guiVisible);
    }

    protected void get(String url, IRequestBuilder o) {
        getMap.put(url, o);
    }

    void post(String url, IRequestBuilder o) {
        postMap.put(url, o);
    }

    void socket(String url, IRequestBuilder o) {
        socketMap.put(url, o);
    }

    void setGUIVisible(boolean guiVisible) {
        this.guiVisible = guiVisible;
    }
}
