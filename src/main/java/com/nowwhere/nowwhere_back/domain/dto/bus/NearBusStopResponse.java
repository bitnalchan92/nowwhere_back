package com.nowwhere.nowwhere_back.domain.dto.bus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NearBusStopResponse {
    @JacksonXmlProperty(localName = "msgBody")
    private MsgBody msgBody;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgBody {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "itemList")
        private List<BusStop> itemList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusStop {
        private String arsId; // 정류소고유번호 (정류소 번호)
        private String posX; // 정류소 좌표X (GRS80)
        private String posY; // 정류소 좌표Y (GRS80)
        private String dist; // 거리
        private String gpsX; // 정류소 좌표X (WGS84)
        private String gpsY; // 정류소 좌표Y (WGS84)
        private String stationId; // 정류소 고유 ID
        private String stationNm; // 정류소명
        private String stationTp; // 정류소 타입
    }
}

/**
 * << 정류소 타입 >>
 * - 0 : 공용
 * - 1 : 일반형 시내/농어촌버스
 * - 2 : 좌석형 시내/농어촌버스
 * - 3 : 직행좌석형 시내/농어촌버스
 * - 4 : 일반형 시외버스
 * - 5 : 좌석형 시외버스
 * - 6 : 고속형 시외버스
 * - 7 : 마을버스
 */