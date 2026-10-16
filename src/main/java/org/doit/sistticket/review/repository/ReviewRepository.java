package org.doit.sistticket.review.repository;


import java.util.List;

import org.doit.sistticket.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Reviews, Long>{
	
	List<Reviews> findByConcertId(Long concertId);
	
	List<Reviews> findByMemberId(Long memberId);
	
}
