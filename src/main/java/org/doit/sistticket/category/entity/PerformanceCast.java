package org.doit.sistticket.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="performance_casts")
public class PerformanceCast {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "performance_cast_id", nullable=false)
	private long performanceCastId;
	
	@Column(name = "performance_id", nullable=false)
	private long performanceId;
	
	@Column(name = "role_id")
	private long roleId;
	
	
	@Column(name = "actor_id")
	private long actorId;
	
}
