package com.nextroom.nextRoomServer.service;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.TARGET_PRODUCT_NOT_FOUND;

import com.nextroom.nextRoomServer.domain.Product;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import java.util.List;
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

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProduct(String productId) {
        return productRepository.findBySubscriptionProductId(productId)
            .orElseThrow(() -> new CustomException(TARGET_PRODUCT_NOT_FOUND));
    }
}
