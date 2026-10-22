package org.doit.sistticket.orders.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.doit.sistticket.admin.domain.AdminReservationDTO;
import org.doit.sistticket.admin.domain.AdminReservationDetailDTO;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.orders.dto.OrdersDetailResponseDTO;
import org.doit.sistticket.orders.dto.OrdersResponseDTO;
import org.doit.sistticket.orders.dto.OrderCreateResponseDTO;
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
import org.doit.sistticket.seat.service.SeatBookingPolicy;
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
	private final SeatBookingPolicy seatBookingPolicy;

	@Transactional
	public OrderCreateResponseDTO createOrder(
			Long memberId,
			Long performanceId,
			List<Long> performanceSeatIds,
			String paymentMethod,
			String holdToken) {

		// 1. 선택 좌석 검증
		seatBookingPolicy.validateSeats(performanceSeatIds);

		if (holdToken == null || holdToken.isBlank()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "좌석 선점 토큰이 없습니다.");
		}

		if (paymentMethod == null || paymentMethod.isBlank()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "결제 수단을 입력해 주세요.");
		}

		// 2. 공연 회차 ID 확인
		if (performanceId == null || performanceId <= 0) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"공연 회차 ID가 올바르지 않습니다.");
		}

		// 3. 선점 좌석 잠금
		List<PerformanceSeat> heldSeats =
				performanceSeatRepository.findByHoldTokenForUpdate(holdToken);

		// 선점이 이미 해제되거나 다른 토큰으로 바뀐 경우
		if (heldSeats.isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT, "선점된 좌석을 찾을 수 없습니다. 다시 좌석을 선택해 주세요.");
		}

		// 로그인 회원이 선점한 좌석인지 확인
		for (PerformanceSeat seat : heldSeats) {
			if (seat.getHoldMember() == null
					|| !seat.getHoldMember().getMemberId().equals(memberId)) {
				throw new ResponseStatusException(
						HttpStatus.FORBIDDEN, "본인이 선점한 좌석으로만 주문할 수 있습니다.");
			}
		}

		// 잠금 이후 공연·회원 정보 조회
		Performances performance =
				performancesRepository.findById(performanceId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "공연 회차를 찾을 수 없습니다."));

		Members member = memberRepository.findById(memberId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

		// 잠금을 기다리는 동안 예매 기간이나 선점 기한이 끝났는지 확인
		LocalDateTime now = LocalDateTime.now();

		seatBookingPolicy.validatePerformance(performance, now);

		// 4. 요청 좌석 목록과 토큰에 연결된 좌석 목록 비교
		List<Long> requestedIds = performanceSeatIds.stream()
				.sorted()
				.toList();

		List<Long> tokenSeatIds = heldSeats.stream()
				.map(PerformanceSeat::getPerformanceSeatId)
				.sorted()
				.toList();

		if (!requestedIds.equals(tokenSeatIds)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT, "요청한 좌석 목록과 선점한 좌석 목록이 일치하지 않습니다.");
		}

		// 가격 계산 및 주문 상세 생성 순서를 일치시키기 위해 정렬
		heldSeats.sort(
				java.util.Comparator.comparing(
						PerformanceSeat::getPerformanceSeatId));

		// 5. 토큰, 공연, 좌석 상태 및 만료 시각 검증
		for (PerformanceSeat seat : heldSeats) {
			if (!seat.canConfirm(holdToken, now)) {
				throw new ResponseStatusException(
						HttpStatus.CONFLICT, "좌석 선점이 만료되었거나 유효하지 않습니다.");
			}

			if (!java.util.Objects.equals(
					seat.getPerformance().getPerformanceId(),
					performance.getPerformanceId())) {
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "선택한 공연에 속하지 않는 좌석이 있습니다.");
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

		// 주문 생성 시점의 홀드 토큰과 만료 시각을 주문에 보관
		LocalDateTime holdExpiresAt = heldSeats.get(0).getHoldExpiresAt();

		Orders order = new Orders(
				member,
				performance,
				orderNo,
				totalAmount,
				holdToken,
				holdExpiresAt
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

		Payments savedPayment = paymentsRepository.save(payment);

		return OrderCreateResponseDTO.from(
		        savedOrder,
		        savedPayment.getPaymentId()
		);
	} // createOrder

	@Transactional
	public void expireOrder(Long ordersId) {

		// 1. 주문 조회
		Orders order = ordersRepository.findByIdForUpdate(ordersId)
				.orElseThrow(() ->
				new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

		// 2. 주문 상세 조회
		List<OrdersDetail> details =
				ordersDetailRepository.findByOrders_OrdersId(ordersId);

		if (details.isEmpty()) {
			throw new IllegalArgumentException("주문 상세 정보가 없습니다.");
		}

		// 3. 결제 성공 여부 확인
		List<Payments> payments =
				paymentsRepository.findByOrders_OrdersId(ordersId);

		boolean hasSuccessfulPayment = payments.stream()
				.anyMatch(payment ->
				payment.getStatus() == PaymentStatus.SUCCESS);

		if (hasSuccessfulPayment) {
			throw new IllegalStateException(
					"결제가 완료된 주문은 만료 처리할 수 없습니다.");
		}

		// 4. 주문 상세 상태 확인
		for (OrdersDetail detail : details) {
			if (detail.getStatus() != OrdersDetailStatus.PENDING) {
				throw new IllegalStateException(
						"만료 처리할 수 없는 주문 상세 상태입니다.");
			}
		}

		// 5. 주문에 저장된 최초 홀드 정보 확인
		LocalDateTime now = LocalDateTime.now();

		if (order.getHoldToken() == null
				|| order.getHoldToken().isBlank()
				|| order.getHoldExpiresAt() == null
				|| order.getHoldExpiresAt().isAfter(now)) {
			throw new IllegalStateException(
					"홀드 토큰이 없거나 아직 만료되지 않은 주문입니다.");
		}

		// 6. 아직 원래 토큰이 남아 있는 만료 좌석만 해제
		performanceSeatStatusService.releaseExpiredSeats(
				ordersId, order.getHoldToken());

		// 7. 주문 상세 만료 처리
		for (OrdersDetail detail : details) {
			detail.expire();
		}

		ordersDetailRepository.saveAll(details);

		// 8. 미결제 상태의 결제도 만료 처리
		for (Payments payment : payments) {
			PaymentStatus status = payment.getStatus();

			if (status == PaymentStatus.PENDING
					|| status == PaymentStatus.FAILED) {
				payment.cancelUnpaidPayment();
			}
		}
	} // expireOrder

	@Transactional
	public void cancelOrder(Long ordersId) {

		// 주문 행 잠금
		Orders order = ordersRepository.findByIdForUpdate(ordersId)
				.orElseThrow(() ->
				new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

		// 주문 상세 조회
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

		// 3. 주문 상세에서 좌석 ID 추출
		List<Long> seatIds = details.stream()
				.map(detail ->
				detail.getPerformanceSeat().getPerformanceSeatId())
				.sorted()
				.toList();

		// 4. 주문의 공연 ID 확인
		Long performanceId = order.getPerformance().getPerformanceId();

		// 5. 좌석을 ID 순서로 잠금 조회
		List<PerformanceSeat> seats =
				performanceSeatRepository.findSeatsForHold(
						performanceId, seatIds);

		if (seats.size() != seatIds.size()) {
			throw new IllegalStateException(
					"주문에 해당하는 좌석을 모두 찾을 수 없습니다.");
		}

		// 6. 좌석 상태 확인
		for (PerformanceSeat seat : seats) {
			if (!"HELD".equals(seat.getStatus())) {
				throw new IllegalStateException(
						"결제 대기 상태가 아닌 좌석은 취소 처리할 수 없습니다.");
			}
		}

		// 주문 생성 당시 저장한 홀드 토큰 사용
		String holdToken = order.getHoldToken();

		if (holdToken == null || holdToken.isBlank()) {
			throw new IllegalStateException("주문의 홀드 토큰이 없습니다.");
		}

		boolean released =
				performanceSeatStatusService.releaseHeldSeats(ordersId, holdToken);

		if (!released) {
			throw new IllegalStateException("좌석 선점을 해제하지 못했습니다.");
		}

		// 7. 주문 상세 취소 처리
		for (OrdersDetail detail : details) {
			detail.cancel();
		}

		performanceSeatRepository.saveAll(seats);
		ordersDetailRepository.saveAll(details);

		// 8. 미결제 상태의 결제도 취소 처리
		for (Payments payment : payments) {
			PaymentStatus status = payment.getStatus();

			if (status == PaymentStatus.PENDING
					|| status == PaymentStatus.FAILED) {
				payment.cancelUnpaidPayment();
			}
		}

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
	// 관리자 모든 예매내역 조회용 서비스
	public List<AdminReservationDTO> getAllReservations() {
		return ordersRepository.findAllReservations();
	}
	// 관리자 모든 예매내역 상세 조회용 서비스
	public List<AdminReservationDetailDTO> getAllReservationDetails(Long ordersId) {
		return this.ordersDetailRepository.findReservationDetails(ordersId);
	}

} // OrderService