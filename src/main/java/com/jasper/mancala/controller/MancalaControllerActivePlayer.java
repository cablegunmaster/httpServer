package com.jasper.mancala.controller;

import com.jasper.mancala.Mancala;
import com.jasper.mancala.Player;
import com.jasper.model.Client;

import javax.annotation.Nonnull;

/**
 * this class facilitates all moves and
 * accepts all incoming strings and translates them in outgoing strings.
 */
public class MancalaControllerActivePlayer extends MancalaController {

    public String actionController(String prefix, String[] args, @Nonnull Client client) {

        String output = "Nothing happened general error";

        Player player = null;
        Mancala mancala = null;

        if (mancalaSessionMap.containsKey(client)) {
            mancala = mancalaSessionMap.get(client);
            player = mancala.getPlayerByClient(client);
        }

        if (args.length > 0 && args[0].startsWith(prefix)) {
            String action = args[0].substring(prefix.length());

            switch (action) {
                case "help":
                    output = LIST_CMD;
                    break;
                case "list": //show list of current games.
                    output = getGameList();
                    break;
                case "create":
                    Mancala newMancala = new Mancala();
                    newMancala.addPlayer(new Player(client));
                    serverListMap.clear(); //for now hotfix. map is wrong.
                    serverListMap.put(serverListMap.size(), newMancala);
                    mancalaSessionMap.put(client, newMancala);
                    output = "Game Mancala number:" + (serverListMap.size() - 1) + " hosted <br/>";
                    break;
                case "turn":
                    output = getPlayerTurn(mancala,player);
                    break;
                case "join":
                    //"join 0" for ex.
                    if (checkNotEmptyArgs(args, 1)) {
                        Mancala man = serverListMap.get(Integer.parseInt(args[1]));
                        if (man != null) {
                            int playerNumber = man.addPlayer(new Player(client));
                            output = "Succesfully joined game: " + args[1] + "<br/>" +
                                    "You are player " + playerNumber + " and its" + man.getPlayerTurn() + " turn.<br/>" +
                                    man.getBoard().toString();
                            mancalaSessionMap.put(client, man);
                        }
                    }
                    break;
                case "move":
                    if (mancalaSessionMap.containsKey(client) && player != null) {
                        if (mancala.getPlayerTurn() == player.playerNumber) {
                            output = mancala.setMove(Integer.parseInt(args[1]), player.playerNumber);
                        } else {
                            output = "Not your turn other players turn";
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
