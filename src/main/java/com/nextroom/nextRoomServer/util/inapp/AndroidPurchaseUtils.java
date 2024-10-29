package com.nextroom.nextRoomServer.util.inapp;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.nextroom.nextRoomServer.dto.SubscriptionDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.util.Base64Decoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AndroidPurchaseUtils {
    private final String profile;
    private final String packageName;
    private final GoogleCredentials credentials;
    private final AndroidPublisher androidPublisher;
    private final ObjectMapper objectMapper;

    public AndroidPurchaseUtils(@Value("${iap.google.credentials}") String accountFilePath,
        @Value("${iap.google.packageName}") String packageName,
        @Value("${spring.config.activate.on-profile}") String profile,
        @Autowired ObjectMapper objectMapper) throws IOException, GeneralSecurityException {

        this.profile = profile;
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

    public SubscriptionPurchase verifyPurchase(String purchaseToken, String subscriptionId) throws IOException {
        AndroidPublisher.Purchases.Subscriptions.Get get = androidPublisher.purchases()
            .subscriptions()
            .get(packageName, subscriptionId, purchaseToken);
        get.setAccessToken(getAccessToken().getTokenValue());
        SubscriptionPurchase subscriptionPurchase = get.execute();

        // TODO 구매 상황 검증 필요
        // 구독 결제 완료 상태가 아닌 경우
        // if (subscriptionPurchase.getPaymentState() != 1) {
        //     throw new CustomException(BAD_REQUEST);
        // }

        // 상품 승인이 되지 않은 경우
        // if (subscriptionPurchase.getAcknowledgementState() == 0) {
        //     throw new CustomException(BAD_REQUEST);
        // }

        log.info("SUBSCRIPTION AT PURCHASE(V1) : {}", subscriptionPurchase.toString());

        return subscriptionPurchase;
    }

    public SubscriptionPurchaseV2 verifyPurchase(String purchaseToken) throws IOException {
        SubscriptionPurchaseV2 subscriptionPurchaseV2 = getSubscriptionPurchase(purchaseToken);

        // TODO 구매 상황 검증 필요
        // 상품 승인이 되지 않은 경우
        // if (subscriptionPurchaseV2.getAcknowledgementState().equals("ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED")) {
        //     throw new CustomException(BAD_REQUEST);
        // }

        log.info("SUBSCRIPTION AT PURCHASE(V2) : {}", subscriptionPurchaseV2.toString());

        return subscriptionPurchaseV2;
    }

    public SubscriptionPurchaseV2 verifyNotification(String purchaseToken) throws IOException {
        SubscriptionPurchaseV2 subscriptionPurchaseV2 = getSubscriptionPurchase(purchaseToken);

        // TODO 갱신 or 만료 상황 검증 필요

        log.info("SUBSCRIPTION AT NOTIFICATION : {}", subscriptionPurchaseV2.toString());

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

    public SubscriptionDto.SubscriptionNotification getSubscriptionNotification(String data) throws
        JsonProcessingException {
        String decodedData = Base64Decoder.decode(data);
        SubscriptionDto.PublishedMessage publishedMessage = objectMapper.readValue(decodedData,
            SubscriptionDto.PublishedMessage.class);
        SubscriptionDto.SubscriptionNotification subscriptionNotification = publishedMessage.getSubscriptionNotification();

        log.info("PURCHASE TOKEN AT NOTIFICATION : {}", subscriptionNotification.getPurchaseToken());

        validateEnvironment(publishedMessage);
        validatePackageName(publishedMessage);

        return subscriptionNotification;
    }

    private SubscriptionPurchaseV2 getSubscriptionPurchase(String purchaseToken) throws IOException {
        AndroidPublisher.Purchases.Subscriptionsv2.Get get = androidPublisher.purchases()
            .subscriptionsv2()
            .get(packageName, purchaseToken);
        get.setAccessToken(getAccessToken().getTokenValue());

        SubscriptionPurchaseV2 subscriptionPurchaseV2 = get.execute();

        validateEnvironment(subscriptionPurchaseV2);

        return subscriptionPurchaseV2;
    }

    private AccessToken getAccessToken() throws IOException {
        credentials.refreshIfExpired();
        return credentials.getAccessToken();
    }

    private void validateEnvironment(SubscriptionDto.PublishedMessage publishedMessage) {
        if (("prod".equals(profile) && publishedMessage.isTestNotification())
                || ("dev".equals(profile) && !publishedMessage.isTestNotification())) {
            throw new CustomException(ENVIRONMENT_DOES_NOT_MATCH);
        }
    }

    private void validateEnvironment(SubscriptionPurchaseV2 subscriptionPurchaseV2) {
        boolean isTestPurchase = subscriptionPurchaseV2.getTestPurchase() != null;

        if (("prod".equals(profile) && isTestPurchase) || ("dev".equals(profile) && !isTestPurchase)) {
            throw new CustomException(ENVIRONMENT_DOES_NOT_MATCH);
        }
    }

    private void validatePackageName(SubscriptionDto.PublishedMessage publishedMessage) {
        if (!this.packageName.equals(publishedMessage.getPackageName())) {
            throw new CustomException(PACKAGE_NAME_DOES_NOT_MATCH);
        }
    }
}
