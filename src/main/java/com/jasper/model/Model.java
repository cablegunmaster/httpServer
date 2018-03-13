package com.jasper.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 * All the operation done on the object.
 */
public class Model {

    private List<ClientWorkerRunnable> connections = new ArrayList<ClientWorkerRunnable>(); //all connections.
    private List<String> currentUserList; //string of users.
    private HashMap<String, String> duelList = new HashMap<>();

    /**
     * Reset the connections.
     */
    public void resetConnections() {
        if (connections.size() > 0) {
            for (ClientWorkerRunnable connection : connections) {
                if (connection.clientSocket.isConnected()) {

                    try {
                        connection.clientSocket.close();
                        connection.clientSocket = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            connections = new ArrayList<ClientWorkerRunnable>();
        }
    }

    public Model() {
    }

    /**
     * Gives back a list of all connected users.
     * @return List<String>
     */
    public List<String> getConnectedPersons() {
        return currentUserList;
    }

    /**
     * Remove a ClientWorkerRunnable from the list of currentConnections.
     * @param connection to be killed.
     */
    public synchronized void removeConnection(ClientWorkerRunnable connection) {
        connections.remove(connection);
    }

    /**
     * Get the current connections.
     * @return List of all connections<ClientWorkerRunnable> containing
     */
    public List<ClientWorkerRunnable> getConnections() {
        return connections;
    }


    /**
     * @return the duelList
     */
    public synchronized HashMap<String, String> getDuelList() {
        return duelList;
    }

    /**
     * Duel consist of a player who starts the duel and the player it wants to duel.
     * @param playerFrom a String containing the player.
     * @param game       the game like TicTacToe
     * @param playerTo   the player String which is actually online
     */
    public synchronized void insertDuel(String playerFrom, String game, String playerTo) {
        duelList.put(playerFrom + ":" + game, playerTo);
    }
}
