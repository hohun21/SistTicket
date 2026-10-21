package org.doit.sistticket.payments.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.payments.entity.Payments;
import org.doit.sistticket.payments.enums.PaymentStatus;

public record PaymentResponse(
		Long paymentId,
		Long ordersId,
		BigDecimal amount,
		String paymentMethod,
		PaymentStatus status,
		LocalDateTime paidAt,
		LocalDateTime cancelledAt) {
	public static PaymentResponse from(Payments payment) {
		return new PaymentResponse(
				payment.getPaymentId(),
				payment.getOrders().getOrdersId(),
				payment.getAmount(),
				payment.getPaymentMethod(),
				payment.getStatus(),
				payment.getPaidAt(),
				payment.getCancelledAt()
				);
	}
}
