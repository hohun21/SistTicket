package org.doit.sistticket.mypage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.repository.MyPageRepository;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {
	
	private final MyPageRepository myPageRepository;
	
	private final MemberRepository memberRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final ReviewRepository reviewRepository;

	@Override
	public List<MyPageDTO> getMyTicketList(Long memberId) {
		
		List<OrdersDetail> result = myPageRepository.findByOrders_Member_MemberIdOrderByOrderDetailIdDesc(memberId);

		return result.stream().map(detail -> {
		    Long concertId = detail.getOrders().getPerformance().getConcert().getConcertId();
		    String concertTitle = detail.getOrders().getPerformance().getConcert().getConcertName();

		    boolean hasReviewed = reviewRepository.existsByMember_MemberIdAndConcert_ConcertId(memberId, concertId);

		    return MyPageDTO.builder()
		            .orderDetailId(detail.getOrderDetailId())
		            .orderNo(detail.getOrders().getOrderNo())
		            .performanceTitle(concertTitle)
		            .price(detail.getPrice())
		            .status(detail.getStatus())
		            .createdAt(detail.getCreatedAt())
		            .isReviewed(hasReviewed)
		            .build();
		}).collect(Collectors.toList());
	}

	@Override
	public Long getConcertIdByOrderDetailId(Long orderDetailId) {
		OrdersDetail orderDetail = myPageRepository.findById(orderDetailId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상세 번호입니다: " + orderDetailId));
		
		return orderDetail.getOrders().getPerformance().getConcert().getConcertId();
	}
	
	@Transactional
	@Override
	public void updateMemberInfo(Long memberId, String newPhone, String newPassword) {
		
		Members member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
		
		member.setPhone(newPhone);
		
		if (newPassword != null && !newPassword.trim().isEmpty()) {
            member.setPassword(passwordEncoder.encode(newPassword));
        }
	}
	
	@Transactional
	@Override
	public boolean withdrawMember(Long memberId, String password) {
		
		Members member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
		
		if (passwordEncoder.matches(password, member.getPassword())) {
            member.setStatus(0); 
            return true; 
        }
        return false;
	}

	@Override
	public boolean checkPassword(Long memberId, String password) {
		
		Members member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
		
		return passwordEncoder.matches(password, member.getPassword());
	}

	@Override
	public Page<ReviewDTO> getMyReviewList(Long memberId, Pageable pageable) {
		
		return reviewRepository.findMyReviewsWithConcertTitle(memberId, pageable);
	}

	
	
	
}