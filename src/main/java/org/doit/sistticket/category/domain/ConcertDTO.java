package org.doit.sistticket.category.domain;

import lombok.*;
import org.doit.sistticket.venue.entity.Venue;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConcertDTO {

    private Long concertId;
    private String concertName;

    private Long venueId;
    private String venueName;

    private Integer runTimeMinutes;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status;
}
