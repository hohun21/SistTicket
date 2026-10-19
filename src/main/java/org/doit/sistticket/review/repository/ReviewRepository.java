package org.doit.sistticket.review.repository;


import java.util.List;

import org.doit.sistticket.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Reviews, Long>{
	
	List<Reviews> findByConcert_ConcertId(Long concertId);
	
	List<Reviews> findByMember_MemberId(Long memberId);
	
}
