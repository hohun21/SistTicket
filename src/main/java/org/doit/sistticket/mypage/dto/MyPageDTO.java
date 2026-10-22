package org.doit.sistticket.mypage.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyPageDTO {
    private Long orderDetailId;
    private String orderNo;
    private Long concertId;
    private String performanceTitle;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    
    private Integer performanceSeq;
    private String venueName;
    private String seatInfo;

    private boolean isReviewed;
    private String buttonStatus; 
}