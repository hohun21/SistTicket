package org.doit.sistticket.review.dto;

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
	private String memberName;
	private Long concertId;
	private String concertTitle;
	private Integer rating;
	private String text;
	private String imgUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
