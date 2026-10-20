package org.doit.sistticket.actor.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ActorDTO {
    private Long actorId;
    private String actorName;
    private String profileUrl;
}