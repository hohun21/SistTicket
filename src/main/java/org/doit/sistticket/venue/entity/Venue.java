package org.doit.sistticket.venue.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="venues")
public class Venue {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name = "venue_id", nullable=false)
	private long venueId;
	
	@Column(name = "venue_name", nullable=false)
	private String venueName;
	
	@Column(name = "address", nullable=false)
	private String address;
	
	@Column(name = "detail_address")
	private String detailAddress;
	
	@Column(name = "capacity")
	private long capacity;
	
	
}
