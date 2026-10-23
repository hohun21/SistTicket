package org.doit.sistticket.admin.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminTopConcertDTO {

    private Long concertId;
    private String concertName;
    private String concertImgPath;
    private String genreName;
}
