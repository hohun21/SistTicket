package org.doit.sistticket.category.dto;

import java.time.LocalDateTime;
import org.doit.sistticket.category.entity.ConcertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertListResponseDTO {
    private Long concertId;
    private String concertName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ConcertStatus status;
    private String genreName;
    private String venueName;
    private String imgUrl;
    private Long runTimeMinutes; 
}