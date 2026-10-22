package org.doit.sistticket.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationDTO {

    private Long ordersId;
    private String orderNo;
    private Long memberId;
    private String memberName;
    private String concertsName;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private String paymentStatus;
}
