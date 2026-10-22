
package org.doit.sistticket.seat.repository;

import java.util.List;
import java.util.Optional;

import org.doit.sistticket.seat.dto.SeatPerformanceResponse;
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
    
    // 좌석 화면에 표시할 공연 회차 정보 조회
    @Query("""
        select new org.doit.sistticket.seat.dto.SeatPerformanceResponse(
		    p.performanceId,
		    p.performanceSeq,
		    c.concertName,
		    p.performanceDate,
		    v.venueName
		)
        from Performances p
        join p.concert c
        join c.venue v
        where p.performanceId = :performanceId
        """)
    Optional<SeatPerformanceResponse> findPerformanceInfo(
            @Param("performanceId") Long performanceId);
    
    // 해당 회차에 이미 좌석이 생성되어 있는지 확인
    boolean existsByPerformance_PerformanceId(Long performanceId);
    
    // 현재 회차와 같은 공연에 속한 회차 목록 조회
    @Query("""
        select new org.doit.sistticket.seat.dto.SeatPerformanceResponse(
		    p.performanceId,
		    p.performanceSeq,
		    c.concertName,
		    p.performanceDate,
		    v.venueName
		)
        from Performances p
        join p.concert c
        join c.venue v
        where c.concertId = (
            select base.concert.concertId
            from Performances base
            where base.performanceId = :performanceId
        )
        order by p.performanceDate, p.performanceId
        """)
    List<SeatPerformanceResponse> findSchedules(
            @Param("performanceId") Long performanceId);
}
