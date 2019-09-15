package com.jasper.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 * All the operation done on the object.
 */
public class connectionManager {

    private CopyOnWriteArrayList<Client> connections = new CopyOnWriteArrayList<>();

    /**
     * Get the current connections.
     *
     * @return List of all connections<Client> containing
     */
    public List<Client> getConnections() {
        return connections;
    }

    public void addConnection(Client client){
        connections.add(client);
    }

    public void removeConnection(Client client){
        connections.remove(client);
    }

}
