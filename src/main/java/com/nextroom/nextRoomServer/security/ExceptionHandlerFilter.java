package com.nextroom.nextRoomServer.security;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.exceptions.ErrorResponse;
import com.nextroom.nextRoomServer.exceptions.StatusCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,

        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            Throwable unwrappedThrowable = unwrap(e);
            setErrorResponse(response, resolveStatusCode(unwrappedThrowable));
        }
    }

    // 컨트롤러/서비스에서 던진 예외는 DispatcherServlet 이 ServletException 으로 감싸서 올라오므로 원인 예외를 꺼낸다
    private Throwable unwrap(Throwable e) {
        while (e instanceof ServletException && e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }

    private StatusCode resolveStatusCode(Throwable e) {
        if (e instanceof io.jsonwebtoken.security.SecurityException || e instanceof MalformedJwtException) {
            return INVALID_TOKEN_SIGNATURE;
        }
        if (e instanceof ExpiredJwtException) {
            return TOKEN_EXPIRED;
        }
        if (e instanceof UnsupportedJwtException) {
            return UNSUPPORTED_TOKEN;
        }
        if (e instanceof IllegalArgumentException || e instanceof JwtException) {
            return INVALID_TOKEN;
        }
        if (e instanceof CustomException customException) {
            return customException.getStatusCode();
        }
        // 토큰 문제가 아닌 예외(Redis 연결 장애 등)는 5xx 로 응답해 클라이언트가 세션 만료로 오인하지 않게 한다
        log.error("Unhandled exception in filter chain", e);
        return INTERNAL_SERVER_ERROR;
    }

    private void setErrorResponse(HttpServletResponse response, StatusCode statusCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode.getCode().value());

        try {
            response.getWriter().write(objectMapper.writeValueAsString(
                    ErrorResponse.builder()
                        .code(statusCode.getCode().value())
                        .message(statusCode.getMessage())
                        .build()
                )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
