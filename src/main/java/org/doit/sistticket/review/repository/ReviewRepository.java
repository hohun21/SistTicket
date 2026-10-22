package org.doit.sistticket.review.repository;

import java.util.List;

import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.entity.Reviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Reviews, Long>{
	
	List<Reviews> findByConcert_ConcertId(Long concertId);
	
	List<Reviews> findByMember_MemberId(Long memberId);
	
	@Query("""
	       SELECT new org.doit.sistticket.review.dto.ReviewDTO(
	           r.reviewId, r.member.memberId, r.member.name, r.concert.concertId, r.concert.concertName,
	           r.rating, r.text, r.imgUrl, r.createdAt, r.updatedAt
	       )
	       FROM Reviews r
	       WHERE r.member.memberId = :memberId
	       ORDER BY r.createdAt DESC
	       """)
	Page<ReviewDTO> findMyReviewsWithConcertTitle(@Param("memberId") Long memberId, Pageable pageable);
	
	boolean existsByMember_MemberIdAndConcert_ConcertId(Long memberId, Long concertId); 
	
	@Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Reviews r WHERE r.concert.concertId = :concertId")
	Double findAverageRatingByConcertId(@Param("concertId") Long concertId);
	
	@Query("SELECT COUNT(r) FROM Reviews r WHERE r.concert.concertId = :concertId")
	Long countByConcertId(@Param("concertId") Long concertId);
}