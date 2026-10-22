package org.doit.sistticket.category.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.category.entity.PerformanceStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceRequestDTO {
    
    private LocalDateTime performanceDate; // 공연 일시
    private LocalDateTime bookingStart;    // 예매 시작일
    private LocalDateTime bookingEnd;      // 예매 마감일
    private PerformanceStatus status;      // 회차 상태 (UPCOMING, ONGOING, FINISHED)[cite: 2]
    
    // 이 회차에 참여하는 출연진(배역-배우 매핑) 목록
    private List<CastRequestDTO> casts;

    @Getter @Setter
    @ToString
    public static class CastRequestDTO {
        private Long roleId;  // 배역 ID (Role)[cite: 4]
        private Long actorId; // 배우 ID (Actor)[cite: 5]
    }
}