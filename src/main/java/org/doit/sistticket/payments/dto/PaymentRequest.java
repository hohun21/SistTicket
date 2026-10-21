package org.doit.sistticket.payments.dto;

public record PaymentRequest(
		Long paymentId,
		String paymentKey) {
	
}
