package com.jasper.game.mancala.controller;

import com.jasper.game.IGame;
import com.jasper.game.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    //GameController to interact with any game which follows the interface.
    public Map<Integer, IGame> serverMap = new HashMap<>();
    public Map<Player, IGame> gameMap = new HashMap<>();

    public String getGameList() {
        StringBuilder sb = new StringBuilder();

        if (serverMap.isEmpty()) {
            sb.append("No games are found");
        }

        for (Integer key : serverMap.keySet()) {
            IGame game = serverMap.get(key);
            sb.append("<br/> GameName: ").
                    append(game.getName())
                    .append(" ")
                    .append(key)
                    .append(" <br/> ");
        }
        return sb.toString();
    }

    String appendPlayerTurn(Player player) {
        return gameMap.get(player).getPlayerTurn() == player.getPlayerNumber() ?
                "Your turn " + player.getPlayerNumber() : "Turn of your opponent";
    }

    int getGameNumber() {
        return (serverMap.size() - 1);
    }

    void makeMove(Player player,
                  IGame game,
                  int move) {
        IGame sessionGame = gameMap.get(player);
        if (sessionGame.isStarted() &&
                game.getPlayerTurn() == player.getPlayerNumber()) {
            game.setMove(move, player);
        }
    }

    String addBoardContent(IGame game) {
        return game.getBoardContent();
    }

    IGame getNewGame(@Nonnull Object game) {
        try {
            return (IGame) Class.forName(
                    game.getClass().getName())
                    .newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
