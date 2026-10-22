package org.doit.sistticket.admin.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;

import java.math.BigDecimal;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class AdminReservationDetailDTO {

    private String seatGrade;
    private Integer performancesSeq;
    private String rowLabel;
    private Integer seatNumber;
    private OrdersDetailStatus status;
    private BigDecimal price;
}
