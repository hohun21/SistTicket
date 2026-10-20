package org.doit.sistticket.category.repository;

import java.util.Optional;

import org.doit.sistticket.category.entity.ConcertImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertImgRepository extends JpaRepository<ConcertImg,Long>{
	// 특정 콘서트의 썸네일 이미지 단건 조회
    Optional<ConcertImg> findByConcert_ConcertIdAndIsThumbnail(Long concertId, boolean isThumbnail);

	void deleteByConcert_ConcertId(Long concertId);
}
