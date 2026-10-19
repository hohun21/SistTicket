package org.doit.sistticket.category.repository;

import java.util.List;

import org.doit.sistticket.category.entity.PerformanceCast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceCastRepository extends JpaRepository<PerformanceCast,Long>{
	//회차 ID 목록에 포함되는 캐스팅 정보 조회
    List<PerformanceCast> findByPerformance_PerformanceIdIn(List<Long> performanceIds);
}
