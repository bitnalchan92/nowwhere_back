package com.nowwhere.nowwhere_back.service;

import com.nowwhere.nowwhere_back.domain.dto.kakao.KakaoAddressDto;
import com.nowwhere.nowwhere_back.domain.dto.kakao.KakaoAddressResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class KakaoService {
    private final WebClient kakaoWebClient;

    public KakaoService(WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }

    public KakaoAddressDto getAddressFromCoords(
            String longitude,
            String latitude
    ) {
        try {
            // https://developers.kakao.com/docs/latest/ko/local/dev-guide
            KakaoAddressResponse response = kakaoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/geo/coord2address.json")
                            .queryParam("x", longitude)
                            .queryParam("y", latitude)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(KakaoAddressResponse.class)
                    .block();

            if ( response == null || response.getDocuments().isEmpty()) {
                return null;
            }

            KakaoAddressResponse.Document doc = response.getDocuments().get(0);
            KakaoAddressResponse.Address address = doc.getAddress();
            KakaoAddressResponse.RoadAddress roadAddress = doc.getRoad_address();

            String roadAddressName = roadAddress != null ? roadAddress.getRoad_name() : "";
            String zoneNo = roadAddress != null ? roadAddress.getZone_no() : "";

            return new KakaoAddressDto(
                    address.getAddress_name(),
                    roadAddressName,
                    zoneNo
            );
        } catch (Exception e) {
            log.error("카카오 주소 조회 실패 : {}", e.getMessage(), e);
            return null;
        }
    }
}
