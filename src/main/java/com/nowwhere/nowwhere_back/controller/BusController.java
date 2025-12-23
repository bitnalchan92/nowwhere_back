package com.nowwhere.nowwhere_back.controller;

import com.nowwhere.nowwhere_back.domain.dto.bus.BusArrivalDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopDto;
import com.nowwhere.nowwhere_back.service.BusService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
@Slf4j
public class BusController {
    private final BusService busService;

    @GetMapping("/nearBusStops")
    public ResponseEntity<List<NearBusStopDto>> getNearBusStops(
            @RequestParam(required = false) String latitude,
            @RequestParam(required = false) String longitude,
            HttpSession session
    ) {
        String latStr = (String) session.getAttribute("lat");
        String lonStr = (String) session.getAttribute("lon");

        // 세션에 위치 정보가 없으면 파라미터에서 가져오기
        if (latStr == null || lonStr == null) {
            if (latitude != null && longitude != null) {
                log.info("세션에 위치 정보 없음 - 파라미터 값 사용: lat={}, lon={}", latitude, longitude);
                latStr = latitude;
                lonStr = longitude;

                // 세션에 저장해서 다음 요청에 대비
                session.setAttribute("lat", latStr);
                session.setAttribute("lon", lonStr);
            } else {
                throw new IllegalStateException("위치 정보가 세션과 파라미터 모두에 없습니다. 위치 정보를 먼저 조회해주세요.");
            }
        } else {
            log.info("세션에서 위치 정보 사용: lat={}, lon={}", latStr, lonStr);
        }

        List<NearBusStopDto> nearBusStopDto = busService.getNearBusStops(latStr, lonStr);

        return ResponseEntity.ok(nearBusStopDto);
    }

    @GetMapping("/arrivalInfo")
    public ResponseEntity<List<BusArrivalDto>> getBusArrivalInfo(
            @RequestParam String arsId
    ) {
        log.info("버스 도착 정보 조회 요청 - arsId: {}", arsId);
        List<BusArrivalDto> arrivalInfo = busService.getBusArrivalInfo(arsId);
        return ResponseEntity.ok(arrivalInfo);
    }
}
