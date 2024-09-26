package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.OK;

import com.nextroom.nextRoomServer.dto.BannerDto;
import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.DataResponse;
import com.nextroom.nextRoomServer.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Banner")
@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(
        summary = "배너 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
        }
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<BannerDto>>> getBannerList() {
        return ResponseEntity.ok(new DataResponse<>(OK, bannerService.getBannerList()));
    }

}
