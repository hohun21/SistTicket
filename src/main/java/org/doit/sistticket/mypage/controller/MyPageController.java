package org.doit.sistticket.mypage.controller;

import java.util.List;

import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.service.MyPageService;
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
	
	@GetMapping("")
	public String myPage(Model model) {
		
		Long loginMemberId = 4L;
		
		List<MyPageDTO> ticketList = myPageService.getMyTicketList(loginMemberId);
		
		model.addAttribute("ticketList", ticketList);
		
		return "mypage/mypage";
	}
	
	@PostMapping("/review")
	public String saveReview(
	        @RequestParam("orderDetailId") Long orderDetailId,
	        @RequestParam("rating") int rating,
	        @RequestParam("text") String text,
	        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
		
		Long loginMemberId = 4L; // 현재 로그인한 회원 ID (추후 세션 등으로 변경)
		
		myPageService.saveReview(loginMemberId, orderDetailId, rating, text, imageFile);
		
		return "redirect:/mypage"; // 등록 후 마이페이지로 리다이렉트
	}
	
}
