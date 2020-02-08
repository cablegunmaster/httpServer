package com.jasper.game;

import com.jasper.model.Request;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Player {

    private Request client;
    private int playerNumber;

    public Player(Request client, int playerNumber) {
        this.client = client;
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
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
                Objects.equals(client, player.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber, client);
    }
}
