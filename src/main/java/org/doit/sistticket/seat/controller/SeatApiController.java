package org.doit.sistticket.seat.controller;

import java.util.List;

import org.doit.sistticket.seat.dto.SeatPerformanceResponse;
import org.doit.sistticket.seat.dto.SeatResponse;
import org.doit.sistticket.seat.service.SeatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class SeatApiController {

    private final SeatService seatService;

    @GetMapping("/{performanceId}/seats")
    public List<SeatResponse> getSeats(
            @PathVariable("performanceId") Long performanceId) {

        return seatService.getSeats(performanceId);
    }
    
    @GetMapping("/{performanceId}/seat-info")
    public SeatPerformanceResponse getPerformanceInfo(
            @PathVariable("performanceId") Long performanceId) {

        return seatService.getPerformanceInfo(performanceId);
    }
    
    // 같은 공연의 회차 목록
    @GetMapping("/{performanceId}/seat-schedules")
    public List<SeatPerformanceResponse> getSchedules(
            @PathVariable("performanceId") Long performanceId) {

        return seatService.getSchedules(performanceId);
    }
}