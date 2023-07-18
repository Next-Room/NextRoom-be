package com.nextroom.oescape.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nextroom.oescape.service.HintService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hint")
@RequiredArgsConstructor
public class HintController {
    private final HintService hintService;
}
