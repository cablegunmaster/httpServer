package com.jasper.model.http.enums;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 * Enum for the request
 *
 * if not implemented get a statuscode 405 back meaning:(METHOD_NOT_ALLOWED),
 * Not all RequestType implemented.
 */
public enum RequestType {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH;

    public boolean isGetRequest(){
        return this == RequestType.GET;
    }

    public boolean isPostRequest(){
        return this == RequestType.POST;
    }
}
