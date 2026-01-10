package com.nowwhere.nowwhere_back.domain.dto.subway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubwayTrainPositionDto {
    private String subwayId;      // 호선 ID
    private String subwayNm;      // 호선명
    private String statnId;       // 역 ID
    private String statnNm;       // 현재역명
    private String trainNo;       // 열차번호
    private String updnLine;      // 상하행선 (0: 상행, 1: 하행)
    private String statnTnm;      // 종착역명
    private String trainSttus;    // 열차상태 (0: 진입, 1: 도착, 2: 출발)
    private String directAt;      // 급행여부 (0: 일반, 1: 급행)
    private String lstcarAt;      // 막차여부 (0: 아님, 1: 막차)
    private String recptnDt;      // 데이터 수신 시각
}
