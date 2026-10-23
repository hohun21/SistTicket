package org.doit.sistticket.orders.repository;

import java.util.List;

import org.doit.sistticket.admin.domain.AdminReservationDetailDTO;
import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {

    // 주문에 포함된 상세항목 조회.
    List<OrdersDetail> findByOrders_OrdersId(Long ordersId);

    // 이미 주문에 묶인 좌석을 선점 취소 API가 풀지 못하게 하는 조회 메서드
    @Query("""
            select case when count(d) > 0 then true else false end
            from OrdersDetail d
            where d.performanceSeat.performanceSeatId in :seatIds
              and d.status = :status
            """)
    boolean hasOrderForSeats(
            @Param("seatIds") List<Long> seatIds,
            @Param("status") OrdersDetailStatus status);

    // 주문의 모든 상세 항목이 PENDING인지 확인
    @Query("""
            SELECT case when count(d) = count(
            	case when d.status = :status then 1 end
            ) then true else false end
            FROM OrdersDetail d
            WHERE d.orders.ordersId = :ordersId
            """)
    boolean allDetailsHaveStatus(
            @Param("ordersId") Long ordersId,
            @Param("status") OrdersDetailStatus status);

    // 관리자용 예매 상세 조회
    @Query("""
                SELECT new org.doit.sistticket.admin.domain.AdminReservationDetailDTO(
                    od.orderDetailId,
                    sg.gradeName,
                    p.performanceSeq,
                    s.rowLabel,
                    s.seatNumber,
                    od.status,
                    od.price
                )
                FROM OrdersDetail od
                JOIN od.performanceSeat ps
                JOIN ps.performance p
                JOIN ps.seat s
                JOIN s.seatGrade sg
                WHERE od.orders.ordersId = :ordersId
            """)
    List<AdminReservationDetailDTO> findReservationDetails(
            @Param("ordersId") Long ordersId
    );

    @Query("""
                SELECT new org.doit.sistticket.admin.domain.AdminReservationDetailDTO(
                    od.orderDetailId,
                    sg.gradeName,
                    p.performanceSeq,
                    s.rowLabel,
                    s.seatNumber,
                    od.status,
                    od.price
                )
                FROM OrdersDetail od
                JOIN od.performanceSeat ps
                JOIN ps.performance p
                JOIN ps.seat s
                JOIN s.seatGrade sg
                WHERE od.orders.ordersId = :ordersId
                  AND od.status = :status
            """)
    List<AdminReservationDetailDTO> findCancellationReservationDetails(
            @Param("ordersId") Long ordersId,
            @Param("status") OrdersDetailStatus status
    );

    // 취소 승인
    @Modifying
    @Query("""
                UPDATE OrdersDetail od 
                SET od.status = org.doit.sistticket.orders.enums.OrdersDetailStatus.CANCELLED
                WHERE od.orderDetailId =:orderDetailId       
            """)
    int approveCancellation(@Param("orderDetailId") Long orderDetailId);
} // 리파지토리