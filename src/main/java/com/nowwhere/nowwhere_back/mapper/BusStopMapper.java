package com.nowwhere.nowwhere_back.mapper;

import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopDto;
import com.nowwhere.nowwhere_back.domain.dto.bus.NearBusStopResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusStopMapper {

    public List<NearBusStopDto> toDtoList(NearBusStopResponse response) {
        if (response == null || response.getMsgBody() == null || response.getMsgBody().getItemList() == null) {
            return List.of();
        }

        return response.getMsgBody().getItemList().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private NearBusStopDto toDto(NearBusStopResponse.BusStop stop) {
        return new NearBusStopDto(
                stop.getStationId(),
                stop.getStationNm(),
                stop.getArsId(),
                parseDouble(stop.getPosX()),
                parseDouble(stop.getPosY()),
                parseDouble(stop.getGpsX()),
                parseDouble(stop.getGpsY()),
                parseInt(stop.getDist())
        );
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }
}