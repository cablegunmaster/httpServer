package com.jasper.model.request.requestenums;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 * Enum for the request
 *
 * if not implemented get a statuscode 405 back meaning:(METHOD_NOT_ALLOWED),
 * The function is not yet implemented.
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
}
