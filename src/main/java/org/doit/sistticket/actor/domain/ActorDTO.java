package org.doit.sistticket.actor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActorDTO {
    private String actorName;
    private String description;
    private String profileURL; 
}