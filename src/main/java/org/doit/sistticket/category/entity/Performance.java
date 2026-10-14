package org.doit.sistticket.category.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="performances")
public class Performance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "performance_id", nullable=false)
	private long performanceId;
	
	//@ManyToOne(targetEntity = Concert)
	@Column(name = "concert_id", nullable=false)
	private long concertId;
	
	@Column(name = "performance_date")
	private LocalDateTime performanceDate;
	
	@Column(name = "booking_start")
	private LocalDateTime bookingStart;
	
	@Column(name = "booking_end")
	private LocalDateTime bookingEnd;
	
	@Column(name = "status")
	private String status;
	

}
