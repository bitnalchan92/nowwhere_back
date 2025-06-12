package com.nowwhere.nowwhere_back.controller;

import com.nowwhere.nowwhere_back.domain.dto.kakao.KakaoAddressDto;
import com.nowwhere.nowwhere_back.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/addressInfo")
    public ResponseEntity<KakaoAddressDto> getAddressFromCoords(
            @RequestParam String longitude,
            @RequestParam String latitude
    ) {
        KakaoAddressDto kakaoAddressDto = kakaoService.getAddressFromCoords(longitude, latitude);

        if (kakaoAddressDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(kakaoAddressDto);
    }
}
