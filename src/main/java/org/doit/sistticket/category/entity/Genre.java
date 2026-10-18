package org.doit.sistticket.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Setter;

@Entity
@Table(name="genre")
@Setter
public class Genre {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "genre_id", nullable=false)
	private long genre_id;
	

	@Column(name = "genre_name", nullable=false)
	private String genreName;

	

}
