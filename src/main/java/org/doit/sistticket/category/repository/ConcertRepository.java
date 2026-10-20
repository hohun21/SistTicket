package org.doit.sistticket.category.repository;

import org.doit.sistticket.category.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConcertRepository extends JpaRepository<Concert,Long>{
	// 특정 장르 ID에 해당하는 콘서트 페이징 조회
    Page<Concert> findByGenre_GenreId(Long genreId, Pageable pageable);
    
    // 장르 ID 범위로 조회 (예: 뮤지컬 장르 id 8~9 등 필요할 때)
    Page<Concert> findByGenre_GenreIdBetween(Long startId, Long endId, Pageable pageable);
    
 // 💡 JPQL을 활용한 타입별(n, v, nv) + 장르 필터 동적 검색 쿼리
    @Query("SELECT c FROM Concert c " +
           "LEFT JOIN c.venue v " +
           "WHERE (:genreId IS NULL OR c.genre.genreId = :genreId) " +
           "AND (" +
           "  :keyword IS NULL OR :keyword = '' OR " +
           "  (:type = 'n' AND c.concertName LIKE CONCAT('%', :keyword, '%')) OR " +
           "  (:type = 'v' AND v.venueName LIKE CONCAT('%', :keyword, '%')) OR " +
           "  (:type = 'nv' AND (c.concertName LIKE CONCAT('%', :keyword, '%') OR v.venueName LIKE CONCAT('%', :keyword, '%')))" +
           ")")
    Page<Concert> searchConcerts(
            @Param("genreId") Long genreId,
            @Param("type") String type,
            @Param("keyword") String keyword,
            Pageable pageable);
}
