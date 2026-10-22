package org.doit.sistticket.category.repository;

import java.util.List;

import org.doit.sistticket.category.entity.PerformanceCast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceCastRepository extends JpaRepository<PerformanceCast,Long>{
	//회차 ID 목록에 포함되는 캐스팅 정보 조회
    List<PerformanceCast> findByPerformance_PerformanceIdIn(List<Long> performanceIds);
    
    
    // 특정 회차에 속한 캐스팅 목록 조회
    List<PerformanceCast> findByPerformance_PerformanceId(Long performanceId);

    // 회차 삭제 시 연관된 캐스팅 정보 삭제
    void deleteByPerformance_PerformanceId(Long performanceId);
    
 
    //특정 배역 삭제 시 연관된 출연진 전체 삭제
    void deleteByRole_RoleId(Long roleId);
    
    //콘서트 삭제시 얘도 삭제
    void deleteByPerformance_Concert_ConcertId(Long concertId);
}
