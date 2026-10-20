package org.doit.sistticket.category.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.category.entity.ConcertStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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

 // 💡 비동기 업로드 완료 후 넘어오는 이미지 정보들 (썸네일 + 상세 이미지)
    private List<ImageUploadDTO> imageDTOs;
}