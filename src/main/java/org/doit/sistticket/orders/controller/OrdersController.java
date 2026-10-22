package org.doit.sistticket.orders.controller;

import java.util.List;

import org.doit.sistticket.orders.dto.OrderCreateResponseDTO;
import org.doit.sistticket.orders.dto.OrdersCreateRequestDTO;
import org.doit.sistticket.orders.dto.OrdersDetailResponseDTO;
import org.doit.sistticket.orders.dto.OrdersResponseDTO;
import org.doit.sistticket.orders.entity.Orders;
import org.doit.sistticket.orders.service.OrdersService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrdersController {

	private final OrdersService ordersService;
	
	// 주문번호로 주문 조회
	@GetMapping("/number/{orderNo}")
	public OrdersResponseDTO getOrderByOrderNo(
			@PathVariable String orderNo
			,@AuthenticationPrincipal CustomerUserDetails userDetails) {
		Long memberId = userDetails.getMember().getMemberId();
		
		return ordersService.getOrderByOrderNo(orderNo,memberId);
	}
	
	// 회원별 주문 내역 조회
	@GetMapping("/member")
	public List<OrdersResponseDTO> getMyOrders(
			@AuthenticationPrincipal CustomerUserDetails userDetails){
		Long memberId = userDetails.getMember().getMemberId();
		return ordersService.getOrdersByMemberId(memberId);
	}
	
	@GetMapping("/{ordersId}/details")
	public List<OrdersDetailResponseDTO> getOrderDetails(
			@PathVariable Long ordersId
			,@AuthenticationPrincipal CustomerUserDetails userDetails) {
		
		Long memberId = userDetails.getMember().getMemberId();
		
		return ordersService.getOrderDetails(ordersId, memberId);
	}
	
	@PostMapping
	public OrderCreateResponseDTO createOrder(
			@RequestBody OrdersCreateRequestDTO request
			,@AuthenticationPrincipal CustomerUserDetails userDetails) {
		
		Long memberId = userDetails.getMember().getMemberId();
		
		return ordersService.createOrder(
		        memberId,
		        request.getPerformanceId(),
		        request.getPerformanceSeatIds(),
		        request.getPaymentMethod(),
		        request.getHoldToken()
		);
	}
	
	
	
}
