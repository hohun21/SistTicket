package org.doit.sistticket.orders.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.orders.dto.OrdersDetailResponseDTO;
import org.doit.sistticket.orders.dto.OrdersResponseDTO;
import org.doit.sistticket.orders.entity.Orders;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.doit.sistticket.orders.repository.OrdersDetailRepository;
import org.doit.sistticket.orders.repository.OrdersRepository;
import org.doit.sistticket.payments.entity.Payments;
import org.doit.sistticket.payments.enums.PaymentStatus;
import org.doit.sistticket.payments.repository.PaymentsRepository;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.doit.sistticket.seat.service.PerformanceSeatStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersService {

	private final OrdersRepository ordersRepository;
	private final OrdersDetailRepository ordersDetailRepository;
	private final PerformanceSeatRepository performanceSeatRepository;
	private final MemberRepository memberRepository;
	private final PerformancesRepository performancesRepository;
	private final PaymentsRepository paymentsRepository;
	private final PerformanceSeatStatusService performanceSeatStatusService;

	private String getHoldToken(List<OrdersDetail> details) {
		List<String> tokens = details.stream().map(detail -> detail.getPerformanceSeat().getHoldToken())
											  .distinct().toList();
		if (tokens.size() != 1
		        || tokens.get(0) == null
		        || tokens.get(0).isBlank()) {
		    throw new IllegalStateException("주문 좌석의 선점 토큰이 없거나 일치하지 않습니다.");
		}
		return tokens.get(0);
	}
	
	@Transactional
	public Orders createOrder(
			Long memberId,
			Long performanceId,
			List<Long> performanceSeatIds,
			String paymentMethod,
			String holdToken) {

		// 1. 요청값 검증
		if (performanceSeatIds == null || performanceSeatIds.isEmpty()) {
			throw new IllegalArgumentException("선택한 좌석이 없습니다.");
		}

		if (performanceSeatIds.stream().anyMatch(id -> id == null)) {
			throw new IllegalArgumentException("좌석 ID가 올바르지 않습니다.");
		}

		if (new HashSet<>(performanceSeatIds).size()
				!= performanceSeatIds.size()) {
			throw new IllegalArgumentException("중복된 좌석이 포함되어 있습니다.");
		}

		if (holdToken == null || holdToken.isBlank()) {
			throw new IllegalArgumentException("좌석 선점 토큰이 없습니다.");
		} 

		if (paymentMethod == null || paymentMethod.isBlank()) {
			throw new IllegalArgumentException("결제 수단을 입력해 주세요.");
		}

		// 2. 회원 및 공연 조회
		Members member = memberRepository.findById(memberId)
				.orElseThrow(() ->
				new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Performances performance = performancesRepository.findById(performanceId)
				.orElseThrow(() ->
				new IllegalArgumentException("공연 정보를 찾을 수 없습니다."));

		LocalDateTime now = LocalDateTime.now();

		// 3. 토큰으로 선점된 좌석 조회 및 잠금
		List<PerformanceSeat> heldSeats =
				performanceSeatRepository.findByHoldTokenForUpdate(holdToken);

		if (heldSeats.isEmpty()) {
			throw new IllegalStateException(
					"선점된 좌석을 찾을 수 없습니다. 다시 좌석을 선택해 주세요.");
		}
		
		for (PerformanceSeat seat : heldSeats) {
		    if (seat.getHoldMember() == null
		            || !seat.getHoldMember().getMemberId().equals(memberId)) {
		        throw new ResponseStatusException(
		                HttpStatus.FORBIDDEN,
		                "해당 좌석의 선점 회원이 아닙니다."
		        );
		    }
		}
		

		// 4. 요청 좌석 목록과 토큰에 연결된 좌석 목록 비교
		List<Long> requestedIds = performanceSeatIds.stream()
				.sorted()
				.toList();

		List<Long> tokenSeatIds = heldSeats.stream()
				.map(PerformanceSeat::getPerformanceSeatId)
				.sorted()
				.toList();

		if (!requestedIds.equals(tokenSeatIds)) {
			throw new IllegalArgumentException(
					"요청한 좌석 목록과 선점한 좌석 목록이 일치하지 않습니다.");
		}

		// 가격 계산 및 주문 상세 생성 순서를 일치시키기 위해 정렬
		heldSeats.sort(
				java.util.Comparator.comparing(
						PerformanceSeat::getPerformanceSeatId));

		// 5. 토큰, 공연, 좌석 상태 및 만료 시각 검증
		for (PerformanceSeat seat : heldSeats) {

			if (!seat.canConfirm(holdToken, now)) {
				throw new IllegalStateException(
						"좌석 선점이 만료되었거나 유효하지 않습니다.");
			}

			if (!java.util.Objects.equals(
					seat.getPerformance().getPerformanceId(),
					performance.getPerformanceId())) {
				throw new IllegalArgumentException(
						"선택한 공연에 속하지 않는 좌석이 있습니다.");
			}
		}

		// 6. 서버에서 좌석별 가격 조회 및 총액 계산
		List<BigDecimal> prices = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (PerformanceSeat seat : heldSeats) {
			Integer price = seat.getSeat().getSeatGrade().getPrice();

			if (price == null || price < 0) {
				throw new IllegalStateException(
						"좌석 가격 정보가 올바르지 않습니다.");
			}

			BigDecimal detailPrice = BigDecimal.valueOf(price);

			prices.add(detailPrice);
			totalAmount = totalAmount.add(detailPrice);
		}

		// 7. 주문 생성
		String orderNo = "ORD-" + UUID.randomUUID();

		Orders order = new Orders(
				member,
				performance,
				orderNo,
				totalAmount
				);

		Orders savedOrder = ordersRepository.save(order);

		// 8. 기존 좌석 선점은 유지하고 주문 상세만 생성
		List<OrdersDetail> details = new ArrayList<>();

		for (int i = 0; i < heldSeats.size(); i++) {
			PerformanceSeat seat = heldSeats.get(i);

			// 주의: seat.hold(...)를 다시 호출하지 않는다.
			// 기존 holdToken과 holdExpiresAt을 유지한다.

			details.add(new OrdersDetail(
					seat,
					savedOrder,
					OrdersDetailStatus.PENDING,
					prices.get(i)
					));
		}

		ordersDetailRepository.saveAll(details);

		// 9. 결제 대기 생성
		Payments payment = new Payments(
				paymentMethod,
				totalAmount,
				savedOrder
				);

		paymentsRepository.save(payment);

		return savedOrder;
	}

	@Transactional
	public void expireOrder(Long ordersId) {

		List<OrdersDetail> details =
				ordersDetailRepository.findByOrders_OrdersId(ordersId);

		if (details.isEmpty()) {
			throw new IllegalArgumentException("주문 상세 정보가 없습니다.");
		}

		LocalDateTime now = LocalDateTime.now();

		// 모든 좌석의 만료 여부를 먼저 확인
		List<PerformanceSeat> seats = new ArrayList<>();

		for (OrdersDetail detail : details) {

			if (detail.getStatus() != OrdersDetailStatus.PENDING) {
				throw new IllegalStateException(
						"만료 처리할 수 없는 주문 상세 상태입니다.");
			}

			PerformanceSeat seat = performanceSeatRepository
					.findByIdForUpdate(
							detail.getPerformanceSeat().getPerformanceSeatId())
					.orElseThrow(() ->
					new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));

			if (!"HELD".equals(seat.getStatus())
					|| seat.getHoldExpiresAt() == null
					|| seat.getHoldExpiresAt().isAfter(now)) {
				throw new IllegalStateException(
						"아직 만료되지 않았거나 점유 상태가 올바르지 않습니다.");
			}

			seats.add(seat);
		}

		String holdToken = getHoldToken(details);
		
		boolean released = performanceSeatStatusService.releaseHeldSeats(ordersId, holdToken);
		
		if(!released) {
			throw new IllegalStateException("좌석 선점을 해제하지 못했습니다.");
		}
		
		for(OrdersDetail detail : details) {
			detail.expire();
		}

		performanceSeatRepository.saveAll(seats);
		ordersDetailRepository.saveAll(details);
	} // expireOrder

	@Transactional
	public void cancelOrder(Long ordersId) {

		List<OrdersDetail> details =
				ordersDetailRepository.findByOrders_OrdersId(ordersId);

		if (details.isEmpty()) {
			throw new IllegalArgumentException("주문 상세 정보가 없습니다.");
		}

		// 1. 결제 성공 여부 확인
		List<Payments> payments =
				paymentsRepository.findByOrders_OrdersId(ordersId);

		boolean hasSuccessfulPayment = payments.stream()
				.anyMatch(payment ->
				payment.getStatus() == PaymentStatus.SUCCESS);

		if (hasSuccessfulPayment) {
			throw new IllegalStateException(
					"결제가 완료된 주문은 환불 절차를 통해 취소해야 합니다.");
		}

		// 2. 주문 상세 상태 확인
		for (OrdersDetail detail : details) {
			if (detail.getStatus() != OrdersDetailStatus.PENDING) {
				throw new IllegalStateException(
						"취소할 수 없는 주문 상세 상태입니다.");
			}
		}

		// 3. 좌석을 잠그고 상태 확인
		List<PerformanceSeat> seats = new ArrayList<>();

		for (OrdersDetail detail : details) {
			PerformanceSeat seat = performanceSeatRepository
					.findByIdForUpdate(
							detail.getPerformanceSeat().getPerformanceSeatId())
					.orElseThrow(() ->
					new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));

			if (!"HELD".equals(seat.getStatus())) {
				throw new IllegalStateException(
						"결제 대기 상태가 아닌 좌석은 취소 처리할 수 없습니다.");
			}

			seats.add(seat);
		}

		String holdToken = getHoldToken(details);
		
		boolean released = performanceSeatStatusService.releaseHeldSeats(ordersId, holdToken);
		
		if(!released) {
			throw new IllegalStateException("좌석 선점을 해제하지 못했습니다.");
		}
		
		for(OrdersDetail detail : details) {
			detail.cancel();
		}
		

		performanceSeatRepository.saveAll(seats);
		ordersDetailRepository.saveAll(details);
	} // cancelOrder

	public OrdersResponseDTO getOrderByOrderNo(String orderNo, Long memberId) {
		Orders orders = ordersRepository.findByOrderNo(orderNo)
				.orElseThrow( () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

		if(!Objects.equals(orders.getMember().getMemberId(), memberId)) {
			throw new IllegalArgumentException("주문 정보를 찾을 수 없습니다.");
		}

		return new OrdersResponseDTO(orders);
	}

	public List<OrdersResponseDTO> getOrdersByMemberId(Long memberId){
		return ordersRepository.findByMember_MemberIdOrderByOrdersIdDesc(memberId)
				.stream()
				.map(OrdersResponseDTO::new)
				.toList();
	}

	public List<OrdersDetailResponseDTO> getOrderDetails(Long ordersId, Long memberId){

		Orders orders = ordersRepository.findById(ordersId)
				.orElseThrow( () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.") ); 

		if (!Objects.equals(orders.getMember().getMemberId(), memberId)) {
			throw new IllegalArgumentException("주문 정보를 찾을 수 없습니다.");
		}

		return ordersDetailRepository.findByOrders_OrdersId(ordersId)
				.stream()
				.map(OrdersDetailResponseDTO::new)
				.toList();
	}

} // OrderService