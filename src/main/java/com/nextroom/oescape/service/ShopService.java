package com.nextroom.oescape.service;

import org.springframework.stereotype.Service;

import com.nextroom.oescape.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
}
