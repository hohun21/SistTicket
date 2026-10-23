
package org.doit.sistticket.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.orders.entity.Orders;

public record OrderCreateResponseDTO(
        Long ordersId,
        String orderNo,
        Long memberId,
        Long performanceId,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        Long paymentId
) {
    public static OrderCreateResponseDTO from(
            Orders order,
            Long paymentId
    ) {
        return new OrderCreateResponseDTO(
                order.getOrdersId(),
                order.getOrderNo(),
                order.getMember().getMemberId(),
                order.getPerformance().getPerformanceId(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                paymentId
        );
    }
}
