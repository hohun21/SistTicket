package org.doit.sistticket.seat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeatHoldCancelRequest {

    private String holdToken;
    
}