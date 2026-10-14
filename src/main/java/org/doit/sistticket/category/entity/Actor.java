package org.doit.sistticket.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="actors")
public class Actor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "actor_id", nullable= false)
	private long actorId;
	
	@Column(name = "actor_name", nullable= false)
	private String actorName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "profile_url")
	private String profileUrl;
	

}
