package com.jasper.model.request;

import com.jasper.model.HttpRequest;
import com.jasper.model.response.HttpResponseHandler;
import java.io.UnsupportedEncodingException;


public interface RequestHandler {
    void handle(HttpRequest request, HttpResponseHandler response) throws UnsupportedEncodingException;
}
