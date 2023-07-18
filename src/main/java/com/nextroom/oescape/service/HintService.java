package com.nextroom.oescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.repository.HintRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HintService {
    private HintRepository hintRepository;

    @Transactional
    public void addHint(Shop shop, HintDto.AddHintRequest request) {
        //TODO 회원 검증 로직

    }
}