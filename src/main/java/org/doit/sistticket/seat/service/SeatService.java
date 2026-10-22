package org.doit.sistticket.seat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.seat.dto.SeatPerformanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.doit.sistticket.seat.dto.SeatResponse;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.entity.Seat;
import org.doit.sistticket.seat.entity.SeatGrade;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final PerformanceSeatRepository performanceSeatRepository;

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeats(Long performanceId) {
        LocalDateTime now = LocalDateTime.now();

        return performanceSeatRepository.findSeatsByPerformanceId(performanceId)
                .stream()
                .map(ps -> toResponse(ps, now))
                .toList();
    }

    private SeatResponse toResponse(PerformanceSeat ps, LocalDateTime now) {
        Seat seat = ps.getSeat();
        SeatGrade grade = seat.getSeatGrade();

        // 만료된 선점은 조회 화면에서 선택 가능한 상태로 표시
        String displayedStatus =
                "HELD".equals(ps.getStatus()) && ps.canHold(now)
                        ? "AVAILABLE"
                        : ps.getStatus();

        return new SeatResponse(
                ps.getPerformanceSeatId(),
                seat.getSeatId(),
                seat.getRowLabel(),
                seat.getSeatNumber(),
                grade.getGradeName(),
                grade.getPrice(),
                displayedStatus);
    }
    
    // 좌석 화면 조회 시 공연 정보 
    @Transactional(readOnly = true)
    public SeatPerformanceResponse getPerformanceInfo(Long performanceId) {

        return performanceSeatRepository.findPerformanceInfo(performanceId)
                .orElseThrow(() -> new ResponseStatusException(
                		HttpStatus.NOT_FOUND, "공연 회차 정보를 찾을 수 없습니다."));
    }
    
    // 달력과 시간 선택에 사용할 공연 회차 목록
    @Transactional(readOnly = true)
    public List<SeatPerformanceResponse> getSchedules(Long performanceId) {

        List<SeatPerformanceResponse> schedules =
                performanceSeatRepository.findSchedules(performanceId);

        if (schedules.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "공연 회차 정보를 찾을 수 없습니다."
            );
        }

        return schedules;
    }
}