package org.doit.sistticket.seat.controller;

import java.util.List;

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
}