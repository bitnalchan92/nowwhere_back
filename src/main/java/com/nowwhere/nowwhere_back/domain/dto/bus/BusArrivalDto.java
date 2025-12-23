package com.nowwhere.nowwhere_back.domain.dto.bus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusArrivalDto {
    private String busRouteId;      // 노선 ID
    private String busRouteNm;      // 노선명 (버스 번호)
    private String arrmsg1;         // 첫 번째 버스 도착 메시지 (예: "5분 후 도착")
    private String arrmsg2;         // 두 번째 버스 도착 메시지
    private Integer traTime1;       // 첫 번째 버스 도착 예정 시간 (초)
    private Integer traTime2;       // 두 번째 버스 도착 예정 시간 (초)
    private Integer sectOrd1;       // 첫 번째 버스 남은 정류장 수
    private Integer sectOrd2;       // 두 번째 버스 남은 정류장 수
}
