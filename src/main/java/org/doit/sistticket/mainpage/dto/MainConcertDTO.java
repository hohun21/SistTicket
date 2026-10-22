package org.doit.sistticket.mainpage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainConcertDTO {
    private Long concertId;
    private String title;
    private String posterUrl;
    private String category;
    private String venueName;
    private String startDate;
    private String endDate;
}