package com.jasper.model.request;

import com.jasper.model.request.requestenums.ParseState;
import com.jasper.model.request.requestenums.RequestType;
import com.jasper.model.request.requestenums.StatusCode;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private ParseState parseState;

    public RequestParser() {
        parseState = ParseState.READING_METHOD;
    }

    public ParseState getParseState() {
        return parseState;
    }

    public void setParseState(ParseState parseState) {
        this.parseState = parseState;
    }

    /**
     * Input to be processed and checked.
     *
     * @param inputString inputString from a client.
     * @param request     HttpRequest object which contains the statuscode.
     */
    public void parseRequest(String inputString, HttpRequest request) {

        //Parameter variable length of request. 7K
        //413 Entity too large
        if (inputString.length() > 8192) {
            request.setStatusCode(StatusCode.PAYLOAD_TO_LARGE);
            return;
        }

        //might be an switch
        if (parseState.isReadingMethod()) {
            readMethod(inputString, request);
        }

        if(parseState.isReadingURI()) {

//            if(){
//
//            }
//            uri =
//            parseUri(uri,request);
        }

        //URI LENGTH
        //Note: Servers ought to be cautious about depending on URI lengths above 255 bytes, because some older client or proxy
        //implementations might not properly support these lengths.

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

    /**
     * InputString it only the URI
     * @param uri
     * @param request
     */
    private void parseUri(String uri, HttpRequest request) {
    }

    /**
     * Once its found to be in a RequestType
     * Checks if its starting with until it found a valid RequestType
     * Transition to Error, or READ_URI [parseState].
     * @param inputString
     * @param request
     */
    private void readMethod(String inputString, HttpRequest request) {

        //Go through all requestTypes, per letter.
        boolean startsWithRequestTypeFound = false;

        for (RequestType requestTypeItem : RequestType.values()) {
            String[] inputSplit = inputString.split(" ");

            //error if its not the same.
            if(inputSplit.length == 1 && requestTypeItem.name().startsWith(inputString.toUpperCase())){
                startsWithRequestTypeFound = true;
            }

            if (inputSplit[0] != null &&
                    requestTypeItem.name().startsWith(inputSplit[0].toUpperCase()) &&
                    inputSplit.length >= 2) {

                //set RequestMethod.
                RequestType requestType = RequestType.valueOf(inputSplit[0]);
                request.setRequestMethod(requestType);
                parseState = ParseState.READING_URI;
                startsWithRequestTypeFound = true;
                break;
            }
        }

        if(!startsWithRequestTypeFound){
            request.setStatusCode(StatusCode.BAD_REQUEST);
            parseState = ParseState.ERROR;
        }
    }

}
