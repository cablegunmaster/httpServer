package com.jasper.model.request;

import com.jasper.controller.Controller;
import com.jasper.model.ClientWorkerRunnable;
import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.requestenums.ParseState;
import com.jasper.model.request.requestenums.RequestType;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private Controller controller;
    private ParseState parseState;

    public RequestParser(Controller controller) {
        parseState = ParseState.INITIAL;
        this.controller = controller;
    }

    public ParseState getState(){
        return parseState;
    }

    /**
     * Command to be processed by the server.
     *
     * @param runnable Runnable client.
     * @param input    String of input from a client.
     */
    //TODO refactor this function, right now its not doing anything useful.
    public void procesCommand(ClientWorkerRunnable runnable,
                                     String input,
                                     HttpRequest request) {
    /*
        parseState = ParseState.READING_FIRST_LINE;
        if(getState().isReadingFirstLine()) {

            String requestTypeIncoming = input; //split on /r/n for first line only, else ERROR
            //Go through all requestTypes.
            for (RequestType requestTypeItem : RequestType.values()) {

                //start with these names
                String[] inputString = requestTypeIncoming.split(" ");
                if (requestTypeItem.name().startsWith(requestTypeIncoming.toUpperCase()) || inputString.length == 2) {
                    if (inputString[0] != null && inputString.length == 2) {
                        //requestType = RequestType.valueOf(inputString[0]);
                        break;
                    }

                } else {
                    //no valid input found.
                    //request.setState(RequestState.);
                    System.out.println("Invalid request found");
                    break;
                }
            }
        }

        /*if (requestType != null || response.length() > 8) {
            request.setState();
            request.setStatusCode();//some error.
        }

        if (request.getState().equals(RequestState.READING_HEADER_KEY) || request.getState().equals(RequestState.READING_HEADER_VALUE)) {
            int length = response.length();

            //check /r/n/r/n is found.
            if () {
                isReadingRequest = false;
            }
        }

        //GET request.
        //TODO get first line only for this part.
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
        }*/
    }

}
