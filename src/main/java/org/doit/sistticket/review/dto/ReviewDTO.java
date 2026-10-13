package org.doit.SistTicket.review.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReviewDTO {
	
	private Long reviewId;
	private Long memberId;
	private Long concertId;
	private Integer rating;
	private String text;
	private String imgUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
