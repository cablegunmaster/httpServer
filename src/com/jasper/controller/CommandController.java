package com.jasper.controller;

import com.jasper.model.ClientWorkerRunnable;
/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class
CommandController {

    Controller controller;

    public CommandController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Command to be processed by the server.
     * @param runnable Runnable client.
     * @param input    String of input from a client.
     */
    public void procesCommand(ClientWorkerRunnable runnable, String input) {
        String[] pieces = input.split(" ", 3);
        String command = null;
        String player = null;
        String rest = null;

        if (pieces.length >= 1) {
            command = pieces[0];
        } else {
            command = input;
        }

        if (pieces.length >= 2) {
            player = pieces[1];
        }

        if (pieces.length >= 3) {
            rest = pieces[2];
        }

        switch (command) {
            //Show list of users.
            case "":
                break;
            default:
                break;
        }
    }


}
