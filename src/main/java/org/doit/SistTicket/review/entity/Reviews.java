package org.doit.SistTicket.review.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reviews {
	
	private Long reviewId;
	
	private Long memberId;
	
	private Long concertId;
	
	private Integer rating;
	
	private String text;
	
	private String imgUrl;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
}
