package org.doit.sistticket.category.repository;


import java.util.List;

import org.doit.sistticket.category.entity.Performances;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformancesRepository extends JpaRepository<Performances,Long>{

	//특정 콘서트 ID에 속한 회차 목록 조회
    List<Performances> findByConcert_ConcertId(Long concertId);
    
 // 회차 삭제 시 활용
    void deleteByConcert_ConcertId(Long concertId);
}
