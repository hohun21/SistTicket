package org.doit.sistticket.order.repository;

import java.util.List;
import java.util.Optional;

import org.doit.sistticket.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long>{

    // 주문번호로 주문 조회
    Optional<Orders> findByOrderNo(String orderNo);

    // 회원별 주문 목록 조회
    List<Orders> findByMemberIdOrderByOrdersIdDesc(Long memberId);
}
