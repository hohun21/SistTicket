package org.doit.sistticket.orders.repository;

import java.util.List;

import org.doit.sistticket.orders.entity.OrdersDetail;
import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}