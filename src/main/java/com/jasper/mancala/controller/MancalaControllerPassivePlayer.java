package com.jasper.mancala.controller;

import com.jasper.mancala.Mancala;
import com.jasper.mancala.Player;
import com.jasper.model.Client;

import javax.annotation.Nonnull;

public class MancalaControllerPassivePlayer extends MancalaController {

    public String actionController(String prefix, String[] args, @Nonnull Client client) {
        String output = "";

        if (args.length > 0 && args[0].startsWith(prefix)) {
            String action = args[0].substring(prefix.length());
            Player p = this.getPlayerByClient(client);

            switch (action) {
                case "help":
                    output = LIST_CMD;
                    break;
                case "list": //show list of current games.
                    output = getGameList();
                    break;
                case "create":
                    output = "Game Mancala number:" + (serverListMap.size() - 1) + " is hosted <br/>" +
                            "by another player you can join";
                    break;
                case "turn":
                    output = getPlayerTurn(mancalaSessionMap.get(client), p);
                    break;
                case "join":
                    //"join 0" for ex.
                    output = "a player joined a game";
                    if (p != null) {
                        Mancala mancala = mancalaSessionMap.get(client);
                        output += "you are player" + p.playerNumber + " it is player" + mancala.getPlayerTurn();
                        output += mancala.getBoard();
                    }
                    break;
                case "move":
                    if (mancalaSessionMap.containsKey(client)) {
                        Mancala mancala = mancalaSessionMap.get(client);
                        output += mancala.getBoard();

                        if (mancala.getPlayerTurn() == p.playerNumber) {
                            output += "Current turn is " + mancala.getPlayerTurn() + " you are " + p.playerNumber;
                        }
                    }
                    break;
                default:
                    output = "Wrong argument entered please use the command help";
                    break;
            }
        }
        return output;
    }
}
