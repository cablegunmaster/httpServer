package com.jasper.model;

import com.jasper.model.http.HttpResponseHandler;

import java.io.UnsupportedEncodingException;


public interface IRequestHandler {
    void handle(HttpRequest request, HttpResponseHandler response) throws UnsupportedEncodingException;
}