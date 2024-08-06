package com.nextroom.nextRoomServer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.nextRoomServer.dto.ProductDto;
import com.nextroom.nextRoomServer.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public void addProduct(ProductDto.AddProductRequest request) {
        productRepository.save(request.toProduct());
    }
}
