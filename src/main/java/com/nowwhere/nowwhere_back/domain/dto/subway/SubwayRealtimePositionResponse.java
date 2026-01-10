package com.nowwhere.nowwhere_back.domain.dto.subway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubwayRealtimePositionResponse {

    @JsonProperty("errorMessage")
    private ErrorMessage errorMessage;

    @JsonProperty("realtimePositionList")
    private List<TrainPosition> realtimePositionList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorMessage {
        private String status;
        private String code;
        private String message;
        private String link;
        private String developerMessage;
        private Integer total;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainPosition {
        private String subwayId;      // 호선 ID (1002: 2호선 등)
        private String subwayNm;      // 호선명
        private String statnId;       // 역 ID
        private String statnNm;       // 역명
        private String trainNo;       // 열차번호
        private String lastRecptnDt;  // 최종 수신 일시
        private String recptnDt;      // 수신 일시
        private String updnLine;      // 상하행선 구분 (0: 상행, 1: 하행)
        private String statnTid;      // 종착역 ID
        private String statnTnm;      // 종착역명
        private String trainSttus;    // 열차상태 (0: 진입, 1: 도착, 2: 출발)
        private String directAt;      // 급행여부 (0: 일반, 1: 급행)
        private String lstcarAt;      // 막차여부 (0: 아님, 1: 막차)
    }
}
