
package org.doit.sistticket.seat.repository;

import java.util.List;
import java.util.Optional;

import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface PerformanceSeatRepository
        extends JpaRepository<PerformanceSeat, Long> {

    // 특정 공연 회차의 좌석 조회
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

    // ID로 좌석을 잠금 조회: 주문·결제 서비스용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select ps
        from PerformanceSeat ps
        where ps.performanceSeatId = :id
        """)
    Optional<PerformanceSeat> findByIdForUpdate(
            @Param("id") Long id);

    // 특정 공연의 여러 좌석을 잠금 조회
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

    // 홀드 토큰으로 좌석 잠금 조회
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
