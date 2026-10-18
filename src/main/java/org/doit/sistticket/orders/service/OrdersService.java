package org.doit.sistticket.orders.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Orders createOrder(
            Long memberId,
            Long performanceId,
            List<Long> performanceSeatIds,
            String paymentMethod) {

        // 1. 요청값 검증
        if (performanceSeatIds == null || performanceSeatIds.isEmpty()) {
            throw new IllegalArgumentException("선택한 좌석이 없습니다.");
        }

        if (new HashSet<>(performanceSeatIds).size()
                != performanceSeatIds.size()) {
            throw new IllegalArgumentException("중복된 좌석이 포함되어 있습니다.");
        }

        if (performanceSeatIds.stream().anyMatch(id -> id == null)) {
            throw new IllegalArgumentException("좌석 ID가 올바르지 않습니다.");
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

        // 3. 좌석 ID를 정렬해 일정한 순서로 잠금 획득
        List<Long> sortedIds = performanceSeatIds.stream()
                .sorted()
                .toList();

        List<PerformanceSeat> seats = new ArrayList<>();

        for (Long seatId : sortedIds) {
            PerformanceSeat seat = performanceSeatRepository
                    .findByIdForUpdate(seatId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "좌석 정보를 찾을 수 없습니다: " + seatId));

            // 4. 선택한 좌석이 해당 공연에 속하는지 확인
            if (seat.getPerformance().getPerformanceId()
                    != performance.getPerformanceId()) {
                throw new IllegalArgumentException(
                        "선택한 공연에 속하지 않는 좌석이 있습니다.");
            }

            // 5. 좌석 상태 검증
            String status = seat.getStatus();

            if ("RESERVED".equals(status)) {
                throw new IllegalStateException(
                        "이미 예약 완료된 좌석입니다: " + seatId);
            }

            if ("HELD".equals(status)) {
                LocalDateTime expiresAt = seat.getHoldExpiresAt();

                if (expiresAt == null || expiresAt.isAfter(now)) {
                    throw new IllegalStateException(
                            "다른 사용자가 결제진행 중인 좌석입니다: " + seatId);
                }
            } else if (!"AVAILABLE".equals(status)) {
                throw new IllegalStateException(
                        "예약할 수 없는 좌석 상태입니다: " + seatId);
            }

            seats.add(seat);
        }

        // 6. 서버에서 좌석별 가격을 조회해 총액 계산
        List<BigDecimal> prices = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PerformanceSeat seat : seats) {
            Integer price = seat.getSeat().getSeatGrade().getPrice();

            if (price == null || price < 0) {
                throw new IllegalStateException(
                        "좌석 가격 정보가 올바르지 않습니다.");
            }

            BigDecimal detailPrice = BigDecimal.valueOf(price);

            prices.add(detailPrice);
            totalAmount = totalAmount.add(detailPrice);
        }

        // 7. 주문 생성올
        String orderNo = "ORD-" + UUID.randomUUID();

        Orders order = new Orders(
                member,
                performance,
                orderNo,
                totalAmount
        );

        Orders savedOrder = ordersRepository.save(order);

        // 8. 좌석 임시 점유 및 주문 상세 생성
        List<OrdersDetail> details = new ArrayList<>();

        for (int i = 0; i < seats.size(); i++) {
            PerformanceSeat seat = seats.get(i);

            seat.hold(now.plusMinutes(30));

            details.add(new OrdersDetail(
                    seat,
                    savedOrder,
                    OrdersDetailStatus.PENDING,
                    prices.get(i)
            ));
        }

        performanceSeatRepository.saveAll(seats);
        ordersDetailRepository.saveAll(details);

        // 9. 결제 대기 생성
        Payments payment = new Payments(
                paymentMethod,
                totalAmount,
                savedOrder
        );

        paymentsRepository.save(payment);

        return savedOrder;
    } // createOrder
    
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

        // 검증이 모두 끝난 뒤 일괄 만료
        for (int i = 0; i < details.size(); i++) {
            seats.get(i).release();
            details.get(i).expire();
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

        // 4. 좌석 해제 및 주문 상세 취소
        for (int i = 0; i < details.size(); i++) {
            seats.get(i).release();
            details.get(i).cancel();
        }

        performanceSeatRepository.saveAll(seats);
        ordersDetailRepository.saveAll(details);
    } // cancelOrder
    
    public OrdersResponseDTO getOrderByOrderNo(String orderNo) {
    	Orders orders = ordersRepository.findByOrderNo(orderNo)
    									.orElseThrow( () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));
    	
    	return new OrdersResponseDTO(orders);
    }
    
    public List<OrdersResponseDTO> getOrdersByMemberId(Long memberId){
    	return ordersRepository.findByMember_MemberIdOrderByOrdersIdDesc(memberId)
    							.stream()
    							.map(OrdersResponseDTO::new)
    							.toList();
    }
    
    public List<OrdersDetailResponseDTO> getOrderDetails(Long ordersId){
    	
    	if (!ordersRepository.existsById(ordersId)) {
			throw new IllegalArgumentException("주문 정보를 찾을 수 없습니다.");
		}
    	
    	return ordersDetailRepository.findByOrders_OrdersId(ordersId)
    								 .stream()
    								 .map(OrdersDetailResponseDTO::new)
    								 .toList();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
} // OrderService