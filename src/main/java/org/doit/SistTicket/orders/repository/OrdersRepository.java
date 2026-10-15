
package org.doit.sistticket.orders.repository;

import java.util.List;
import java.util.Optional;

import org.doit.sistticket.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByOrderNo(String orderNo);

    // 회원별 주문 목록 조회
    List<Orders> findByMember_MemberIdOrderByOrdersIdDesc(Long memberId);
}
