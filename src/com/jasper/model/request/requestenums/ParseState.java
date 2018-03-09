package com.jasper.model.request.requestenums;

import com.jasper.controller.Controller;

/**
 * States for reading Request.
 */
public enum ParseState {

    //Reading states.
    READING_METHOD {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Reading method");
        }
    },
    READING_URI {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Reading URI");
        }
    },
    READING_HTTP_VERSION {
        @Override
        void debugMessage(Controller controller) {
            controller.addStringToOutputLog("Reading HTTP Version");
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

    public boolean isReadingMethod(){
        return this == ParseState.READING_METHOD;
    }
    public boolean isReadingURI(){
        return this == ParseState.READING_URI;
    }

    public boolean isReadingHttpVersion(){
        return this == ParseState.READING_HTTP_VERSION;
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
