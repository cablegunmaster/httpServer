package com.jasper.controller;

import com.jasper.model.ClientWorkerRunnable;
import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.requestenums.RequestType;

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
     *
     * @param runnable Runnable client.
     * @param input    String of input from a client.
     */
    public void procesCommand(ClientWorkerRunnable runnable,
                                     String input,
                                     HttpRequest request) {

        //GET request.
        if (input.contains("GET") || input.contains("POST")) {
            request.setRequestMethod(RequestType.GET);

            //here we get the path.
            //Ex. 'GET / HTTP/1.1'
            String[] command = input.split(" ", 3); //split on spaces.
            if (command.length == 3) {
                String path = command[1]; //getPath;
                if (path.equals("/")) {
                    request.setRequestpath("index.html");
                }
            }
        }else{
            //should be a status Errorcode not a valid request.
            //request
        }
    }

}
