package com.nowwhere.nowwhere_back.domain.dto.bus;

import lombok.Data;

import java.util.List;

@Data
public class BusRouteResponse {
    private ComMsgHeader comMsgHeader;
    private MsgHeader msgHeader;
    private MsgBody msgBody;

    @Data
    public static class ComMsgHeader {
        private String responseTime;
        private String responseMsgID;
        private String requestMsgID;
        private String returnCode;
        private String errMsg;
        private String successYN;
    }

    @Data
    public static class MsgHeader {
        private String headerMsg;
        private String headerCd;
        private int itemCount;
    }

    @Data
    public static class MsgBody {
        private List<BusRouteItem> itemList;
    }

    @Data
    public static class BusRouteItem {
        private String busRouteId;
        private String busRouteNm;
        private String busRouteAbrv;
        private String length;
        private String busRouteType;
        private String stBegin;
        private String stEnd;
        private String term;
        private String nextBus;
        private String firstBusTm;
        private String lastBusTm;
        private String firstBusTmLow;
        private String lastBusTmLow;
    }
}
