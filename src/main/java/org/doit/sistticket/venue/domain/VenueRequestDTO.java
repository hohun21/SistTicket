package org.doit.sistticket.venue.domain; // 패키지 경로는 프로젝트에 맞게 조절하세요

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VenueRequestDTO {
    private String venueName;     // 장소 이름
    private String address;       // 주소
    private String detailAddress; // 상세 주소
    private Integer capacity;     // 수용 인원 (0이 아닌 적당한 숫자)
}