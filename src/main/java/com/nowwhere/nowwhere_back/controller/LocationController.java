package com.nowwhere.nowwhere_back.controller;

import com.nowwhere.nowwhere_back.domain.dto.location.AddressDto;
import com.nowwhere.nowwhere_back.service.LocationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/addressInfo")
    public ResponseEntity<AddressDto> getAddressFromCoords(
            @RequestParam String longitude,
            @RequestParam String latitude,
            HttpSession session
    ) {
        session.setAttribute("lat", latitude);
        session.setAttribute("lon", longitude);

        AddressDto addressDto = locationService.getAddressFromCoords(longitude, latitude);

        if (addressDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(addressDto);
    }
}
