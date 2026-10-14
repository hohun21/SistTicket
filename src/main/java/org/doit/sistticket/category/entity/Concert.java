package org.doit.sistticket.category.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="concerts")
public class Concert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "concert_id", nullable=false)
	private long concertId;
	
	@Column(name = "venue_id", nullable=false)
	private long venueId;
	
	@Column(name = "start_date")
	private LocalDateTime startDate;
	
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@Column(name = "concert_name", nullable=false)
	private String concert_name;
	
	@Column(name = "run_time")
	private Duration runTime;
	
	@Column(name = "status")
	private String status;
	

}
