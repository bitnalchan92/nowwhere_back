package com.nowwhere.nowwhere_back.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nowwhere.nowwhere_back.config.WebClientConfig;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
class BusServiceTest {
    @Value("${api.key.datago}")
    String dataGoApiKey;

    @Test
    public void callDataGoBusApiManually() throws Exception {
        double tmX = 127.040973;
        double tmY = 37.664542;
        int radius = 200;

        StringBuilder urlBuilder = new StringBuilder("http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + dataGoApiKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("tmX", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(tmX), StandardCharsets.UTF_8)); /*기준위치 X*/
        urlBuilder.append("&" + URLEncoder.encode("tmY", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(tmY), StandardCharsets.UTF_8)); /*기준위치 Y*/
        urlBuilder.append("&" + URLEncoder.encode("radius", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(radius), StandardCharsets.UTF_8)); /*단위 m(미터)*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        System.out.println(sb.toString());
    }
}