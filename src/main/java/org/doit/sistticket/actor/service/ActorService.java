package org.doit.sistticket.actor.service;

import java.util.List;

import org.doit.sistticket.actor.domain.ActorDTO;
import org.doit.sistticket.actor.domain.ActorRequestDTO;
import org.doit.sistticket.actor.entity.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActorService {
    List<Actor> getActorList();
    void saveActor(ActorRequestDTO dto);                 // 배우 등록 
    void updateActor(Long actorId, ActorRequestDTO dto); // 배우 수정
    void deleteActor(Long actorId);                      // 배우 삭제
    Page<Actor> getActorList(String keyword, Pageable pageable); // 검색 및 페이징 목록 조회

}