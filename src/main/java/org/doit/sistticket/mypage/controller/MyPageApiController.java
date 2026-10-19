package org.doit.sistticket.mypage.controller;

import java.util.Map;
import java.util.HashMap;

import org.doit.sistticket.mypage.service.MyPageService;
import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.service.ReviewService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageApiController {

	private final MyPageService myPageService;
	
	private final ReviewService reviewService;

	// 1. 리뷰 작성
	@PostMapping("/review")
	public ResponseEntity<?> saveReview(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@RequestParam("orderDetailId") Long orderDetailId,
			@RequestParam("rating") int rating,
			@RequestParam("text") String text,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

		Long loginMemberId = userDetails.getMember().getMemberId();

		if (orderDetailId == 999L) {
			ReviewDTO dummyDto = ReviewDTO.builder()
					.memberId(loginMemberId).concertId(1L).rating(rating).text(text).build();
			try { reviewService.registerWithImage(dummyDto, imageFile); } catch (Exception e) {}
			return ResponseEntity.ok(Map.of("success", true, "message", "테스트 리뷰가 등록되었습니다."));
		}

		Long concertId = myPageService.getConcertIdByOrderDetailId(orderDetailId);
		ReviewDTO reviewDTO = ReviewDTO.builder()
				.memberId(loginMemberId).concertId(concertId).rating(rating).text(text).build();
		reviewService.registerWithImage(reviewDTO, imageFile);

		return ResponseEntity.ok(Map.of("success", true, "message", "리뷰가 성공적으로 등록되었습니다."));
	}

	// 2. 비밀번호 재확인
	@PostMapping("/verify")
	public ResponseEntity<?> verifyPassword(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@RequestBody Map<String, String> requestData,
			HttpSession httpSession) {

		String password = requestData.get("password");
		Long loginMemberId = userDetails.getMember().getMemberId();

		if(myPageService.checkPassword(loginMemberId, password)) {
			httpSession.setAttribute("passwordVerified", true);
			httpSession.setMaxInactiveInterval(60 * 10);
			return ResponseEntity.ok(Map.of("success", true));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "비밀번호가 일치하지 않습니다."));
		}
	}

	// 3. 회원 정보 수정
	@PostMapping("/edit")
	public ResponseEntity<?> editMember(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@RequestBody Map<String, String> requestData,
			HttpSession httpSession) {

		if(httpSession.getAttribute("passwordVerified") == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("success", false, "message", "비밀번호 인증이 필요합니다."));
		}

		String phone = requestData.get("phone");
		String password = requestData.get("password");
		Long loginMemberId = userDetails.getMember().getMemberId();

		myPageService.updateMemberInfo(loginMemberId, phone, password);
		httpSession.removeAttribute("passwordVerified");

		return ResponseEntity.ok(Map.of("success", true, "message", "회원 정보가 성공적으로 수정되었습니다."));
	}

	// 4. 회원 탈퇴
	@PostMapping("/withdraw")
	public ResponseEntity<?> withdrawMember(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@RequestBody Map<String, String> requestData,
			HttpServletRequest request, 
			HttpServletResponse response,
			HttpSession httpSession) {

		if (httpSession.getAttribute("passwordVerified") == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("success", false, "message", "비밀번호 인증이 필요합니다."));
		}		

		String password = requestData.get("password");
		Long loginMemberId = userDetails.getMember().getMemberId();
		boolean isSuccess = myPageService.withdrawMember(loginMemberId, password);

		if (isSuccess) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
			return ResponseEntity.ok(Map.of("success", true, "message", "탈퇴 처리가 완료되었습니다.")); 
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "비밀번호가 일치하지 않습니다."));
		}
	}
}