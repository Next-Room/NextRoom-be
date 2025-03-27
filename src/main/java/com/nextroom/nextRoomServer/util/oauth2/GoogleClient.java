package com.nextroom.nextRoomServer.util.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.nextroom.nextRoomServer.dto.AuthDto;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.INVALID_TOKEN;
import static com.nextroom.nextRoomServer.exceptions.StatusCode.IO_ERROR;


@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient {

    @Value("${oauth2.google.token-url}")
    private String tokenUrl;
    @Value("${oauth2.google.profile-url}")
    private String profileUrl;
    @Value("${oauth2.google.client-id}")
    private String clientId;
    @Value("${oauth2.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    private static final String GRANT_TYPE = "authorization_code";

    public AuthDto.GoogleInfoResponseDto getUserInfo(AuthDto.GoogleLogInRequestDto request) {
        if (request.isCode()) {
            String googleAccessToken = requestAccessToken(request.getCode());
            return requestAccountProfileFromAccessToken(googleAccessToken);
        } else {
            return requestAccountProfileFromIdToken(request.getIdToken());
        }
    }

    private String requestAccessToken(String authorizationCode) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", GRANT_TYPE);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        AuthDto.GoogleTokenResponseDto response = restTemplate.postForObject(tokenUrl, request, AuthDto.GoogleTokenResponseDto.class);

        assert response != null;
        return response.getAccessToken();
    }

    private AuthDto.GoogleInfoResponseDto requestAccountProfileFromAccessToken(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(profileUrl, HttpMethod.GET, request, AuthDto.GoogleInfoResponseDto.class).getBody();
    }

    private AuthDto.GoogleInfoResponseDto requestAccountProfileFromIdToken(String requestIdToken) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(requestIdToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new CustomException(IO_ERROR);
        }
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String id = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            log.info("sub: {}", id);
            log.info("email: {}", email);
            log.info("name: {}", name);

            return AuthDto.GoogleInfoResponseDto.toGoogleInfoResponseDto(id, email);
        } else {
            throw new CustomException(INVALID_TOKEN);
        }
    }
}
