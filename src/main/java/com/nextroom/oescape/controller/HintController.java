package com.nextroom.oescape.controller;

import static com.nextroom.oescape.exceptions.StatusCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<BaseResponse> addHint(@RequestBody HintDto.AddHintRequest request) {
        hintService.addHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getHintList(@RequestParam("themeId") Long themeId) {
        return ResponseEntity.ok(new DataResponse<>(OK, hintService.getHintList(themeId)));
    }

    @PutMapping
    public ResponseEntity<BaseResponse> editHint(@RequestBody HintDto.EditHintRequest request) {
        hintService.editHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse> removeHint(@RequestBody HintDto.RemoveHintRequest request) {
        hintService.removeHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }
}
