package com.nowwhere.nowwhere_back.domain.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoAddressDto {
    private String addressName; // 전체 지번 주소
    private String roadAddressName; // 전체 도로명 주소
    private String zoneNo; // 우편번호(5자리)
}
