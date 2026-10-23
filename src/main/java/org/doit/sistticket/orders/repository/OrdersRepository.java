
package org.doit.sistticket.orders.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.doit.sistticket.admin.domain.AdminReservationDTO;
import org.doit.sistticket.orders.entity.Orders;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
	
	// 만료 시각이 지난 주문 조회
	List<Orders> findByHoldExpiresAtBefore(LocalDateTime now);
	
    Optional<Orders> findByOrderNo(String orderNo);

    // 회원별 주문 목록 조회
    List<Orders> findByMember_MemberIdOrderByOrdersIdDesc(Long memberId);
    
    // 주문을 잠근 상태로 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    		SELECT o
    		FROM Orders o
    		WHERE o.ordersId = :ordersId
    		""")
    Optional<Orders> findByIdForUpdate(@Param("ordersId") Long ordersId);

    // 관리자용 모든 예매내역 조회
    @Query("""
        SELECT new org.doit.sistticket.admin.domain.AdminReservationDTO(
            o.ordersId,
            o.orderNo,
            m.memberId,
            m.name,
            c.concertName,
            o.createdAt,
            o.totalAmount,
            CAST(pay.status AS string)
        )
        FROM Orders o
        JOIN o.member m
        JOIN o.performance per
        JOIN per.concert c
        LEFT JOIN Payments pay ON pay.orders = o
        ORDER BY o.createdAt DESC
    """)
    List<AdminReservationDTO> findAllReservations();

    @Query("""
    SELECT new org.doit.sistticket.admin.domain.AdminReservationDTO(
        o.ordersId,
        o.orderNo,
        m.memberId,
        m.name,
        c.concertName,
        o.createdAt,
        o.totalAmount,
        CAST(pay.status AS string)
    )
    FROM Orders o
    JOIN o.member m
    JOIN o.performance per
    JOIN per.concert c
    LEFT JOIN Payments pay ON pay.orders = o
    WHERE EXISTS (
        SELECT od.orderDetailId
        FROM OrdersDetail od
        WHERE od.orders = o
          AND od.status = :status
    )
    ORDER BY o.createdAt DESC
""")
    List<AdminReservationDTO> findCancellationReservations(
            @Param("status")OrdersDetailStatus status
            );

    boolean existsByHoldToken(String holdToken);

}
