package com.jasper.model;

import com.jasper.model.http.HttpResponseBuilder;

import java.io.UnsupportedEncodingException;


public interface IRequestBuilder {
    void handle(HttpRequest request, HttpResponseBuilder response) throws UnsupportedEncodingException;
}
