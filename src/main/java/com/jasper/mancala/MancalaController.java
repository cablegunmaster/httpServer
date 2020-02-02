package com.jasper.mancala;

import java.util.HashMap;
import java.util.Map;

/**
 * this class facilitates all moves and
 * accepts all incoming strings and translates them in outgoing strings.
 */
public class MancalaController {

    //for now always expect Mancala (interface could have made it in any game required).
    Map<Integer, Mancala> gameListMap = new HashMap<>();

    public void actionController(String action, String[] args) {

        String output;
        switch (action) {
            case "list": //show list of current games.
                StringBuilder sb = new StringBuilder();
                if (gameListMap.isEmpty()) {
                    output = "No games are found";
                }

                for (Integer key : gameListMap.keySet()) {
                    sb.append("GameName: ")
                            .append(gameListMap.get(key).getName())
                            .append(" ")
                            .append(key)
                            .append(" /r/n ");
                    //ex:" GameName: Mancala 0"
                }

                output = sb.toString();
                break;
            case "create": //"create mancala"
                if (checkNotEmptyArgs(args, 0)) {
                    Mancala m = new Mancala();
                    m.addPlayer(new Player());
                    gameListMap.put(gameListMap.size(), m);
                } else {
                    output = "create command has argument of : mancala ";
                }
                break;
            case "join":

                //"join 0" for ex.
                if (checkNotEmptyArgs(args, 0)) {
                    try {
                        Mancala m = gameListMap.get(Integer.parseInt(args[0]));
                        m.addPlayer(new Player());
                    } catch (IllegalArgumentException ex) {
                        output = ex.getMessage();
                    }

                } else {
                    output = "";
                }
                break;
            case "move":

                break;

            case "disconnect":
                //check connection and throw out the game on the list.
                break;
            default:
                output = "Wrong argument entered please use help";
                break;
        }
    }

    /**
     * Check the arguments if its not empty.
     *
     * @param args argument given by command.
     * @param i    content of array start with 0
     */
    private boolean checkNotEmptyArgs(String[] args, int i) {
        return args[i] != null && !args[i].isEmpty();
    }
}
