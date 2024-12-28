package com.nextroom.nextRoomServer.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "requestId";
    private static final int MAX_PAYLOAD_LENGTH = 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청 ID 생성 및 MDC에 저장
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID, requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContentCachingRequestWrapper wrappedRequest = request instanceof ContentCachingRequestWrapper
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = response instanceof ContentCachingResponseWrapper
                ? (ContentCachingResponseWrapper) response
                : new ContentCachingResponseWrapper(response);

        // Request 로깅
        logRequest(wrappedRequest);
        // Response 로깅
        logResponse(wrappedResponse, request);

        MDC.clear();
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("[REQ][%s] [%s] %s",
            MDC.get(REQUEST_ID),
            request.getMethod(),
            request.getRequestURI()
        ));

        // Client IP
        logMessage.append(" - Client IP: ")
            .append(request.getRemoteAddr());

        // Request Body
        String contentType = request.getContentType();
        if (contentType != null && !contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            String payload = getPayload(request.getContentAsByteArray());
            if (!payload.isEmpty()) {
                logMessage.append("\nRequest Body: ").append(payload);
            }
        }

        log.info(logMessage.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("[RES][%s] [%s] %s [%d]",
            MDC.get(REQUEST_ID),
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus()
        ));

        // Response Body
        String payload = getPayload(response.getContentAsByteArray());
        if (!payload.isEmpty()) {
            logMessage.append("\nResponse Body: ").append(payload);
        }

        log.info(logMessage.toString());
    }

    private String getPayload(byte[] buf) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
        String payload = new String(buf, 0, length);

        payload = payload.replaceAll("[\\r\\n\\t]", "");
        payload = payload.replaceAll("\\s{2,}", " ");

        if (buf.length > MAX_PAYLOAD_LENGTH) {
            payload += "... (truncated)";
        }
        return payload;
    }
}