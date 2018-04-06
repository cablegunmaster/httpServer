package com.jasper.model.request;

import com.jasper.model.HttpRequest;
import com.jasper.model.HttpResponse;
import java.io.UnsupportedEncodingException;

public interface RequestHandler {
    void handle(HttpRequest request, HttpResponse response) throws UnsupportedEncodingException;
}
