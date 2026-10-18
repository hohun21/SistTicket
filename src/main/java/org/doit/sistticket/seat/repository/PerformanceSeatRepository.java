package org.doit.sistticket.seat.repository;

import java.util.Optional;

import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface PerformanceSeatRepository
extends JpaRepository<PerformanceSeat, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT ps
			FROM PerformanceSeat ps 
			WHERE ps.performanceSeatId = :id
			""")
	Optional<PerformanceSeat> findByIdForUpdate(@Param("id") Long id);
	
}
