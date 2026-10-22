package org.doit.sistticket.seat.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.orders.repository.OrdersRepository;
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
    private final MemberRepository memberRepository;
    private final OrdersRepository ordersRepository;

    // 좌석 선점
    @Transactional
    public SeatHoldResponse holdSeats(
            Long performanceId
            , List<Long> performanceSeatIds
            , Long memberId) {

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
        LocalDateTime expiresAt = now.plusMinutes(30);
        
        Members member = memberRepository.findById(memberId)
        								.orElseThrow( () -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        for (PerformanceSeat seat : seats) {
            seat.hold(holdToken, expiresAt, member);
        }

        return new SeatHoldResponse(
                holdToken, performanceSeatIds, expiresAt);
    }
    
    // 좌석 선점 취소
    @Transactional
    public void cancelHold( Long performanceId, String holdToken, Long memberId) {

        if (performanceId == null
                || memberId == null
                || holdToken == null
                || holdToken.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "선점 취소 요청이 올바르지 않습니다.");
        }

        List<PerformanceSeat> seats = performanceSeatRepository.findByHoldTokenForUpdate(holdToken);

        // 이미 해제됐거나 토큰이 바뀌었다면 완료 처리.
        // 뒤로가기나 재시도로 같은 취소 요청이 와도 안전하게 처리한다.
        if (seats.isEmpty()) {
            return;
        }

        for (PerformanceSeat seat : seats) {
            if (seat.getPerformance().getPerformanceId()!= performanceId.longValue()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "해당 공연 회차의 선점이 아닙니다.");
            }

            if (seat.getHoldMember() == null
                    || !seat.getHoldMember()
                            .getMemberId().equals(memberId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "본인의 선점이 아닙니다.");
            }

            if (!"HELD".equals(seat.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "이미 상태가 변경된 좌석입니다.");
            }
        }

        // 현재 토큰으로 주문이 생성됐다면 여기서 해제하지 않는다.
        if (ordersRepository.existsByHoldToken(holdToken)) {
            throw new ResponseStatusException(
             HttpStatus.CONFLICT, "이미 주문이 생성된 좌석입니다. 주문 취소 기능을 이용해 주세요.");
        }

        seats.forEach(PerformanceSeat::release);
    }
}