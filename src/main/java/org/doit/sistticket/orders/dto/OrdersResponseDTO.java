package org.doit.sistticket.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.orders.entity.Orders;

import lombok.Getter;

@Getter
public class OrdersResponseDTO {
	
	private final Long ordersId;
	private final String orderNo;
	private final Long memberId;
	private final Long performanceId;
	private final BigDecimal totalAmount;
	private final LocalDateTime createdAt;
	
	public OrdersResponseDTO(Orders orders) {
		this.ordersId = orders.getOrdersId();
		this.orderNo = orders.getOrderNo();
		this.memberId = orders.getMember().getMemberId();
		this.performanceId = orders.getPerformance().getPerformanceId();
		this.totalAmount = orders.getTotalAmount();
		this.createdAt = orders.getCreatedAt();
	}
}
