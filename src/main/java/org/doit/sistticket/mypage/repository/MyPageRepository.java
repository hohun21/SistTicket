package org.doit.sistticket.mypage.repository;

import java.util.List;

import org.doit.sistticket.orders.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyPageRepository extends JpaRepository<OrdersDetail, Long> {
	
	List<OrdersDetail> findByOrders_Member_MemberIdOrderByOrderDetailIdDesc(Long memberId);
}
