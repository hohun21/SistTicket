package org.doit.sistticket.orders.service;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.orders.entity.Orders;
import org.doit.sistticket.orders.repository.OrdersRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {

	private final OrdersService ordersService;
	private final OrdersRepository ordersRepository;
	
	// 1분 마다 만료 후보 주문 확인
	@Scheduled(fixedDelay = 60_000)
	public void expireOrders() {
		List<Orders> expiredCandidates = ordersRepository.findByHoldExpiresAtBefore(LocalDateTime.now());
		
		for(Orders order : expiredCandidates) {
			try {
				ordersService.expireOrder(order.getOrdersId());
			} catch (IllegalStateException | IllegalArgumentException e) {
				// 이미 결제 되었거나 다른 처리로 상태가 변경된 경우
				log.debug("주문 만료 처리 생략 : orderId = {}, reason={}",
						order.getOrdersId(), e.getMessage());
			} catch (Exception e) {
				// 한 주문의 실패가 나머지 주문 처리를 막지 않도록한다.
				log.error("주문 만료 처리 실패 : orderId={}",
						order.getOrdersId(),e);
			}
		} // for
	}
}
