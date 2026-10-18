package org.doit.sistticket.mypage.service;

import java.util.List;

import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
	List<MyPageDTO> getMyTicketList (Long memberId);
	
	void saveReview(Long memberId, Long orderDetailId, int rating, String text, MultipartFile imageFile);
}
