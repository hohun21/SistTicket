package org.doit.sistticket.actor.service;

import java.util.List;

import org.doit.sistticket.actor.domain.ActorDTO;
import org.doit.sistticket.actor.entity.Actor;

public interface ActorService {
    List<Actor> getActorList();
    void saveActor(ActorDTO dto);
    void deleteActor(Long id);
}