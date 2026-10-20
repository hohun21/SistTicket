package org.doit.sistticket.review.controller;

import java.util.List;
import java.util.Map;

import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.service.ReviewService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	@PutMapping("/{reviewId}")
	public ResponseEntity<?> modifyReview(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@PathVariable("reviewId") Long reviewId,
			@RequestBody ReviewDTO reviewDTO) {
		
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}

		// 본인 확인 등 필요한 검증 후 수정 실행
		reviewDTO.setReviewId(reviewId);
		reviewService.modify(reviewDTO);

		return ResponseEntity.ok(Map.of("success", true, "message", "리뷰가 성공적으로 수정되었습니다."));
	}

	// 5. 리뷰 삭제
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<?> removeReview(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@PathVariable("reviewId") Long reviewId) {
		
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}

		reviewService.remove(reviewId);

		return ResponseEntity.ok(Map.of("success", true, "message", "리뷰가 성공적으로 삭제되었습니다."));
	}
	
}
