package com.nowwhere.nowwhere_back.domain.dto.bus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearBusStopDto {
    private String stationId; // 정류소 고유 ID
    private String stationNm; // 정류소명
    private String arsId; // 정류소고유번호 (정류소 번호)
    private double posX; // 정류소 좌표X (GRS80)
    private double posY; // 정류소 좌표Y (GRS80)
    private double gpsX; // 정류소 좌표X (WGS84)
    private double gpsY; // 정류소 좌표Y (WGS84)
    private int dist; // 거리(m)
    private List<BusRouteDto> routes;
}