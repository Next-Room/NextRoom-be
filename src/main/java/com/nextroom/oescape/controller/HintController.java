package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.dto.BaseResponse;
import com.nextroom.oescape.dto.DataResponse;
import com.nextroom.oescape.dto.HintDto;
import com.nextroom.oescape.service.HintService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hint")
@RequiredArgsConstructor
public class HintController {
    private final HintService hintService;

    @PostMapping
    public ResponseEntity<BaseResponse> addHint(
        @AuthenticationPrincipal Shop shop,
        @RequestBody HintDto.AddHintRequest request) {
        hintService.addHint(shop, request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getHintList(@AuthenticationPrincipal Shop shop,
        @RequestParam("themeId") Long themeId) {
        return ResponseEntity.ok(new DataResponse<>(OK, hintService.getHintList(shop, themeId)));
    }

    @PutMapping
    public ResponseEntity<BaseResponse> editHint(@AuthenticationPrincipal Shop shop,
        @RequestBody HintDto.EditHintRequest request) {
        hintService.editHint(shop, request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse> removeHint(
        @AuthenticationPrincipal Shop shop,
        @RequestBody HintDto.RemoveHintRequest request) {
        System.out.println(shop);
        hintService.removeHint(shop, request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
