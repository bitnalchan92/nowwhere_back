package com.nowwhere.nowwhere_back.controller;

import com.nowwhere.nowwhere_back.domain.dto.subway.SubwayTrainPositionDto;
import com.nowwhere.nowwhere_back.service.SubwayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subway")
@RequiredArgsConstructor
@Slf4j
public class SubwayController {

    private final SubwayService subwayService;

    /**
     * 특정 호선의 실시간 열차 위치 조회
     * GET /api/subway/realtimePosition?line={호선명}
     * 예: /api/subway/realtimePosition?line=2호선
     */
    @GetMapping("/realtimePosition")
    public ResponseEntity<List<SubwayTrainPositionDto>> getRealtimePosition(
            @RequestParam String line
    ) {
        log.info("지하철 실시간 위치 조회 요청 - 노선: {}", line);
        List<SubwayTrainPositionDto> positions = subwayService.getRealtimePosition(line);
        return ResponseEntity.ok(positions);
    }

    /**
     * 전체 호선의 실시간 열차 위치 조회
     * GET /api/subway/realtimePosition/all
     */
    @GetMapping("/realtimePosition/all")
    public ResponseEntity<Map<String, List<SubwayTrainPositionDto>>> getAllRealtimePosition() {
        log.info("전체 호선 실시간 위치 조회 요청");
        Map<String, List<SubwayTrainPositionDto>> allPositions = subwayService.getAllLinesPosition();
        return ResponseEntity.ok(allPositions);
    }

    /**
     * 지원하는 호선 목록 조회
     * GET /api/subway/lines
     */
    @GetMapping("/lines")
    public ResponseEntity<List<String>> getSupportedLines() {
        return ResponseEntity.ok(subwayService.getSupportedLines());
    }
}
