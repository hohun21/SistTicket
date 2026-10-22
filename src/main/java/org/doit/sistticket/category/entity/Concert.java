package org.doit.sistticket.category.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.doit.sistticket.venue.entity.Venue;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    @JoinColumn(name = "venue_id", nullable=true)
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.SET_NULL) // 장소가 삭제되면 FK를 NULL로 설정
    private Venue venue;
	
	@Column(name = "start_date")
	private LocalDateTime startDate;
	
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@Column(name = "concert_name", nullable=false)
	private String concertName;
	
	@Column(name = "run_time")
	private Duration runTime;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConcertStatus status;
	
	//concerts - genre
	@ManyToOne
	@JoinColumn(name = "genre_id", nullable=false)
	private Genre genre;	
	

}
