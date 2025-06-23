package com.nowwhere.nowwhere_back.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
            String urlStr = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos" +
                    "?serviceKey=" + dataGoApiKey +
                    "&tmX=" + lonStr +
                    "&tmY=" + latStr +
                    "&radius=" + radius;

            URI uri = new URI(urlStr);
            WebClient webClient = WebClient.builder().build();
            String responseXml = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            XmlMapper xmlMapper = new XmlMapper();
            NearBusStopResponse response = xmlMapper.readValue(responseXml, NearBusStopResponse.class);

            if (response.getMsgBody() == null || response.getMsgBody().getItemList() == null) {
                log.warn("msgBody or itemList is null");
                return List.of(); // 빈 리스트 반환
            }

            return response.getMsgBody().getItemList().stream()
                    .map(busStop -> new NearBusStopDto(
                            busStop.getStationId(),
                            busStop.getStationNm(),
                            busStop.getArsId(),
                            Double.parseDouble(busStop.getPosX()),
                            Double.parseDouble(busStop.getPosY()),
                            Double.parseDouble(busStop.getGpsX()),
                            Double.parseDouble(busStop.getGpsY()),
                            Integer.parseInt(busStop.getDist())
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("공공데이터 버스 API 조회 실패 : {}", e.getMessage(), e);
            return List.of();
        }
    }
}
