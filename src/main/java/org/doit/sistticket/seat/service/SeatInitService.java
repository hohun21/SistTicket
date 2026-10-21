package org.doit.sistticket.seat.service;

import java.util.List;

import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.entity.Seat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.doit.sistticket.seat.repository.SeatRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatInitService {

	private final SeatRepository seatRepository;
	private final PerformanceSeatRepository performanceSeatRepository;

	// 회차 등록 서비스의 트랜잭션 안에서 호출
    @Transactional(propagation = Propagation.MANDATORY)
    public void createSeats(Performances performance) {

        if (performance == null || performance.getPerformanceId() <= 0) {
            throw new IllegalArgumentException("먼저 공연 회차를 저장해 주세요.");
        }

        Long performanceId = performance.getPerformanceId();

        if (performanceSeatRepository
                .existsByPerformance_PerformanceId(performanceId)) {
            throw new IllegalStateException("이미 좌석이 생성된 공연 회차입니다.");
        }

        // 현재 프로젝트의 공통 기본 좌석 조회
        List<Seat> seats = seatRepository.findAll(
                Sort.by("rowLabel", "seatNumber"));

        if (seats.isEmpty()) {
            throw new IllegalStateException("등록된 기본 좌석이 없습니다.");
        }

        // 각 기본 좌석을 새 회차와 연결
        List<PerformanceSeat> performanceSeats = seats.stream()
                .map(seat -> PerformanceSeat.create(performance, seat))
                .toList();

        // 회차별 좌석을 DB에 저장
        performanceSeatRepository.saveAll(performanceSeats);
    }
	
}
