package com.nextroom.nextRoomServer.util.inapp;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.SubscriptionPurchase;
import com.google.api.services.androidpublisher.model.SubscriptionPurchaseV2;
import com.google.api.services.androidpublisher.model.SubscriptionPurchasesAcknowledgeRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.nextroom.nextRoomServer.exceptions.CustomException;

@Component
public class AndroidPurchaseUtils {
    private final String packageName;
    private final GoogleCredentials credentials;
    private final AndroidPublisher androidPublisher;
    private final ObjectMapper objectMapper;

    public AndroidPurchaseUtils(@Value("${iap.google.credentials}") String accountFilePath,
        @Value("${iap.google.packageName}") String packageName,
        @Autowired ObjectMapper objectMapper) throws IOException, GeneralSecurityException {

        this.packageName = packageName;
        this.objectMapper = objectMapper;

        InputStream inputStream = new ClassPathResource(accountFilePath).getInputStream();
        this.credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(AndroidPublisherScopes.ANDROIDPUBLISHER);

        this.androidPublisher = new AndroidPublisher.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials)
        ).setApplicationName(packageName).build();
    }

    public SubscriptionPurchase verify(String purchaseToken, String subscriptionId) throws IOException {
        AndroidPublisher.Purchases.Subscriptions.Get get = androidPublisher.purchases()
            .subscriptions()
            .get(packageName, subscriptionId, purchaseToken);
        get.setAccessToken(getAccessToken().getTokenValue());
        SubscriptionPurchase subscriptionPurchase = get.execute();

        // 구독 결제 완료 상태가 아닌 경우
        if (subscriptionPurchase.getPaymentState() != 1) {
            throw new CustomException(BAD_REQUEST);
        }

        // 상품 승인이 되지 않은 경우
        if (subscriptionPurchase.getAcknowledgementState() == 0) {
            throw new CustomException(BAD_REQUEST);
        }

        return subscriptionPurchase;
    }

    public SubscriptionPurchaseV2 verify(String purchaseToken) throws IOException {
        AndroidPublisher.Purchases.Subscriptionsv2.Get get = androidPublisher.purchases()
            .subscriptionsv2()
            .get(packageName, purchaseToken);
        get.setAccessToken(getAccessToken().getTokenValue());
        SubscriptionPurchaseV2 subscriptionPurchaseV2 = get.execute();

        // 상품 승인이 되지 않은 경우
        if (subscriptionPurchaseV2.getAcknowledgementState().equals("ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED")) {
            throw new CustomException(BAD_REQUEST);
        }

        return subscriptionPurchaseV2;
    }

    public void acknowledge(String purchaseToken, String subscriptionId) throws IOException {
        SubscriptionPurchasesAcknowledgeRequest request = new SubscriptionPurchasesAcknowledgeRequest();
        AndroidPublisher.Purchases.Subscriptions.Acknowledge acknowledge = androidPublisher.purchases()
            .subscriptions()
            .acknowledge(packageName, subscriptionId, purchaseToken, request);
        acknowledge.setAccessToken(getAccessToken().getTokenValue());
        acknowledge.execute();
    }

    private AccessToken getAccessToken() throws IOException {
        credentials.refreshIfExpired();
        return credentials.getAccessToken();
    }
}
