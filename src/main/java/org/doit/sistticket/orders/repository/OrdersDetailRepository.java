package org.doit.sistticket.orders.repository;

import java.util.List;

import org.doit.sistticket.orders.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {

	// 주문에 포함된 상세항목 조회.
	List<OrdersDetail> findByOrders_OrdersId(Long ordersId);
	
	
}