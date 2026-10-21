package org.doit.sistticket.mypage.controller;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.member.service.MemberService;
import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.service.MyPageService;
import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.doit.sistticket.review.service.ReviewService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	private final ReviewRepository reviewRepository;
	
	private final MemberRepository memberRepository;
	
	private final MemberService memberService;

	// 1. 리뷰 작성
	@PostMapping("/review")
	public ResponseEntity<?> saveReview(
			@AuthenticationPrincipal CustomerUserDetails userDetails,
			@RequestParam("orderDetailId") Long orderDetailId,
			@RequestParam("rating") int rating,
			@RequestParam("text") String text,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

		Long loginMemberId = userDetails.getMember().getMemberId();
		Long concertId;

		if (orderDetailId == 999L) {
			concertId = 2L;
		} else {
			concertId = myPageService.getConcertIdByOrderDetailId(orderDetailId);
		}

		if(reviewRepository.existsByMember_MemberIdAndConcert_ConcertId(loginMemberId, concertId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("success", false, "message", "이미 이 공연에 대한 리뷰를 작성하셨습니다."));
		}
		
		ReviewDTO reviewDTO = ReviewDTO.builder()
				.memberId(loginMemberId)
				.concertId(concertId)
				.rating(rating)
				.text(text)
				.build();
		
		try { 
			reviewService.registerWithImage(reviewDTO, imageFile); 
		} catch (Exception e) {
		}

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
		
		String email = userDetails.getMember().getEmail();

		Map<String, Object> result = memberService.editMemberInfo(email, phone, password);

		if (result.get("success").equals(false)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		}

		httpSession.removeAttribute("passwordVerified");

		return ResponseEntity.ok(result);
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

	@GetMapping("/user-info")
	public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomerUserDetails userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}
		
		Members currentMembers = memberRepository.findByEmail(userDetails.getMember().getEmail())
				.orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
		

		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("success", true);
		userInfo.put("name", currentMembers.getName());
		userInfo.put("email", currentMembers.getEmail());
		userInfo.put("phone", currentMembers.getPhone());
		
		return ResponseEntity.ok(userInfo);
	}
	
	@GetMapping("/myreviews")
    public ResponseEntity<?> getMyReviews(
    		  @AuthenticationPrincipal CustomerUserDetails userDetails
    		, @RequestParam(name = "page", defaultValue = "0") int page
    		, @RequestParam(name = "size", defaultValue = "3") int size) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        Long loginMemberId = userDetails.getMember().getMemberId();
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ReviewDTO> reviewPage = myPageService.getMyReviewList(loginMemberId, pageable);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "reviews", reviewPage.getContent(),
                "totalPages", reviewPage.getTotalPages(),
                "currentPage", reviewPage.getNumber()
        ));
    }
	
	@GetMapping("/tickets")
	public ResponseEntity<?> getMyTickets(@AuthenticationPrincipal CustomerUserDetails userDetails) {
	    if (userDetails == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("success", false, "message", "로그인이 필요합니다."));
	    }

	    Long loginMemberId = userDetails.getMember().getMemberId();
	    List<MyPageDTO> ticketList = myPageService.getMyTicketList(loginMemberId);
	    
	    return ResponseEntity.ok(Map.of(
	        "success", true,
	        "tickets", ticketList
	    ));
	}
	
}