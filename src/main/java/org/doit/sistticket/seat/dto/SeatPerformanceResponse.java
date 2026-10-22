package org.doit.sistticket.seat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatPerformanceResponse {

    private long performanceId;
    private String concertName;
    private LocalDateTime performanceDate;
    private String venueName;
    
}