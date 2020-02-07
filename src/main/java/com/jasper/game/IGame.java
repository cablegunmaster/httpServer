package com.jasper.game;

import com.jasper.model.Request;

public interface IGame {

    String getName();

    void startGame();

    int getPlayerTurn();

    Player getPlayerByRequest(Request request);

    boolean isGameFull();

    boolean isStarted();

    String getHelpMessage();

    void setMove(int move, Player player);

    void addPlayer(Player player);

    boolean contains(Player player);

    String getBoardContent();
}
