package org.doit.sistticket.seat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.doit.sistticket.orders.repository.OrdersDetailRepository;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceSeatStatusService {

    private final PerformanceSeatRepository performanceSeatRepository;
    private final OrdersDetailRepository ordersDetailRepository;

    // 결제 성공을 확인한 뒤 호출하도록 만든 메서드
    //  - 토큰에 해당하는 좌석을 조회·검증하고, 좌석들이 주문 상세의 좌석과 일치하는지 확인 후,  
    //    좌석을 HELD에서 RESERVED로 바꾸는 메서드
    @Transactional
    public void confirmAfterPayment(Long orderId, String holdToken) {
        if (orderId == null || holdToken == null || holdToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "주문 ID 또는 선점 토큰이 없습니다.");
        }

        // 현재 토큰으로 선점된 좌석을 잠근다.
        List<PerformanceSeat> seats =
                performanceSeatRepository.findByHoldTokenForUpdate(holdToken);

        if (seats.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "선점된 좌석을 찾을 수 없습니다.");
        }

        List<OrdersDetail> details =
                ordersDetailRepository.findByOrders_OrdersId(orderId);

        if (details.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "주문 상세를 찾을 수 없습니다.");
        }

        List<Long> orderSeatIds = details.stream()
                .map(detail -> detail.getPerformanceSeat().getPerformanceSeatId())
                .sorted()
                .toList();

        List<Long> heldSeatIds = seats.stream()
                .map(PerformanceSeat::getPerformanceSeatId)
                .sorted()
                .toList();

        if (!orderSeatIds.equals(heldSeatIds)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "주문 좌석과 선점 좌석이 다릅니다.");
        }

        Long orderMemberId = details.get(0)
                .getOrders().getMember().getMemberId();
        LocalDateTime now = LocalDateTime.now();

        for (OrdersDetail detail : details) {
            if (detail.getStatus() != OrdersDetailStatus.PENDING) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "결제 대기 중인 주문이 아닙니다.");
            }
        }

        for (PerformanceSeat seat : seats) {
            if (!seat.canConfirm(holdToken, now)
                    || seat.getHoldMember() == null
                    || !seat.getHoldMember().getMemberId().equals(orderMemberId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "유효한 본인 선점이 아닙니다.");
            }
        }

        seats.forEach(PerformanceSeat::confirm);
    }
    
    // 결제 실패·취소·만료 시 현재 토큰의 좌석 상태를 AVAILABLE로 변환하는 메서드
    @Transactional
    public boolean releaseHeldSeats(Long orderId, String holdToken) {
        if (orderId == null || holdToken == null || holdToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "주문 ID 또는 선점 토큰이 없습니다.");
        }

        // 토큰이 아직 붙어 있는 좌석만 잠근다.
        List<PerformanceSeat> seats =
                performanceSeatRepository.findByHoldTokenForUpdate(holdToken);

        // 이미 해제됐거나 다른 토큰으로 다시 선점됐다면 건드리지 않는다.
        if (seats.isEmpty()) {
            return false;
        }

        List<OrdersDetail> details =
                ordersDetailRepository.findByOrders_OrdersId(orderId);

        if (details.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "주문 상세를 찾을 수 없습니다.");
        }

        List<Long> orderSeatIds = details.stream()
                .map(d -> d.getPerformanceSeat().getPerformanceSeatId())
                .sorted()
                .toList();

        List<Long> heldSeatIds = seats.stream()
                .map(PerformanceSeat::getPerformanceSeatId)
                .sorted()
                .toList();

        if (!orderSeatIds.equals(heldSeatIds)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "주문 좌석과 선점 좌석이 다릅니다.");
        }

        Long orderMemberId = details.get(0)
                .getOrders().getMember().getMemberId();

        for (OrdersDetail detail : details) {
            if (detail.getStatus() != OrdersDetailStatus.PENDING) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "결제 대기 중인 주문이 아닙니다.");
            }
        }

        for (PerformanceSeat seat : seats) {
            if (!"HELD".equals(seat.getStatus())
                    || !holdToken.equals(seat.getHoldToken())
                    || seat.getHoldMember() == null
                    || !seat.getHoldMember().getMemberId().equals(orderMemberId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "현재 주문의 선점이 아닙니다.");
            }
        }

        seats.forEach(PerformanceSeat::release);
        return true;
    }
    
    // 주문 좌석들의 현재 선점 토큰을 조회하고 일치 여부 확인
    public String getHoldToken(List<OrdersDetail> details) {

        if (details == null || details.isEmpty()) {
            throw new IllegalStateException(
                    "주문 상세 정보가 없습니다.");
        }

        List<String> tokens = details.stream()
                .map(detail ->
                        detail.getPerformanceSeat().getHoldToken())
                .distinct()
                .toList();

        if (tokens.size() != 1
                || tokens.get(0) == null
                || tokens.get(0).isBlank()) {
            throw new IllegalStateException(
                    "주문 좌석의 선점 토큰이 없거나 일치하지 않습니다.");
        }

        return tokens.get(0);
    }
    
    // 결제 재시도 이전에 좌석 선점 상태만 검증 - 좌석상태 변경 X
    public void validateHoldForRetry(Long orderId, String holdToken) {
    		if (orderId == null || holdToken == null || holdToken.isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "주문 ID 또는 선점 토큰이 없습니다.");
			}
    		
    		// 1. 해당 토큰으로 선점된 좌석을 잠근다.
    		List<PerformanceSeat> seats = performanceSeatRepository.findByHoldTokenForUpdate(holdToken);
    		
    		if (seats.isEmpty()) {
    			throw new ResponseStatusException(HttpStatus.CONFLICT, "선점된 좌석을 찾을 수 없습니다.");
			}
    		
    		// 2. 주문 상세를 조회한다.
    		List<OrdersDetail> details = ordersDetailRepository.findByOrders_OrdersId(orderId);
    		
    		if (details.isEmpty()) {
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문 상세를 찾을 수 없습니다.");
			}
    		
    		// 3. 모든 주문 상세가 결제 대기 상태인지 확인한다.
    		for(OrdersDetail detail : details) {
    			if (detail.getStatus() != OrdersDetailStatus.PENDING) {
    				throw new ResponseStatusException(HttpStatus.CONFLICT, "결제 대기 중인 주문이 아니므로 재시도할 수 없습니다.");
				}
    		}
    		
    		// 4. 주문 좌석 ID와 현재 선점 좌석 ID를 비교한다.
    		List<Long> orderSeatIds = details.stream().map(detail -> detail.getPerformanceSeat().getPerformanceSeatId())
    												 .sorted().toList();
    		
    		List<Long> heldSeatIds = seats.stream().map(PerformanceSeat::getPerformanceSeatId)
					 .sorted().toList();
    		
    		if (!orderSeatIds.equals(heldSeatIds)) {
    			throw new ResponseStatusException(HttpStatus.CONFLICT, "주문좌석과 선점 좌석이 다릅니다.");
			}
    		
    	    // 5. 주문 회원과 좌석 선점 회원을 비교한다.
    	    Long orderMemberId = details.get(0)
    	            .getOrders().getMember().getMemberId();

    	    LocalDateTime now = LocalDateTime.now();

    	    for (PerformanceSeat seat : seats) {
    	        if (!seat.canConfirm(holdToken, now)
    	                || seat.getHoldMember() == null
    	                || !seat.getHoldMember().getMemberId().equals(orderMemberId)) {

    	            throw new ResponseStatusException(
    	                    HttpStatus.CONFLICT,
    	                    "좌석 선점이 만료되었거나 현재 주문의 선점이 아닙니다.");
    	        }
    	    }
    } // validateHoldForRetry
    
} // PerformanceSeatStatusService














