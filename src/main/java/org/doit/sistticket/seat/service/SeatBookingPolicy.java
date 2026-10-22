package org.doit.sistticket.seat.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.ConcertStatus;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.entity.PerformanceStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SeatBookingPolicy {

    public static final int MAX_SEATS = 5;

    // 선점 요청 및 주문 하나에 포함할 수 있는 좌석 검증
    public void validateSeats(List<Long> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "좌석을 선택해 주세요.");
        }

        if (seatIds.size() > MAX_SEATS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "예매 한도 초과.");
        }

        if (seatIds.stream().anyMatch(id -> id == null || id <= 0)
                || new HashSet<>(seatIds).size() != seatIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "좌석 목록이 올바르지 않습니다.");
        }
    }

    // 공연 상태·예매 기간·공연 시작 시각 검증
    public void validatePerformance(
            Performances performance,
            LocalDateTime now) {

        if (performance == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "공연 회차를 찾을 수 없습니다.");
        }

        Concert concert = performance.getConcert();

        if (concert == null || concert.getStatus() == null
                || performance.getStatus() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "공연 예매 상태를 확인해 주세요.");
        }

        if (concert.getStatus() == ConcertStatus.ISCANCELED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "취소된 공연은 예매할 수 없습니다.");
        }

        if (concert.getStatus() == ConcertStatus.FINISHED
                || performance.getStatus() == PerformanceStatus.FINISHED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "종료된 공연은 예매할 수 없습니다.");
        }

        LocalDateTime showTime = performance.getPerformanceDate();
        LocalDateTime start = performance.getBookingStart();
        LocalDateTime end = performance.getBookingEnd();

        if (showTime == null || start == null || end == null
                || !start.isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "공연 예매 일정이 올바르지 않습니다.");
        }

        if (!now.isBefore(showTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "이미 시작된 공연은 예매할 수 없습니다.");
        }

        if (now.isBefore(start)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "아직 예매가 시작되지 않았습니다.");
        }

        if (!now.isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "예매가 마감되었습니다.");
        }
    }
}