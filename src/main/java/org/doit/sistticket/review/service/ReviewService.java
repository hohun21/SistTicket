package org.doit.sistticket.review.service;

import java.util.List;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.entity.Reviews;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
	
	Long register (ReviewDTO reviewDTO);
	
	Long registerWithImage(ReviewDTO reviewDTO, MultipartFile imageFile);
	
	List<ReviewDTO> getReviewsByConcertId(Long concertId);
	
	List<ReviewDTO> getReviewsByMemberId(Long memberId);
	
	void modify(ReviewDTO reviewDTO);
	
	void remove(Long reviewId);
	
	default Reviews dtoToEntity(ReviewDTO reviewDTO) {
        Members member = new Members();
        member.setMemberId(reviewDTO.getMemberId());
        
        Concert concert = new Concert();
        concert.setConcertId(reviewDTO.getConcertId());

        Reviews entity = Reviews.builder()
                .reviewId(reviewDTO.getReviewId())
                .member(member)
                .concert(concert)
                .rating(reviewDTO.getRating())
                .text(reviewDTO.getText())
                .imgUrl(reviewDTO.getImgUrl())
                .build();
        return entity;
    }
	
	default ReviewDTO entityToDto (Reviews entity) {
		ReviewDTO reviewDTO = ReviewDTO.builder()
				.reviewId(entity.getReviewId())
				.memberId(entity.getMember().getMemberId())
				.concertId(entity.getConcert().getConcertId())
				.rating(entity.getRating())
				.text(entity.getText())
				.imgUrl(entity.getImgUrl())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
		return reviewDTO;
	}
	
}
