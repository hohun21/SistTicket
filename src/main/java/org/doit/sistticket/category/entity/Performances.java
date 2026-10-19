package org.doit.sistticket.category.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "performances")
@Getter
@Setter
public class Performances {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "performance_id", nullable=false)
	private long performanceId;
	
	//performances - concerts
    @ManyToOne
    @JoinColumn(name = "concert_id", nullable=false)
    private Concert concert;
	
	@Column(name = "performance_date")
	private LocalDateTime performanceDate;
	
	@Column(name = "booking_start")
	private LocalDateTime bookingStart;
	
	@Column(name = "booking_end")
	private LocalDateTime bookingEnd;
	
	@Column(name = "status")
	private String status;
	

}