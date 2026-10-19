package org.doit.sistticket.category.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.category.entity.ConcertStatus;
import org.springframework.format.annotation.DateTimeFormat;

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
public class ConcertRequestDTO {

    private String concertName;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private Long venueId;
    private Long genreId;

    private ConcertStatus status;
    private Long runTimeMinutes; 

    private String thumbnailUrl;          // 대표 썸네일 (1개)
    private List<String> detailImgUrls;   // 상세 이미지들 (여러 개 가능)
}