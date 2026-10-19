package org.doit.sistticket.venue.service;

import java.util.List;

import org.doit.sistticket.venue.domain.VenueDTO;
import org.doit.sistticket.venue.entity.Venue;

public interface VenueService {

	List<Venue> getVenueList();

	void deleteVenue(Long id);

	void saveVenue(VenueDTO dto);

}
