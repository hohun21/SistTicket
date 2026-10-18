package org.doit.sistticket.orders.controller;

import java.util.List;

import org.doit.sistticket.orders.dto.OrdersDetailResponseDTO;
import org.doit.sistticket.orders.dto.OrdersResponseDTO;
import org.doit.sistticket.orders.service.OrdersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrdersController {

	private final OrdersService ordersService;
	
	// 주문번호로 주문 조회
	@GetMapping("/number/{orderNo}")
	public OrdersResponseDTO getOrderByOrderNo(@PathVariable String orderNo) {
		return ordersService.getOrderByOrderNo(orderNo);
	}
	
	// 회원별 주문 내역 조회
	@GetMapping("/member/{memeberId}")
	public List<OrdersResponseDTO> getOrdersByMemberId(@PathVariable Long memberId) {
		return ordersService.getOrdersByMemberId(memberId);
	}
	
	@GetMapping("/{ordersId}/details")
	public List<OrdersDetailResponseDTO> getOrderDetails(@PathVariable Long ordersId) {
		return ordersService.getOrderDetails(ordersId);
	}
	
	
	
}
