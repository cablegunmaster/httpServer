package com.jasper.game.mancala.controller;

import com.jasper.game.IGame;
import com.jasper.game.Player;
import com.jasper.model.Request;

import javax.annotation.Nonnull;

public class GameControllerPassivePlayer extends GameController {


    public String actionController(String prefix,
                                   String[] args,
                                   @Nonnull IGame game,
                                   @Nonnull Request request) {

        StringBuilder output = new StringBuilder();
        Player player = game.getPlayerByRequest(request);

        if (player != null && gameMap.containsKey(player)) {
            String action = getAction(prefix, args);

            switch (action) {
                case "create":
                    output.append("Game")
                            .append(game.getName())
                            .append("number: ")
                            .append(getGameNumber())
                            .append(" is hosted <br/> by another player you can join");
                    break;
                case "join":
                    output.append("game is joined by another player");
                    break;
                case "turn":
                    output.append(appendPlayerTurn(player));
                    break;
                case "move":
                    if (game.getPlayerTurn() == player.getPlayerNumber()) {
                        appendPlayerTurn(player);
                        output.append(addBoardContent(game));
                    }
                    break;
                default:
                    break;
            }
        }
        return output.toString();
    }

    private String getAction(String prefix, String[] args) {
        return args[0].substring(prefix.length()).toLowerCase();
    }
}
