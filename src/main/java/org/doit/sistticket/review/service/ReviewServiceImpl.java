package org.doit.sistticket.review.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.entity.Reviews;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		
		List<Reviews> result = reviewRepository.findByConcert_ConcertId(concertId);
		
		return result.stream()
						.map(entity -> entityToDto(entity))
						.collect(Collectors.toList());
	}

	@Override
	public List<ReviewDTO> getReviewsByMemberId(Long memberId) {
		
		List<Reviews> result = reviewRepository.findByMember_MemberId(memberId);
		
		return result.stream()
						.map(entity -> entityToDto(entity))
						.collect(Collectors.toList());
	}
	
	@Transactional
	@Override
	public void modify(ReviewDTO reviewDTO) {
		
		Optional<Reviews> result = reviewRepository.findById(reviewDTO.getReviewId());
		
		if(result.isPresent()) {
			
			Reviews review = result.get();
			
			review.setText(reviewDTO.getText());
			review.setRating(reviewDTO.getRating());
			review.setImgUrl(reviewDTO.getImgUrl());
			
			reviewRepository.save(review);
		}
		
	}

	@Override
	public void remove(Long reviewId) {
		
		reviewRepository.deleteById(reviewId);
		
	}

}
