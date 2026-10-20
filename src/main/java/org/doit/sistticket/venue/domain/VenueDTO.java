package org.doit.sistticket.venue.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class VenueDTO {
    private Long venueId;
    private String venueName;
    private String address;
    private String detailAddress;
    private long capacity;
}