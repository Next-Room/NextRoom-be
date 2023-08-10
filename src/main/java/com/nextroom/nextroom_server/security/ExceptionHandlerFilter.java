package com.nextroom.nextroom_server.security;

import static com.nextroom.nextroom_server.exceptions.StatusCode.*;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextroom_server.exceptions.CustomException;
import com.nextroom.nextroom_server.exceptions.ErrorResponse;
import com.nextroom.nextroom_server.exceptions.StatusCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,

        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            setErrorResponse(response, INVALID_TOKEN_SIGNATURE);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            setErrorResponse(response, UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException | JwtException e) {
            setErrorResponse(response, INVALID_TOKEN);
        } catch (CustomException e) {
            setErrorResponse(response, e.getStatusCode());
        }
    }

    private void setErrorResponse(HttpServletResponse response, StatusCode statusCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
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
