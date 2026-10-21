package org.doit.sistticket.actor.service;

import java.util.List;

import org.doit.sistticket.actor.domain.ActorRequestDTO;
import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.repository.ActorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;

    @Override
    public List<Actor> getActorList() {
        return actorRepository.findAll();
    }

    @Override
    public void saveActor(ActorRequestDTO dto) {
        Actor actor = Actor.builder()
                .actorName(dto.getActorName())
                .description(dto.getDescription())
                .build();
        actorRepository.save(actor);
    }

    @Override
    public void updateActor(Long actorId, ActorRequestDTO dto) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배우입니다. ID: " + actorId));
        
        actor.setActorName(dto.getActorName());
        actor.setDescription(dto.getDescription());
    }

    @Override
    public void deleteActor(Long actorId) {
        actorRepository.deleteById(actorId);
    }

    @Override
    @Transactional
    public Page<Actor> getActorList(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return actorRepository.findByActorNameContaining(keyword, pageable);
        }
        return actorRepository.findAll(pageable);
    }
}