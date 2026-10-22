package org.doit.sistticket.venue.repository;

import org.doit.sistticket.venue.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue,Long>{

	Page<Venue> findByVenueNameContaining(String keyword, Pageable pageable);

}
