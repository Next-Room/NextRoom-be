package com.nextroom.oescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Test")
public class TestController {

    @Operation(
        description = "Test endpoint",
        summary = "Create Test",
        responses = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Unauthorized / Invalid Token")
        }
    )
    @PostMapping("/test")
    public ResponseEntity<Void> exampleMethod() {
        return ResponseEntity.ok().build();
    }
}
