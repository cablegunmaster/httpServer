package com.jasper.model.request.requestenums;

import com.jasper.controller.Controller;

/**
 * States for reading Request.
 */
public enum ParseState {

    //Reading states.
    INITIAL {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Initial state");
        }
    },
    READING_FIRST_LINE {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Reading first line");
        }
    },
    READING_HEADER_KEY {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Reading header key");

        }
    },
    READING_HEADER_VALUE {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Reading header value");
        }
    },
    ERROR {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Error occured");
        }
    };
    public boolean isReadingFirstLine(){
        return this == ParseState.READING_FIRST_LINE;
    }

    public boolean isReadingHeaderKey() {
        return this == ParseState.READING_HEADER_KEY;
    }

    public boolean isReadingHeaderValue() {
        return this == ParseState.READING_HEADER_VALUE;
    }

    public boolean isErrorState() {
        return this == ParseState.ERROR;
    }

    //function to show in the state a debug line.
    abstract void debugMessage(Controller aParameter);

}
