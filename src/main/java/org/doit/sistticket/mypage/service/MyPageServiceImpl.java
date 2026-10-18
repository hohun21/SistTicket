package org.doit.sistticket.mypage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.repository.MyPageRepository;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.review.entity.Reviews;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {
	
	private final MyPageRepository myPageRepository;
	
	private final ReviewRepository reviewRepository;
	
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
	@Transactional
	public void saveReview(Long memberId, Long orderDetailId, int rating, String text, MultipartFile imageFile) {
		
		OrdersDetail orderDetail = myPageRepository.findById(orderDetailId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상세 번호입니다: " + orderDetailId));
		
		Concert concert = orderDetail.getOrders().getPerformance().getConcert();
		Members member = orderDetail.getOrders().getMember();
		
		String imgUrl = null;
		if (imageFile != null && !imageFile.isEmpty()) {
			imgUrl = "/upload/" + imageFile.getOriginalFilename(); 
		}
		
		Reviews review = Reviews.builder()
				.member(member)
				.concert(concert)
				.rating(rating)
				.text(text)
				.imgUrl(imgUrl)
				.build();
				
		reviewRepository.save(review);
	}

}
