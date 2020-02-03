package com.jasper.mancala.controller;

import com.jasper.mancala.Mancala;
import com.jasper.mancala.Player;
import com.jasper.model.Client;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class MancalaController {

    //for now always expect Mancala (interface could have made it in any game required).
    public static Map<Integer, Mancala> serverListMap = new HashMap<>();
    public static Map<Client, Mancala> mancalaSessionMap = new HashMap<>(); //better way?

    public static final String LIST_CMD ="<br/>" +
            "command list overview: <br/>" +
            "!list - shows all games <br/>" +
            "!create - create new game <br/>" +
            "!join 'number' - join a game <br/>" +
            "!move 'number' - make a move in game<br/>" +
            "!show - return list of valid moves" +
            "!turn - return which turn it is" +
            "1- 6 for P1 or <br/>" +
            "8-13 for P2<br/>";

    public String getGameList() {
        StringBuilder sb = new StringBuilder();

        if (serverListMap.isEmpty()) {
            sb.append("No games are found");
        }

        for (Integer key : serverListMap.keySet()) {
            sb.append("<br/> GameName: ")
                    .append(serverListMap.get(key).getName())
                    .append(" ")
                    .append(key)
                    .append(" <br/> ");
            //ex:" GameName: Mancala 0"
        }
        return sb.toString();
    }

    public String getPlayerTurn(Mancala mancala , Player player){
        if (player != null && mancala.getPlayerTurn() == player.playerNumber) {
            return "It is your turn you can play a move";
        }else if(player != null){
            return "it is not your turn its player"+ mancala.getPlayerTurn() + " turn you are player"+ player.playerNumber;
        }
        return "player X not found but its player " +mancala.getPlayerTurn() + "turn";
    }

    /**
     * Check the arguments if its not empty.
     *
     * @param args argument given by command.
     * @param i    content of array start with 0
     */
    public boolean checkNotEmptyArgs(String[] args, int i) {
        return args.length > i;
    }

    /**
     * @return player
     */
    @CheckForNull
    public Player getPlayerByClient(@Nonnull Client client){
        if (mancalaSessionMap.containsKey(client)) {
            Mancala mancala = mancalaSessionMap.get(client);
            return mancala.getPlayerByClient(client);
        }
        return null;
    }
}
