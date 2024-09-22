package com.nextroom.nextRoomServer.controller;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import io.swagger.v3.oas.annotations.media.Content;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nextroom.nextRoomServer.dto.BaseResponse;
import com.nextroom.nextRoomServer.dto.DataResponse;
import com.nextroom.nextRoomServer.dto.HintDto;
import com.nextroom.nextRoomServer.service.HintService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "Hint")
@RestController
@RequestMapping("/api/v1/hint")
@RequiredArgsConstructor
public class HintController {
    private final HintService hintService;

    @Operation(
        summary = "힌트 등록",
        description = "이미지 리스트는 확장자를 제외한 순수 파일 이름 필요. ex) \"1_2e20b6a9-e24b-45a8-a974-005c14f9f44f\"",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND", content = @Content),
            @ApiResponse(responseCode = "409", description = "HINT_CODE_CONFLICT", content = @Content),
        }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> addHint(@RequestBody @Valid HintDto.AddHintRequest request) {
        hintService.addHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "힌트 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND", content = @Content)
        }
    )
    @GetMapping
    public ResponseEntity<DataResponse<List<HintDto.HintListResponse>>> getHintList(@RequestParam("themeId") Long themeId) {
        return ResponseEntity.ok(new DataResponse<>(OK, hintService.getHintList(themeId)));
    }

    @Operation(
        summary = "힌트 수정",
        description = "이미지 리스트는 확장자를 제외한 순수 파일 이름 필요. ex) \"1_2e20b6a9-e24b-45a8-a974-005c14f9f44f\"",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND", content = @Content)
        }
    )
    @PutMapping
    public ResponseEntity<BaseResponse> editHint(@RequestBody @Valid HintDto.EditHintRequest request) {
        hintService.editHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
        summary = "힌트 삭제",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
            @ApiResponse(responseCode = "404", description = "HINT_NOT_FOUND", content = @Content)
        }
    )
    @DeleteMapping
    public ResponseEntity<BaseResponse> removeHint(@RequestBody @Valid HintDto.RemoveHintRequest request) {
        hintService.removeHint(request);
        return ResponseEntity.ok(new BaseResponse(OK));
    }

    @Operation(
            summary = "PreSigned Url 요청",
            description = """
                    s3 url /{profile}/{shopId}/{themeId}/{type}/{num}_uuid.png

                    ex) "/dev/1/3/hint/1_2e20b6a9-e24b-45a8-a974-005c14f9f44f.png/"
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "NOT_PERMITTED", content = @Content),
                    @ApiResponse(responseCode = "404", description = "THEME_NOT_FOUND", content = @Content)
            }
    )
    @GetMapping("/url")
    public ResponseEntity<DataResponse<HintDto.UrlResponse>> getUrl(@ModelAttribute @Valid @ParameterObject HintDto.UrlRequest request) {
        return ResponseEntity.ok(new DataResponse<>(OK, hintService.getPresignedUrl(request)));
    }
}
