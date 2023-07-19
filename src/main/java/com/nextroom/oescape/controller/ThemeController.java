package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.dto.BaseResponse;
import com.nextroom.oescape.dto.DataResponse;
import com.nextroom.oescape.dto.ThemeDto;
import com.nextroom.oescape.repository.ShopRepository;
import com.nextroom.oescape.service.ShopService;
import com.nextroom.oescape.service.ThemeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/theme")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;
    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<BaseResponse> addTheme(
        Authentication authentication,
        @RequestBody ThemeDto.AddThemeRequest request) {
        Shop shop = shopService.extractShopFromAuthentication(authentication);
        themeService.addTheme(shop, request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getThemeList(Authentication authentication) {
        Shop shop = shopService.extractShopFromAuthentication(authentication);
        return ResponseEntity.ok(new DataResponse<>(OK, themeService.getThemeList(shop)));
    }

    @PutMapping
    public ResponseEntity<BaseResponse> editTheme(Authentication authentication,
        @RequestBody ThemeDto.EditThemeRequest request) {
        Shop shop = shopService.extractShopFromAuthentication(authentication);
        themeService.editTheme(shop, request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse> removeTheme(
        Authentication authentication,
        @RequestBody ThemeDto.RemoveRequest request) {
        Shop shop = shopService.extractShopFromAuthentication(authentication);
        themeService.removeTheme(shop, request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
