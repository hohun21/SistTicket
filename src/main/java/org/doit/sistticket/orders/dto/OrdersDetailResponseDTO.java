package org.doit.sistticket.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;

import lombok.Getter;

@Getter
public class OrdersDetailResponseDTO {

    private final Long orderDetailId;
    private final Long performanceSeatId;
    private final String rowLabel;
    private final Integer seatNumber;
    private final String gradeName;
    private final BigDecimal price;
    private final OrdersDetailStatus status;
    private final LocalDateTime createdAt;

    public OrdersDetailResponseDTO(OrdersDetail detail) {
        this.orderDetailId = detail.getOrderDetailId();

        this.performanceSeatId =
                detail.getPerformanceSeat().getPerformanceSeatId();

        this.rowLabel =
                detail.getPerformanceSeat().getSeat().getRowLabel();

        this.seatNumber =
                detail.getPerformanceSeat().getSeat().getSeatNumber();

        this.gradeName =
                detail.getPerformanceSeat().getSeat()
                        .getSeatGrade().getGradeName();

        this.price = detail.getPrice();
        this.status = detail.getStatus();
        this.createdAt = detail.getCreatedAt();
    }
}