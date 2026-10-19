package org.doit.sistticket.mypage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.repository.MyPageRepository;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {
	
	private final MyPageRepository myPageRepository;

	@Override
	public List<MyPageDTO> getMyTicketList(Long memberId) {
		
		List<OrdersDetail> result = myPageRepository.findByOrders_Member_MemberIdOrderByOrderDetailIdDesc(memberId);
		
		return result.stream().map(detail -> {
			String concertTitle = detail.getOrders().getPerformance().getConcert().getConcertName(); 

			return MyPageDTO.builder()
					.orderDetailId(detail.getOrderDetailId())
					.orderNo(detail.getOrders().getOrderNo())
					.performanceTitle(concertTitle) 
					.price(detail.getPrice())
					.status(detail.getStatus())
					.createdAt(detail.getCreatedAt())
					.build();
		}).collect(Collectors.toList());
	}

	@Override
	public Long getConcertIdByOrderDetailId(Long orderDetailId) {
		OrdersDetail orderDetail = myPageRepository.findById(orderDetailId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상세 번호입니다: " + orderDetailId));
		
		return orderDetail.getOrders().getPerformance().getConcert().getConcertId();
	}

}