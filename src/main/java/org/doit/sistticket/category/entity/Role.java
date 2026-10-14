package org.doit.sistticket.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="roles")
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "role_id", nullable=false)
	private long roleId;
	
	@Column(name = "concert_id", nullable=false)
	private long concertId;
	
	@Column(name = "role_name", nullable=false)
	private String roleName;
	
	@Column(name = "description")
	private String description;
	

}
