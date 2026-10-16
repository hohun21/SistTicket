package org.doit.sistticket.category.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.venue.entity.Venue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Table(name="performance_casts")
public class PerformanceCast {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "performance_cast_id", nullable=false)
    private long performanceCastId;
    
	//performances - performances_casts
    @ManyToOne
    @JoinColumn(name = "performance_id", nullable=false)
    private Performances performance;
    
    //performance_casts - roles
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    //performances_casts  - actors
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;
	
}
