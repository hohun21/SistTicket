package org.doit.sistticket.category.repository;


import java.util.List;

import org.doit.sistticket.category.entity.Performances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerformancesRepository extends JpaRepository<Performances,Long>{

	//특정 콘서트 ID에 속한 회차 목록 조회
    List<Performances> findByConcert_ConcertIdOrderByPerformanceDateAsc(Long concertId);
    
 // 회차 삭제 시 활용
    void deleteByConcert_ConcertId(Long concertId);

 // 특정 콘서트 ID에 속한 회차 중 가장 큰 sequence 값을 조회
    @Query("SELECT MAX(p.performanceSeq) FROM Performances p WHERE p.concert.concertId = :concertId")
    Integer findMaxSeqByConcertId(@Param("concertId") Long concertId);
}
