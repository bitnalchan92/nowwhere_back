package com.nowwhere.nowwhere_back.controller;

import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopDto;
import com.nowwhere.nowwhere_back.service.BusService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
            HttpSession session
    ) {
        String latStr = (String) session.getAttribute("lat");
        String lonStr = (String) session.getAttribute("lon");

        if (latStr == null || lonStr == null) {
             throw new IllegalStateException("위치 정보가 세션에 없습니다.");
        }

        List<NearBusStopDto> nearBusStopDto = busService.getNearBusStops(latStr, lonStr);

        return ResponseEntity.ok(nearBusStopDto);
    }
}
