package org.doit.sistticket.payments.repository;

import java.util.List;
import java.util.Optional;

import org.doit.sistticket.payments.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface PaymentsRepository extends JpaRepository<Payments, Long> {
	
	@Lock(LockModeType.WRITE)
	@Query("""
			SELECT p
			FROM Payments p
			WHERE p.paymentId = :paymentId
			""")
	Optional<Payments> findByIdForUpdate(@Param("paymentId") Long paymentId);
	
	List<Payments> findByOrders_OrdersId(Long ordersId);
	
}