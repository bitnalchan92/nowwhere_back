package com.nowwhere.nowwhere_back.service;

import com.nowwhere.nowwhere_back.domain.dto.subway.SubwayRealtimePositionResponse;
import com.nowwhere.nowwhere_back.domain.dto.subway.SubwayTrainPositionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubwayService {

    @Value("${api.key.seoul-subway}")
    private String seoulSubwayApiKey;

    // 서울시 지하철 노선 목록 (서울시 열린데이터광장 API 기준)
    private static final List<String> SUBWAY_LINES = List.of(
            "1호선", "2호선", "3호선", "4호선", "5호선",
            "6호선", "7호선", "8호선", "9호선"
    );

    /**
     * 특정 호선의 실시간 열차 위치 정보 조회
     * API: http://swopenapi.seoul.go.kr/api/subway/{KEY}/json/realtimePosition/0/100/{노선명}
     */
    public List<SubwayTrainPositionDto> getRealtimePosition(String lineName) {
        try {
            // 지하철 전용 도메인 사용 (swopenapi.seoul.go.kr)
            String encodedLineName = java.net.URLEncoder.encode(lineName, java.nio.charset.StandardCharsets.UTF_8);
            String fullUrl = "http://swopenapi.seoul.go.kr/api/subway/" + seoulSubwayApiKey +
                    "/json/realtimePosition/0/100/" + encodedLineName;

            log.info("지하철 실시간 위치 조회 - 노선: {}", lineName);

            SubwayRealtimePositionResponse response = WebClient.builder().build()
                    .get()
                    .uri(URI.create(fullUrl))
                    .retrieve()
                    .bodyToMono(SubwayRealtimePositionResponse.class)
                    .block();

            if (response == null) {
                log.warn("API 응답이 null입니다.");
                return List.of();
            }

            // 에러 응답 체크
            if (response.getErrorMessage() != null) {
                String code = response.getErrorMessage().getCode();
                if (code != null && !code.equals("INFO-000")) {
                    log.warn("API 에러: {} - {}", code, response.getErrorMessage().getMessage());
                    return List.of();
                }
            }

            if (response.getRealtimePositionList() == null) {
                log.warn("realtimePositionList가 null입니다.");
                return List.of();
            }

            log.info("{}에서 열차 {}대 발견", lineName, response.getRealtimePositionList().size());

            return response.getRealtimePositionList().stream()
                    .map(train -> new SubwayTrainPositionDto(
                            train.getSubwayId(),
                            train.getSubwayNm(),
                            train.getStatnId(),
                            train.getStatnNm(),
                            train.getTrainNo(),
                            train.getUpdnLine(),
                            train.getStatnTnm(),
                            train.getTrainSttus(),
                            train.getDirectAt(),
                            train.getLstcarAt(),
                            train.getRecptnDt()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("지하철 실시간 위치 API 조회 실패 (노선: {}): {}", lineName, e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 전체 호선의 실시간 열차 위치 정보 조회
     */
    public Map<String, List<SubwayTrainPositionDto>> getAllLinesPosition() {
        Map<String, List<SubwayTrainPositionDto>> result = new HashMap<>();

        for (String line : SUBWAY_LINES) {
            List<SubwayTrainPositionDto> positions = getRealtimePosition(line);
            result.put(line, positions);
        }

        return result;
    }

    /**
     * 지원하는 호선 목록 반환
     */
    public List<String> getSupportedLines() {
        return SUBWAY_LINES;
    }
}
