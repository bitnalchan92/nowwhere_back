package com.nowwhere.nowwhere_back.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nowwhere.nowwhere_back.domain.dto.bus.BusArrivalDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.BusArrivalResponse;
import com.nowwhere.nowwhere_back.domain.dto.bus.BusRouteDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.BusRouteResponse;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BusService {
    @Value("${api.key.datago}")
    String dataGoApiKey;

    // 참고 : https://www.data.go.kr/iim/api/selectAPIAcountView.do
    public List<NearBusStopDto> getNearBusStops(
            String latStr,
            String lonStr
    ) {
        try {
            int radius = 200;
            // 완전한 절대 URL 사용 (WebClient는 빈에서 주입받아 재사용)
            String fullUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos" +
                    "?serviceKey=" + dataGoApiKey +
                    "&tmX=" + lonStr +
                    "&tmY=" + latStr +
                    "&radius=" + radius;

            log.info("버스 정류장 조회 URL 호출");

            // URI.create()로 생성하면 추가 인코딩을 하지 않음
            String responseXml = WebClient.builder().build()
                    .get()
                    .uri(URI.create(fullUrl))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("API 응답 받음: {} bytes", responseXml != null ? responseXml.length() : 0);

            XmlMapper xmlMapper = new XmlMapper();
            NearBusStopResponse response = xmlMapper.readValue(responseXml, NearBusStopResponse.class);

            if (response.getMsgBody() == null || response.getMsgBody().getItemList() == null) {
                log.warn("msgBody or itemList is null");
                log.warn("응답 내용: {}", responseXml);
                return List.of(); // 빈 리스트 반환
            }

            log.info("정류장 {}개 발견", response.getMsgBody().getItemList().size());

            return response.getMsgBody().getItemList().stream()
                    .filter(busStop -> !busStop.getArsId().equals("0")) // 미정차 정류소 제외!
                    .map(busStop -> {
                        List<BusRouteDto> routes = getBusRoutesByStation(busStop.getArsId());

                        return new NearBusStopDto(
                                busStop.getStationId(),
                                busStop.getStationNm(),
                                busStop.getArsId(),
                                Double.parseDouble(busStop.getPosX()),
                                Double.parseDouble(busStop.getPosY()),
                                Double.parseDouble(busStop.getGpsX()),
                                Double.parseDouble(busStop.getGpsY()),
                                Integer.parseInt(busStop.getDist()),
                                routes
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("공공데이터 버스 API 조회 실패 : {}", e.getMessage(), e);
            return List.of();
        }
    }

    private List<BusRouteDto> getBusRoutesByStation(String arsId) {
        try {
            String fullUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getRouteByStation" +
                    "?serviceKey=" + dataGoApiKey +
                    "&arsId=" + arsId +
                    "&resultType=json";

            BusRouteResponse response = WebClient.builder().build()
                    .get()
                    .uri(URI.create(fullUrl))
                    .retrieve()
                    .bodyToMono(BusRouteResponse.class)
                    .block();

            if (response == null || response.getMsgBody() == null || response.getMsgBody().getItemList() == null) {
                log.warn("msgBody or itemList is null");
                return List.of();
            }

            return response.getMsgBody().getItemList().stream()
                    .map(busRoute -> new BusRouteDto(
                            busRoute.getBusRouteNm()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("공공데이터 버스노선 API 조회 실패 : {}", e.getMessage(), e);
            return List.of();
        }
    }

    // 정류소별 실시간 버스 도착 정보 조회
    public List<BusArrivalDto> getBusArrivalInfo(String arsId) {
        try {
            String fullUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid" +
                    "?serviceKey=" + dataGoApiKey +
                    "&arsId=" + arsId;

            log.info("버스 도착 정보 조회");

            String responseXml = WebClient.builder().build()
                    .get()
                    .uri(URI.create(fullUrl))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            XmlMapper xmlMapper = new XmlMapper();
            BusArrivalResponse response = xmlMapper.readValue(responseXml, BusArrivalResponse.class);

            if (response.getMsgBody() == null || response.getMsgBody().getItemList() == null) {
                log.warn("msgBody or itemList is null for arsId: {}", arsId);
                return List.of();
            }

            return response.getMsgBody().getItemList().stream()
                    .map(arrival -> new BusArrivalDto(
                            arrival.getBusRouteId(),
                            arrival.getRtNm(),
                            arrival.getArrmsg1(),
                            arrival.getArrmsg2(),
                            parseIntegerSafely(arrival.getTraTime1()),
                            parseIntegerSafely(arrival.getTraTime2()),
                            parseIntegerSafely(arrival.getSectOrd1()),
                            parseIntegerSafely(arrival.getSectOrd2())
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("공공데이터 버스 도착정보 API 조회 실패 (arsId: {}) : {}", arsId, e.getMessage(), e);
            return List.of();
        }
    }

    // 문자열을 Integer로 안전하게 변환 (null 또는 빈 문자열이면 null 반환)
    private Integer parseIntegerSafely(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
