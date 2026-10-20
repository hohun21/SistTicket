package org.doit.sistticket.payments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.doit.sistticket.orders.repository.OrdersDetailRepository;
import org.doit.sistticket.payments.entity.Payments;
import org.doit.sistticket.payments.enums.PaymentStatus;
import org.doit.sistticket.payments.repository.PaymentsRepository;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final PerformanceSeatRepository performanceSeatRepository;

    @Transactional
    public void completePayment(Long paymentId, String paymentKey) {

        Payments payment = paymentsRepository
                .findByIdForUpdate(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태가 아닙니다.");
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("결제 키가 없습니다.");
        }

        List<OrdersDetail> details =
                ordersDetailRepository.findByOrders_OrdersId(
                        payment.getOrders().getOrdersId());

        if (details.isEmpty()) {
            throw new IllegalStateException("주문 상세 정보가 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        // 모든 좌석을 검증한 뒤 변경한다.
        for (OrdersDetail detail : details) {
            PerformanceSeat seat = performanceSeatRepository
                    .findByIdForUpdate(
                            detail.getPerformanceSeat().getPerformanceSeatId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));

            if (!"HELD".equals(seat.getStatus())
                    || seat.getHoldExpiresAt() == null
                    || !seat.getHoldExpiresAt().isAfter(now)) {
                throw new IllegalStateException(
                        "결제 대기가 만료되었거나 유효하지 않은 좌석입니다.");
            }

            if (detail.getStatus() != OrdersDetailStatus.PENDING) {
                throw new IllegalStateException("주문 상세 상태가 올바르지 않습니다.");
            }
        }

        // 좌석 확정
        for (OrdersDetail detail : details) {
            PerformanceSeat seat = performanceSeatRepository
                    .findByIdForUpdate(
                            detail.getPerformanceSeat().getPerformanceSeatId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));

            seat.reserve();
            detail.confirm();
        }

        payment.completePayment(paymentKey, now);
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
    }
    
    
    
} // service