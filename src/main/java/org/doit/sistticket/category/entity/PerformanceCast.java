package org.doit.sistticket.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="performance_casts")
public class PerformanceCast {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "performance_cast_id", nullable=false)
    private long performanceCastId;
    
	//performances - performances_casts
    @ManyToOne
    @JoinColumn(name = "performance_id", nullable=false)
    private Performance performance;
    
    //performance_casts - roles
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    //performances_casts  - actors
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;
	
}
