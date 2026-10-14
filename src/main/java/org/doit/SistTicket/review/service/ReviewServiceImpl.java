package org.doit.SistTicket.review.service;

import java.util.List;

import org.doit.SistTicket.review.dto.ReviewDTO;
import org.doit.SistTicket.review.entity.Reviews;
import org.doit.SistTicket.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {
	
	private final ReviewRepository reviewRepository;
	
	@Override
	public Long register(ReviewDTO reviewDTO) {
		
		log.info("등록된 게시글 DTO 확인: " + reviewDTO);
		
		Reviews entity = dtoToEntity(reviewDTO);
		
		reviewRepository.save(entity);
		
		return entity.getReviewId();
		
	}

	@Override
	public List<ReviewDTO> getReviewsByConcertId(Long concertId) {
		
		return null;
	}

	@Override
	public List<ReviewDTO> getReviewsByMemberId(Long memberId) {
		
		return null;
	}

}
