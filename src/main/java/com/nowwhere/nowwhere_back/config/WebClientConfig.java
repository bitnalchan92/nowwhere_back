package com.nowwhere.nowwhere_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient kakaoWebClient(
            @Value("${api.key.kakao}") String kakaoApiKey
    ) {
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                .build();
    }

    @Bean
    public WebClient dataGoWebClient() {
        return WebClient.builder()
                .baseUrl("http://ws.bus.go.kr/api/rest/stationinfo")
                .build();
    }
}