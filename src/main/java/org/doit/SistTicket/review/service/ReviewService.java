package org.doit.SistTicket.review.service;

import java.util.List;

import org.doit.SistTicket.review.dto.ReviewDTO;
import org.doit.SistTicket.review.entity.Reviews;

public interface ReviewService {
	
	Long register (ReviewDTO reviewDTO);
	
	List<ReviewDTO> getReviewsByConcertId(Long concertId);
	
	List<ReviewDTO> getReviewsByMemberId(Long memberId);
	
	default Reviews dtoToEntity (ReviewDTO reviewDTO) {
		Reviews entity = Reviews.builder()
				.reviewId(reviewDTO.getReviewId())
				.memberId(reviewDTO.getMemberId())
				.concertId(reviewDTO.getConcertId())
				.rating(reviewDTO.getRating())
				.text(reviewDTO.getText())
				.imgUrl(reviewDTO.getImgUrl())
				.build();
		return entity;
	}
	
	default ReviewDTO entityToDto (Reviews entity) {
		ReviewDTO reviewDTO = ReviewDTO.builder()
				.reviewId(entity.getReviewId())
				.memberId(entity.getMemberId())
				.concertId(entity.getConcertId())
				.rating(entity.getRating())
				.text(entity.getText())
				.imgUrl(entity.getImgUrl())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
		return reviewDTO;
	}
	
}
