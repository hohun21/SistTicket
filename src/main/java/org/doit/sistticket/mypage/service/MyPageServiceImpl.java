package org.doit.sistticket.mypage.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.mypage.dto.MyPageDTO;
import org.doit.sistticket.mypage.repository.MyPageRepository;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.doit.sistticket.payments.entity.Payments;
import org.doit.sistticket.payments.enums.PaymentStatus;
import org.doit.sistticket.payments.repository.PaymentsRepository;
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

	private final PaymentsRepository paymentsRepository;

	@Override
	public List<MyPageDTO> getMyTicketList(Long memberId) {

		List<OrdersDetail> result = myPageRepository.findByOrders_Member_MemberIdOrderByOrderDetailIdDesc(memberId);
		LocalDateTime now = LocalDateTime.now();

		return result.stream().map(detail -> {
			Long concertId = detail.getOrders().getPerformance().getConcert().getConcertId();
			String concertTitle = detail.getOrders().getPerformance().getConcert().getConcertName();
			LocalDateTime concertDate = detail.getOrders().getPerformance().getPerformanceDate();

			String venueName = detail.getOrders().getPerformance().getConcert().getVenue().getVenueName();
			
			String seatInfo = detail.getPerformanceSeat().getSeat().getRowLabel() + "-" 
								+ detail.getPerformanceSeat().getSeat().getSeatNumber();
			
			boolean hasReviewed = reviewRepository.existsByMember_MemberIdAndConcert_ConcertId(memberId, concertId);

			long hoursUntilConcert = ChronoUnit.HOURS.between(now, concertDate);
			String buttonStatus = "";

			if (detail.getStatus() == OrdersDetailStatus.CANCELLED) {
				buttonStatus = "CANCELED";
			} else if (detail.getStatus() == OrdersDetailStatus.CANCELEREQUEST) {
				buttonStatus = "CANCEL_REQUESTED"; 
			} else if (detail.getStatus() == OrdersDetailStatus.CONFIRMED) {
				if (now.isAfter(concertDate)) {
					buttonStatus = hasReviewed ? "REVIEWED" : "REVIEWABLE";
				} else if (hoursUntilConcert < 2) {
					buttonStatus = "NON_CANCELABLE";
				} else {
					buttonStatus = "CANCELABLE";
				}
			} else {
				buttonStatus = detail.getStatus().name();
			}

			return MyPageDTO.builder()
					.orderDetailId(detail.getOrderDetailId())
					.orderNo(detail.getOrders().getOrderNo())
					.concertId(concertId)
					.performanceTitle(concertTitle)
					.price(detail.getPrice())
					.status(detail.getStatus().name())
					.createdAt(detail.getCreatedAt())
					.startDate(concertDate)
					.venueName(venueName)
					.performanceSeq(detail.getOrders().getPerformance().getPerformanceSeq())
					.seatInfo(seatInfo)
					.isReviewed(hasReviewed)
					.buttonStatus(buttonStatus)
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

	@Transactional
	@Override
	public void cancelMyTicket(Long orderDetailId, Long memberId) {
		OrdersDetail orderDetail = myPageRepository.findById(orderDetailId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상세 번호입니다."));

		if (!orderDetail.getOrders().getMember().getMemberId().equals(memberId)) {
			throw new IllegalArgumentException("해당 예매 내역을 취소할 권한이 없습니다.");
		}

		if (orderDetail.getStatus() == OrdersDetailStatus.CANCELLED) {
			throw new IllegalStateException("이미 취소된 예매 내역입니다.");
		}
		
		if (orderDetail.getStatus() == OrdersDetailStatus.CANCELEREQUEST) {
			throw new IllegalStateException("이미 취소 요청 중인 예매 내역입니다.");
		}

		orderDetail.cancelRequest();
	}
}