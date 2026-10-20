package org.doit.sistticket.category.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.doit.sistticket.venue.entity.Venue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name="performances")

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
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PerformanceStatus status;
	

}