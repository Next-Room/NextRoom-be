package com.nextroom.nextRoomServer.util;

import java.io.IOException;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

public class NextRoomHttpRequestInitializer implements HttpRequestInitializer {
    @Override
    public void initialize(HttpRequest request) throws IOException {
        request.setConnectTimeout(30000); // 30 seconds
        request.setReadTimeout(30000);    // 30 seconds
    }
}
