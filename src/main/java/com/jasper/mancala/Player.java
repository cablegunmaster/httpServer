package com.jasper.mancala;

import com.jasper.model.Client;

public class Player {
    public int playerNumber;
    public int move;
    public Client client;

    public Player(Client client) {
        this.client = client;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Integer getMove() {
        return move;
    }

    public void setMove(Integer move) {
        this.move = move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
