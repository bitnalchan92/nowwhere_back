package com.nowwhere.nowwhere_back.controller;

import com.nowwhere.nowwhere_back.domain.dto.kakao.KakaoAddressDto;
import com.nowwhere.nowwhere_back.domain.dto.kakao.KakaoAddressResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {
    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @GetMapping("/addressInfo")
    public ResponseEntity<KakaoAddressDto> getAddressFromCoords(
            @RequestParam String longitude,
            @RequestParam String latitude
    ) {
        // https://developers.kakao.com/docs/latest/ko/local/dev-guide
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json"
                + "?x=" + longitude + "&y=" + latitude;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoAddressResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                KakaoAddressResponse.class
        );

        KakaoAddressResponse kakaoAddressResponse = response.getBody();
        if (kakaoAddressResponse.getDocuments().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        KakaoAddressResponse.Document doc = kakaoAddressResponse.getDocuments().get(0);
        KakaoAddressResponse.Address address = doc.getAddress();
        KakaoAddressResponse.RoadAddress roadAddress = doc.getRoad_address();

        String roadAddressName = "";
        String zoneNo = "";
        if (roadAddress != null) {
            roadAddressName = roadAddress.getRoad_name();
            zoneNo = roadAddress.getZone_no();
        }

        KakaoAddressDto kakaoAddressDto = new KakaoAddressDto(
                address.getAddress_name(),
                roadAddressName,
                zoneNo
        );

        return ResponseEntity.ok(kakaoAddressDto);
    }
}
