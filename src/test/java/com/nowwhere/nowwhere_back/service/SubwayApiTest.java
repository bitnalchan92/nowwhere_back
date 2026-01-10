package com.nowwhere.nowwhere_back.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 서울시 지하철 실시간 위치 API 테스트
 */
public class SubwayApiTest {

    private static final String API_KEY = "4f59596d7762697435374579414a79";

    @Test
    void testVariousApiEndpoints() {
        System.out.println("========================================");
        System.out.println("다양한 API 엔드포인트 테스트");
        System.out.println("========================================\n");

        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();

        // 1. realtimePosition (기존)
        testEndpoint(webClient, "realtimePosition", "2호선");

        // 2. realtimeStationArrival (도착정보)
        testEndpoint(webClient, "realtimeStationArrival", "강남");

        // 3. SearchSTNBySubwayLineInfo (역정보)
        testEndpoint(webClient, "SearchSTNBySubwayLineInfo", "1002");

        // 4. SearchInfoBySubwayNameService
        testEndpoint(webClient, "SearchInfoBySubwayNameService", "2호선");
    }

    @Test
    void testRealtimeArrivalApi() {
        System.out.println("========================================");
        System.out.println("실시간 도착정보 API 테스트");
        System.out.println("========================================\n");

        // 실시간 도착 정보는 역 이름으로 조회
        List<String> stations = List.of("강남", "홍대입구", "잠실", "서울역");

        for (String station : stations) {
            String encodedStation = URLEncoder.encode(station, StandardCharsets.UTF_8);
            String url = "http://swopenapi.seoul.go.kr/api/subway/" + API_KEY + "/json/realtimeStationArrival/0/10/" + encodedStation;

            System.out.println("역: " + station);
            System.out.println("URL: " + url);

            try {
                WebClient webClient = WebClient.builder().build();
                String response = webClient.get()
                        .uri(URI.create(url))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response != null) {
                    System.out.println("응답 길이: " + response.length() + " bytes");
                    if (response.contains("ERROR")) {
                        System.out.println("결과: 실패");
                    } else {
                        System.out.println("결과: 성공!");
                        System.out.println("응답 미리보기: " + response.substring(0, Math.min(200, response.length())));
                    }
                }
            } catch (Exception e) {
                System.out.println("예외: " + e.getMessage());
            }
            System.out.println();
        }
    }

    @Test
    void testCorrectRealtimePositionApi() {
        System.out.println("========================================");
        System.out.println("실시간 열차 위치 API (올바른 도메인)");
        System.out.println("========================================\n");

        // swopenapi.seoul.go.kr 도메인 사용
        List<String> lines = List.of("1호선", "2호선", "3호선");

        for (String line : lines) {
            String encodedLine = URLEncoder.encode(line, StandardCharsets.UTF_8);

            // swopenapi 도메인 사용
            String url = "http://swopenapi.seoul.go.kr/api/subway/" + API_KEY + "/json/realtimePosition/0/100/" + encodedLine;

            System.out.println("호선: " + line);
            System.out.println("URL: " + url);

            try {
                WebClient webClient = WebClient.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build();

                String response = webClient.get()
                        .uri(URI.create(url))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response != null) {
                    System.out.println("응답 길이: " + response.length() + " bytes");
                    if (response.contains("ERROR")) {
                        System.out.println("결과: 실패 - " + extractError(response));
                    } else if (response.contains("realtimePositionList")) {
                        int count = countOccurrences(response, "trainNo");
                        System.out.println("결과: 성공! 열차 " + count + "대");
                    } else {
                        System.out.println("응답: " + response.substring(0, Math.min(300, response.length())));
                    }
                }
            } catch (Exception e) {
                System.out.println("예외: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
            System.out.println();
        }
    }

    @Test
    void testAllDomainsAndPaths() {
        System.out.println("========================================");
        System.out.println("모든 도메인/경로 조합 테스트");
        System.out.println("========================================\n");

        String line = "2호선";
        String encodedLine = URLEncoder.encode(line, StandardCharsets.UTF_8);

        // 다양한 URL 패턴 시도
        List<String> urls = List.of(
                // openapi.seoul.go.kr 도메인
                "http://openapi.seoul.go.kr:8088/" + API_KEY + "/json/realtimePosition/0/100/" + encodedLine,

                // swopenapi.seoul.go.kr 도메인 (지하철 전용)
                "http://swopenapi.seoul.go.kr/api/subway/" + API_KEY + "/json/realtimePosition/0/100/" + encodedLine,

                // 다른 API 이름들
                "http://openapi.seoul.go.kr:8088/" + API_KEY + "/json/subwayRealtimePosition/0/100/" + encodedLine,

                // HTTPS
                "https://swopenapi.seoul.go.kr/api/subway/" + API_KEY + "/json/realtimePosition/0/100/" + encodedLine
        );

        int index = 1;
        for (String url : urls) {
            System.out.println("테스트 #" + index++);
            System.out.println("URL: " + url);

            try {
                WebClient webClient = WebClient.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build();

                String response = webClient.get()
                        .uri(URI.create(url))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                if (response != null) {
                    System.out.println("응답 길이: " + response.length() + " bytes");
                    if (response.length() < 200) {
                        System.out.println("응답: " + response);
                    } else if (response.contains("trainNo")) {
                        System.out.println("결과: 성공! (열차 정보 포함)");
                    } else if (response.contains("ERROR")) {
                        System.out.println("결과: 에러 - " + extractError(response));
                    } else {
                        System.out.println("결과: " + response.substring(0, 200));
                    }
                }
            } catch (Exception e) {
                System.out.println("예외: " + e.getClass().getSimpleName());
            }
            System.out.println();
        }
    }

    private void testEndpoint(WebClient webClient, String apiName, String param) {
        String encodedParam = URLEncoder.encode(param, StandardCharsets.UTF_8);
        String url = "http://openapi.seoul.go.kr:8088/" + API_KEY + "/json/" + apiName + "/0/10/" + encodedParam;

        System.out.println("API: " + apiName + " / 파라미터: " + param);
        System.out.println("URL: " + url);

        try {
            String response = webClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                System.out.println("응답 길이: " + response.length() + " bytes");
                System.out.println("응답 미리보기: " + response.substring(0, Math.min(150, response.length())));
            }
        } catch (Exception e) {
            System.out.println("예외: " + e.getMessage());
        }
        System.out.println();
    }

    private String extractError(String response) {
        int start = response.indexOf("\"CODE\"");
        if (start > 0) {
            int end = response.indexOf("}", start);
            return response.substring(start, Math.min(end, start + 100));
        }
        return response.substring(0, Math.min(100, response.length()));
    }

    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
