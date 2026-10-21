package org.doit.sistticket.venue.service;

import java.util.List;

import org.doit.sistticket.venue.domain.VenueDTO;
import org.doit.sistticket.venue.domain.VenueRequestDTO;
import org.doit.sistticket.venue.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VenueService {

	List<Venue> getVenueList();

	void deleteVenue(Long id);

	Page<Venue> getVenueListbyKeyword(String keyword, Pageable pageable);

	void updateVenue(Long venueId, VenueRequestDTO dto);

	void saveVenue(VenueRequestDTO dto);
	
	

}
