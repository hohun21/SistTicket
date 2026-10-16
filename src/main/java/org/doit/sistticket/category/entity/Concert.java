package org.doit.sistticket.category.entity;

import java.time.Duration;
import java.time.LocalDateTime;

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
@Table(name="concerts")

public class Concert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "concert_id", nullable=false)
	private long concertId;
	
	//concerts - venues
	@ManyToOne
    @JoinColumn(name = "venue_id", nullable=false)
    private Venue venue;
	
	@Column(name = "start_date")
	private LocalDateTime startDate;
	
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@Column(name = "concert_name", nullable=false)
	private String concertName;
	
	@Column(name = "run_time")
	private Duration runTime;
	
	@Column(name = "status")
	private String status;
	
	//concerts - genre
	@ManyToOne
	@JoinColumn(name = "genre_id", nullable=false)
	private Genre genre;	
	

}
