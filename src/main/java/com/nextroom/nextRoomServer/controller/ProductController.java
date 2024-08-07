package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.ProductDto;
import com.nextroom.nextRoomServer.service.ProductService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@Hidden
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<BaseResponse> addProduct(@RequestBody ProductDto.AddProductRequest request) {
        productService.addProduct(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
