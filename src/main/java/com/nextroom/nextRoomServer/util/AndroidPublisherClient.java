package com.nextroom.nextRoomServer.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;

public class AndroidPublisherClient {

    private static final String APPLICATION_NAME = "NextRoomApplication";
    private static final String PACKAGE_NAME = "com.nextroom.nextroom";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CLIENT_SECRET_PATH = "src/main/resources/client_secret_90347533694-0uf0fm7rjghobhh534cv3ta3k38d5qru.apps.googleusercontent.com.json";
    private static final String ACCESS_TOKEN_FILE_PATH = "src/main/resources/nextroom-server.token";
    private static AndroidPublisher androidPublisher = null;

    public static AndroidPublisher initializeAndroidPublisher() throws Exception {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        NextRoomHttpRequestInitializer httpRequestInitializer = new NextRoomHttpRequestInitializer();

        GoogleClientRequestInitializer googleClientRequestInitializer = new GoogleClientRequestInitializer() {
            @Override
            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                byte[] bytes = Files.readAllBytes(Paths.get(ACCESS_TOKEN_FILE_PATH));
                String access_token = new String(bytes, StandardCharsets.UTF_8);

                HttpHeaders headers = new HttpHeaders().setAuthorization("Bearer " + access_token);
                // TODO handle exception
                request.setRequestHeaders(headers);
            }
        };

        return new AndroidPublisher.Builder(httpTransport, JSON_FACTORY, httpRequestInitializer)
            .setApplicationName(APPLICATION_NAME)
            .setGoogleClientRequestInitializer(googleClientRequestInitializer)
            .build();
    }

    public AndroidPublisherClient() throws Exception {
        androidPublisher = initializeAndroidPublisher();
    }

    public SubscriptionPurchaseV2 getSubscriptionPurchase(String purchaseToken) throws
        IOException {
        return androidPublisher
            .purchases()
            .subscriptionsv2()
            .get(PACKAGE_NAME, purchaseToken).execute();
    }
}
