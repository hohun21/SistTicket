package org.doit.sistticket.seat.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.doit.sistticket.seat.dto.SeatHoldResponse;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final PerformanceSeatRepository performanceSeatRepository;

    // 좌석 선점
    @Transactional
    public SeatHoldResponse holdSeats(
            Long performanceId, List<Long> performanceSeatIds) {

        if (performanceSeatIds == null || performanceSeatIds.isEmpty()
                || performanceSeatIds.contains(null)
                || new HashSet<>(performanceSeatIds).size() != performanceSeatIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "좌석 목록이 올바르지 않습니다.");
        }

        List<PerformanceSeat> seats =
                performanceSeatRepository.findSeatsForHold(
                        performanceId, performanceSeatIds);

        if (seats.size() != performanceSeatIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "해당 공연 회차의 좌석을 찾을 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        for (PerformanceSeat seat : seats) {
            if (!seat.canHold(now)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "이미 선점되거나 예약된 좌석입니다.");
            }
        }

        // 선점 토큰, 선점 기한 발급
        String holdToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = now.plusMinutes(5);

        for (PerformanceSeat seat : seats) {
            seat.hold(holdToken, expiresAt);
        }

        return new SeatHoldResponse(
                holdToken, performanceSeatIds, expiresAt);
    }
}