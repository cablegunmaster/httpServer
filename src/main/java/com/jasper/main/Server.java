package com.jasper.main;

import com.jasper.controller.Controller;
import com.jasper.model.request.RequestHandler;
import java.util.HashMap;

public class Server {

    private int port = 8081;

    private HashMap<String, RequestHandler> getMap = new HashMap<>();
    private HashMap<String, RequestHandler> postMap = new HashMap<>();
    private HashMap<String, RequestHandler> socketMap = new HashMap<>();

    public Server(int portNumber) {
        port = portNumber;
    }

    public void start() {
        new Controller(getPortNumber(), getMap, postMap);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPortNumber() {
        return port;
    }

    public void get(String url, RequestHandler o) {
        getMap.put(url, o);
    }

    public void post(String url, RequestHandler o) {
        postMap.put(url, o);
    }

    public void socket(String url, RequestHandler o) {
        socketMap.put(url, o);
    }
}
