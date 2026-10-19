package org.doit.sistticket.seat.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatHoldResponse {

    private String holdToken;
    private List<Long> performanceSeatIds;
    private LocalDateTime holdExpiresAt;
}