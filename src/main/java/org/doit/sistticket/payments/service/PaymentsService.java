package org.doit.sistticket.payments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.orders.entity.Orders;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.doit.sistticket.orders.repository.OrdersDetailRepository;
import org.doit.sistticket.orders.repository.OrdersRepository;
import org.doit.sistticket.payments.entity.Payments;
import org.doit.sistticket.payments.enums.PaymentStatus;
import org.doit.sistticket.payments.repository.PaymentsRepository;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.doit.sistticket.seat.service.PerformanceSeatStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final PerformanceSeatStatusService performanceSeatStatusService;
    private final OrdersRepository ordersRepository;

    @Transactional
    public void completePayment(Long paymentId, String paymentKey) {

    	// 1. 결제 정보로 주문 ID 확인 (일반 조회)
    	Payments paymentInfo = paymentsRepository.findById(paymentId)
    	        .orElseThrow(() ->
    	                new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

    	Long orderId = paymentInfo.getOrders().getOrdersId();

    	// 2. 주문 행 잠금
    	Orders order = ordersRepository.findByIdForUpdate(orderId)
    	        .orElseThrow(() ->
    	                new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

    	// 3. 결제 행 잠금
    	Payments payment = paymentsRepository.findByIdForUpdate(paymentId)
    	        .orElseThrow(() ->
    	                new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

    	// 4. 잠금 후 결제 상태 재확인
    	if (!payment.getOrders().getOrdersId().equals(order.getOrdersId())) {
    	    throw new IllegalStateException("결제와 주문 정보가 일치하지 않습니다.");
    	}

    	if (payment.getStatus() != PaymentStatus.PENDING) {
    	    throw new IllegalStateException("결제 대기 상태가 아닙니다.");
    	}

        // 2. 주문 정보 확인
        List<OrdersDetail> details =
                ordersDetailRepository.findByOrders_OrdersId(orderId);

        if (details.isEmpty()) {
            throw new IllegalStateException("주문 상세 정보가 없습니다.");
        }

        // 주문 생성 당시 저장한 홀드 정보 확인
        String holdToken = order.getHoldToken();
        LocalDateTime now = LocalDateTime.now();

        if (holdToken == null || holdToken.isBlank()
                || order.getHoldExpiresAt() == null
                || !order.getHoldExpiresAt().isAfter(now)) {
            throw new IllegalStateException(
                    "좌석 선점이 만료되었거나 유효하지 않습니다.");
        }

        // 3. DB 상태 변경 
        // 좌석 상태 HELD → RESERVED
        performanceSeatStatusService.confirmAfterPayment( orderId, holdToken );
        
        // 주문 상태 PENDING → CONFIRMED
        for (OrdersDetail detail : details) { detail.confirm(); }

        // 결제 상태 PENDING → SUCCESS
        payment.completePayment( paymentKey, LocalDateTime.now());
        
    } // completePayment
    
    @Transactional
    public void failPayment(Long paymentId) {

        Payments payment = paymentsRepository
                .findByIdForUpdate(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "결제 대기 상태인 경우에만 실패 처리할 수 있습니다.");
        }

        payment.failPayment();
        
    } // failPayment
    
    @Transactional
    public void retryPayment(Long paymentId) {
    		// 1. 결제 정보를 조회해 주문ID 확인
    		Payments paymentInfo = paymentsRepository.findById(paymentId)
    												.orElseThrow( () -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
    		
    		Long orderId = paymentInfo.getOrders().getOrdersId();
    		
    		// 2. 주문 먼저 잠금
    		Orders order = ordersRepository.findByIdForUpdate(orderId)
    									   .orElseThrow( () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));
    		
    		// 3. 결제 잠금
    		Payments payment = paymentsRepository.findByIdForUpdate(paymentId)
    											.orElseThrow( () -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
    		
    		// 4. 주문과 결제 연결 확인
    		if (!payment.getOrders().getOrdersId().equals(order.getOrdersId())) {
				throw new IllegalStateException("결제와 주문 정보가 일치하지 않습니다.");
			}
    		
    		// 5. 실패한 결제인지 확인
    		if (payment.getStatus() != PaymentStatus.FAILED) {
				throw new IllegalStateException("실패한 결제만 재시도할 수 있습니다.");
			}
    		
    		// 6. 주문 상세 확인
    		List<OrdersDetail> details = ordersDetailRepository.findByOrders_OrdersId(orderId);
    		
    		if (details.isEmpty()) {
				throw new IllegalStateException("주문 상세 정보가 없습니다.");
			}
    		
    		for(OrdersDetail detail : details) {
    			if (detail.getStatus() != OrdersDetailStatus.PENDING) {
					throw new IllegalStateException("주문이 대기 상태가 아니므로 재시도할 수 없습니다.");
				}
    		}
    		
    		// 7. 좌석 선점 상태 검증
    		String holdToken = order.getHoldToken();

    		if (holdToken == null || holdToken.isBlank()
    		        || order.getHoldExpiresAt() == null
    		        || !order.getHoldExpiresAt().isAfter(LocalDateTime.now())) {

    		    throw new IllegalStateException(
    		            "좌석 선점이 만료되어 결제를 재시도할 수 없습니다.");
    		}

    		performanceSeatStatusService.validateHoldForRetry(
    		        orderId, holdToken);

    		// 8. FAILED → PENDING
    		payment.retryPayment();
    } // retryPayment
    
} // service










