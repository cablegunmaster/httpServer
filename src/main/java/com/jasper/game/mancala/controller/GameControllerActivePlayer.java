package com.jasper.game.mancala.controller;

import com.jasper.game.IGame;
import com.jasper.game.Player;
import com.jasper.model.Request;

import javax.annotation.Nonnull;

/**
 * this class facilitates
 * all input from Sockets
 * accepts all incoming strings and
 * translates to strict outgoing strings.
 */
public class GameControllerActivePlayer extends GameController {

    public String actionController(String prefix,
                                   String[] args,
                                   @Nonnull IGame game,
                                   @Nonnull Request request) {

        StringBuilder output = new StringBuilder();
        Player player = game.getPlayerByRequest(request);
        String action = args[0].substring(prefix.length());

        switch (action) {
            case "help":
                output.append(game.getHelpMessage());
                break;
            case "list":
                output.append(getGameList());
                break;
            case "create":
                IGame newGame = getNewGame(game);
                Player player1 = new Player(request, 1);

                newGame.addPlayer(player1);
                serverMap.put(serverMap.size(), newGame);
                gameMap.put(player1, newGame);
                int gameNumber = (gameMap.size() - 1);

                output.append("Game")
                        .append(game.getName())
                        .append("number:")
                        .append(gameNumber)
                        .append(" hosted");
                break;
            case "turn":
                output.append(appendPlayerTurn(player));
                break;
            case "join":

                if (game.isGameFull()) {
                    output.append("Game is full");
                } else if (game.contains(player)) {
                    output.append("you already joined this game");
                } else {
                    Player playerX = new Player(request, 2);
                    game.addPlayer(playerX);
                    gameMap.put(playerX, game);

                    output.append("You joined game:")
                            .append(game.getName())
                            .append("<br/>You are player ")
                            .append(player.getPlayerNumber());
                }
                break;
            case "move":
                if (gameMap.containsKey(player)) {
                    makeMove(player, game, Integer.parseInt(args[1]));
                    output.append(addBoardContent(game));
                }
                break;
            default:
                output.append("Wrong argument entered please use the command !help");
                break;
        }
        return output.toString();
    }


}
