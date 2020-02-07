package com.jasper.game;

import com.jasper.model.Request;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Player {
    private int playerNumber;
    private int move;

    private Request client;

    public Player(Request client, int playerNumber) {
        this.client = client;
        this.playerNumber = playerNumber;
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

    @Nonnull
    public Request getClient() {
        return client;
    }

    public void setClient(Request client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerNumber == player.playerNumber &&
                move == player.move &&
                Objects.equals(client, player.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber, move, client);
    }
}
