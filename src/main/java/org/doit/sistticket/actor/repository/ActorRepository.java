package org.doit.sistticket.actor.repository;

import org.doit.sistticket.actor.entity.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor,Long>{
	// 배우 이름에 검색어가 포함된 목록을 페이징과 함께 조회
    Page<Actor> findByActorNameContaining(String actorName, Pageable pageable);
}
