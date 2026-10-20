package org.doit.sistticket.seat.controller;

import org.doit.sistticket.seat.dto.SeatHoldCancelRequest;
import org.doit.sistticket.seat.dto.SeatHoldRequest;
import org.doit.sistticket.seat.dto.SeatHoldResponse;
import org.doit.sistticket.seat.service.SeatHoldService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class SeatHoldApiController {

    private final SeatHoldService seatHoldService;

    // 좌석 선점
    @PostMapping("/{performanceId}/holds")
    @ResponseStatus(HttpStatus.CREATED)
    public SeatHoldResponse holdSeats(
    		@PathVariable("performanceId") Long performanceId,
            @RequestBody SeatHoldRequest request,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {

    	Long memberId = userDetails.getMember().getMemberId();
    	
        return seatHoldService.holdSeats(
                performanceId, request.getPerformanceSeatIds(), memberId);
    }
    
    // 선점 취소
    @PostMapping("/{performanceId}/holds/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelHold(
    		@PathVariable("performanceId") Long performanceId,
            @RequestBody SeatHoldCancelRequest request,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();

        seatHoldService.cancelHold(
                performanceId, request.getHoldToken(), memberId);
    }
}