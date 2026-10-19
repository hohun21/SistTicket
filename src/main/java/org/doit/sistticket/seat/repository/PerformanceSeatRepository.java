package org.doit.sistticket.seat.repository;

import java.util.List;

import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface PerformanceSeatRepository
        extends JpaRepository<PerformanceSeat, Long> {

	// 공연 회차 좌석 조회
    @Query("""
        select ps
        from PerformanceSeat ps
        join fetch ps.seat s
        join fetch s.seatGrade
        where ps.performance.performanceId = :performanceId
        order by s.rowLabel, s.seatNumber
        """)
    List<PerformanceSeat> findSeatsByPerformanceId(
            @Param("performanceId") Long performanceId);

    // 임시 선점 좌석 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select ps
        from PerformanceSeat ps
        where ps.performance.performanceId = :performanceId
          and ps.performanceSeatId in :seatIds
        order by ps.performanceSeatId
        """)
    List<PerformanceSeat> findSeatsForHold(
            @Param("performanceId") Long performanceId,
            @Param("seatIds") List<Long> seatIds);

    // 예약 시 좌석 잠금 후 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select ps
        from PerformanceSeat ps
        where ps.holdToken = :holdToken
        order by ps.performanceSeatId
        """)
    List<PerformanceSeat> findByHoldTokenForUpdate(
            @Param("holdToken") String holdToken);
}