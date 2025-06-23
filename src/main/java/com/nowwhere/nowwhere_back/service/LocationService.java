package com.nowwhere.nowwhere_back.service;

import com.nowwhere.nowwhere_back.domain.dto.location.AddressDto;
import com.nowwhere.nowwhere_back.domain.dto.location.AddressResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class LocationService {
    private final WebClient kakaoWebClient;

    public LocationService(WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }

    // 참고 : https://developers.kakao.com/docs/latest/ko/local/dev-guide
    public AddressDto getAddressFromCoords(
            String longitude,
            String latitude
    ) {
        try {
            AddressResponse response = kakaoWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/geo/coord2address.json")
                            .queryParam("x", longitude)
                            .queryParam("y", latitude)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(AddressResponse.class)
                    .block();

            if ( response == null || response.getDocuments().isEmpty()) {
                return null;
            }

            AddressResponse.Document doc = response.getDocuments().get(0);
            AddressResponse.Address address = doc.getAddress();
            AddressResponse.RoadAddress roadAddress = doc.getRoad_address();

            String roadAddressName = roadAddress != null ? roadAddress.getRoad_name() : "";
            String zoneNo = roadAddress != null ? roadAddress.getZone_no() : "";

            return new AddressDto(
                    address.getAddress_name(),
                    roadAddressName,
                    zoneNo
            );
        } catch (Exception e) {
            log.error("카카오맵 API 주소 조회 실패 : {}", e.getMessage(), e);
            return null;
        }
    }
}
