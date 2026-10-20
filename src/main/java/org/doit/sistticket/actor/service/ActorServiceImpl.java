package org.doit.sistticket.actor.service;

import java.util.List;

import org.doit.sistticket.actor.domain.ActorDTO;
import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.repository.ActorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;

    @Override
    public List<Actor> getActorList() {
        return actorRepository.findAll();
    }

    @Override
    @Transactional
    public void saveActor(ActorDTO dto) {
        Actor actor = Actor.builder()
                .actorName(dto.getActorName())
                .profileUrl(dto.getProfileUrl())
                .build();
        actorRepository.save(actor);
    }

    @Override
    @Transactional
    public void deleteActor(Long id) {
        
        actorRepository.deleteById(id);
    }
}