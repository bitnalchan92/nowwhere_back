package com.nowwhere.nowwhere_back.domain.dto.kakao;

import lombok.Getter;

import java.util.List;

@Getter
public class KakaoAddressResponse {
    private Meta meta; // 응답 관련 정보
    private List<Document> documents; // 응답 결과

    @Getter
    public static class Meta {
        private int total_count; // 변환된 지번 주소 및 도로명 주소 의 개수, 0 또는 1
    }

    @Getter
    public static class Document {
        private Address address; // 지번 주소 상세 정보, 아래 Address 참고
        private RoadAddress road_address; // 도로명 주소 상세 정보, 아래 RoadAddress 참고
    }

    @Getter
    public static class Address {
        private String address_name; // 전체 지번 주소
        private String region_1depth_name; // 지역 1Depth명, 시도 단위
        private String region_2depth_name; // 지역 2Depth명, 구 단위
        private String region_3depth_name; // 지역 3Depth명, 동 단위
        private String mountain_yn; // 산 여부, Y 또는 N
        private String main_address_no; // 지번 주 번지
        private String sub_address_no; // 지번 부 번지, 없을 경우 빈 문자열("") 반환
    }

    @Getter
    public static class RoadAddress {
        private String address_name; // 전체 도로명 주소
        private String region_1depth_name; // 지역 1Depth, 시도 단위
        private String region_2depth_name; // 지역 2Depth, 구 단위
        private String region_3depth_name; // 지역 3Depth, 면 단위
        private String road_name; // 도로명
        private String underground_yn; // 지하 여부, Y 또는 N
        private String main_building_no; // 건물 본번
        private String sub_building_no; // 건물 부번, 없을 경우 빈 문자열("") 반환
        private String building_name; // 건물 이름
        private String zone_no; // 우편번호(5자리)
    }
}
