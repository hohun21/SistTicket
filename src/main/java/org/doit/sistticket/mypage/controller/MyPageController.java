package org.doit.sistticket.mypage.controller;

import java.util.List;

import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.service.MyPageService;
import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.service.ReviewService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
	
	private final MyPageService myPageService;
	private final ReviewService reviewService;
	
	@GetMapping("")
	public String myPage(@AuthenticationPrincipal CustomerUserDetails userDetails, Model model) {
		Long loginMemberId = userDetails.getMember().getMemberId();
		List<MyPageDTO> ticketList = myPageService.getMyTicketList(loginMemberId);
		model.addAttribute("ticketList", ticketList);
		return "mypage/mypage";
	}
	
	@PostMapping("/review")
	public String saveReview(
	        @AuthenticationPrincipal CustomerUserDetails userDetails,
	        @RequestParam("orderDetailId") Long orderDetailId,
	        @RequestParam("rating") int rating,
	        @RequestParam("text") String text,
	        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
		
		Long loginMemberId = userDetails.getMember().getMemberId();
		
		if (orderDetailId == 999L) {
			System.out.println("====== [테스트 모드 작동] ======");
			
			ReviewDTO dummyDto = ReviewDTO.builder()
					.memberId(loginMemberId)
					.concertId(1L) 
					.rating(rating)
					.text(text)
					.build();
			
			try {
				reviewService.registerWithImage(dummyDto, imageFile);
			} catch (Exception e) {
				System.out.println("DB 저장 에러 (정상적인 외래키 에러): " + e.getMessage());
			}
			
			System.out.println("최종 이미지 URL: " + dummyDto.getImgUrl());
			System.out.println("==============================");
			return "redirect:/mypage"; 
		}
		
		
		Long concertId = myPageService.getConcertIdByOrderDetailId(orderDetailId);
		
		ReviewDTO reviewDTO = ReviewDTO.builder()
				.memberId(loginMemberId)
				.concertId(concertId)
				.rating(rating)
				.text(text)
				.build();
		
		reviewService.registerWithImage(reviewDTO, imageFile);
		
		return "redirect:/mypage";
	}
}