package org.doit.sistticket.venue.service;

import java.util.List;

import org.doit.sistticket.venue.domain.VenueDTO;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {
    private final VenueRepository venueRepository;

    @Override
    public List<Venue> getVenueList() {
        return venueRepository.findAll();
    }
    
    @Override
    public void saveVenue(VenueDTO dto) {
        Venue venue = Venue.builder()
                .venueName(dto.getVenueName())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress()) 
                .capacity(dto.getCapacity())           
                .build();
        venueRepository.save(venue);
    }
    @Override
    public void deleteVenue(Long id) {
        venueRepository.deleteById(id);
    }
}