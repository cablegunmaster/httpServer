package com.jasper.main;

import com.jasper.controller.Controller;
import com.jasper.model.IRequestHandler;

import java.util.HashMap;

public class Server {

    private int port = 8081;

    private HashMap<String, IRequestHandler> getMap = new HashMap<>();
    private HashMap<String, IRequestHandler> postMap = new HashMap<>();
    private HashMap<String, IRequestHandler> socketMap = new HashMap<>();
    private Boolean guiVisible = false;

    public Server(int portNumber) {
        port = portNumber;
    }

    public void start() {
        new Controller(getPortNumber(), getMap, postMap, socketMap, getGuiVisible());
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPortNumber() {
        return port;
    }

    public void get(String url, IRequestHandler o) {
        getMap.put(url, o);
    }

    public void post(String url, IRequestHandler o) {
        postMap.put(url, o);
    }

    public void socket(String url, IRequestHandler o) {
        socketMap.put(url, o);
    }

    public void setGUIVisible(Boolean guiVisible) {
        this.guiVisible = guiVisible;
    }

    public Boolean getGuiVisible() {
        return guiVisible;
    }
}
