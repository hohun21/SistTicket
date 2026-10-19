package org.doit.sistticket.mypage.controller;

import java.util.List;

import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.service.MyPageService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageViewController {

	private final MyPageService myPageService;

	// 1. 마이페이지 메인 화면
	@GetMapping("")
	public String myPage(@AuthenticationPrincipal CustomerUserDetails userDetails, Model model) {
		Long loginMemberId = userDetails.getMember().getMemberId();
		List<MyPageDTO> ticketList = myPageService.getMyTicketList(loginMemberId);
		model.addAttribute("ticketList", ticketList);
		return "mypage/mypage";
	}

	// 2. 비밀번호 재확인 화면
	@GetMapping("/verify")
	public String verifyForm(@RequestParam("target") String target, Model model) {
		model.addAttribute("target", target);
		return "mypage/verify";
	}

	// 3. 회원 정보 수정 화면
	@GetMapping("/edit")
	public String editForm(@AuthenticationPrincipal CustomerUserDetails userDetails
			, HttpSession httpSession
			, Model model) {

		if(httpSession.getAttribute("passwordVerified") == null) {
			return "redirect:/mypage/verify?target=edit";
		}
		model.addAttribute("member", userDetails.getMember());
		return "mypage/edit";
	}

	// 4. 회원 탈퇴 화면
	@GetMapping("/withdraw")
	public String withdrawForm(HttpSession httpSession) {

		if (httpSession.getAttribute("passwordVerified") == null) {
			return "redirect:/mypage/verify?target=withdraw";
		}
		return "mypage/withdraw";
	}
}