package org.doit.sistticket.actor.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActorRequestDTO {
    private String actorName;
  
    private String description; // 배우 설명 추가
}