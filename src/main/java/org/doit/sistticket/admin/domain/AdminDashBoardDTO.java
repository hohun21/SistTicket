package org.doit.sistticket.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashBoardDTO {

    private Long totalReservations;
    private Long totalMembers;
    private Long totalConcerts;
    private Long totalRevenue;

}
