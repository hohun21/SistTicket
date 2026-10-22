package org.doit.sistticket.category.dto;

import java.time.LocalDateTime;
import java.util.List;
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
public class ConcertDetailResponseDTO {
    
    // 1. 콘서트 기본 정보
    private Long concertId;
    private String concertName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ConcertStatus status;
    private String genreName;
    private String venueName;
    private String venueAddress;
    private String imgUrl;
    private Long runTimeMinutes; //
   
    // 2. 해당 콘서트의 모든 회차 일정 리스트 (1회차, 2회차, 3회차...)
    private List<PerformanceInfoItem> performanceList;

    // --- [내부 클래스] 회차 정보 Item ---
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceInfoItem {
        private Long performanceId;          // 회차 ID
        private LocalDateTime performanceDate; // 공연 시작 일시 (년/월/일 시:분)
        private LocalDateTime bookingStart;    // 예매 시작일
        private LocalDateTime bookingEnd;      // 예매 마감일
        private String status;               // 회차 상태 (ONGOING, UPCOMING 등)
       
        // 3. 해당 회차에 매칭된 캐스팅 정보 리스트 (장발장-박은태, 자베르-옥주현 등)
        private List<CastInfoItem> casts;
    }

    // --- [내부 클래스] 캐스팅 정보 Item ---
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CastInfoItem {
        private String roleName;  // 역할명 (예: 장발장, 주연, 메인 아티스트)
        private String actorName; // 배우명 (예: 박은태, 아이유)
        private String description;
    }
}