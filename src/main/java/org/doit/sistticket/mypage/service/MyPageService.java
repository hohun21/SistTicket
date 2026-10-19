package org.doit.sistticket.mypage.service;

import java.util.List;

import org.doit.sistticket.mypage.dto.MyPageDTO;

public interface MyPageService {
	List<MyPageDTO> getMyTicketList (Long memberId);
	
	Long getConcertIdByOrderDetailId(Long orderDetailId);
}
