package org.doit.sistticket.seat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {

    private Long performanceSeatId;
    private Long seatId;
    private String rowLabel;
    private Integer seatNumber;
    private String gradeName;
    private Integer price;
    private String status;
}