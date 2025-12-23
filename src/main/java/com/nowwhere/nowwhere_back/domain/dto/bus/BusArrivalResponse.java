package com.nowwhere.nowwhere_back.domain.dto.bus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusArrivalResponse {
    @JacksonXmlProperty(localName = "msgBody")
    private MsgBody msgBody;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgBody {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "itemList")
        private List<BusArrival> itemList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusArrival {
        private String busRouteId;      // 노선 ID
        private String rtNm;            // 노선명 (버스 번호)
        private String arrmsg1;         // 첫 번째 버스 도착 메시지
        private String arrmsg2;         // 두 번째 버스 도착 메시지
        private String traTime1;        // 첫 번째 버스 도착 예정 시간 (초)
        private String traTime2;        // 두 번째 버스 도착 예정 시간 (초)
        private String sectOrd1;        // 첫 번째 버스 남은 정류장 수
        private String sectOrd2;        // 두 번째 버스 남은 정류장 수
    }
}
