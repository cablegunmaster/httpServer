package com.jasper.model;

import com.jasper.model.request.RequestHandler;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 * All the operation done on the object.
 */
public class Model {

    private CopyOnWriteArrayList<ClientWorkerRunnable> connections = new CopyOnWriteArrayList<>();
    private HashMap<String, RequestHandler> getMap;
    private HashMap<String, RequestHandler> postMap;

    /**
     * Get the current connections.
     *
     * @return List of all connections<ClientWorkerRunnable> containing
     */
    public List<ClientWorkerRunnable> getConnections() {
        return connections;
    }

    public void setGetMapping(HashMap<String, RequestHandler> getMap) {
        this.getMap = getMap;
    }

    public void setPostMapping(HashMap<String, RequestHandler> postMap) {
        this.postMap = postMap;
    }

    public HashMap getGetMap() {
        return getMap;
    }

    public HashMap getPostMap() {
        return postMap;
    }
}
