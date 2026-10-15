package org.doit.sistticket.review.controller;

import java.util.List;

import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {
	
	private final ReviewService reviewService;
	
	@PostMapping("")
	public ResponseEntity<Long> register(@RequestBody ReviewDTO reviewDTO) {
		
		Long reviewId = reviewService.register(reviewDTO);
		
		return new ResponseEntity<>(reviewId, HttpStatus.OK);
	}
	
	@GetMapping("/concert/{concertId}")
	public ResponseEntity<List<ReviewDTO>> getListByConcertId(@PathVariable("concertId") Long concertId) {
		
		List<ReviewDTO> reviewList = reviewService.getReviewsByConcertId(concertId);
		
		return new ResponseEntity<>(reviewList, HttpStatus.OK);
	}
	
	@GetMapping("/member/{memberId}")
	public ResponseEntity<List<ReviewDTO>> getListByMemberId(@PathVariable("memberId") Long memberId) {
		
		List<ReviewDTO> reviewList = reviewService.getReviewsByMemberId(memberId);
		
		return new ResponseEntity<>(reviewList, HttpStatus.OK);
	}
	
}
