package org.doit.sistticket.venue.service;

import java.util.List;

import org.doit.sistticket.venue.domain.VenueDTO;
import org.doit.sistticket.venue.domain.VenueRequestDTO;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
    public void deleteVenue(Long id) {
        venueRepository.deleteById(id);
    }
    
    
    @Override
    @Transactional
    public void saveVenue(VenueRequestDTO dto) {
        Venue venue = Venue.builder()
                .venueName(dto.getVenueName())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .capacity(dto.getCapacity() != null ? dto.getCapacity() : 1000) // 기본값 처리 등 안전장치
                .build();
        venueRepository.save(venue);
    }

    @Override
    @Transactional
    public void updateVenue(Long venueId, VenueRequestDTO dto) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다. ID: " + venueId));
        
        venue.setVenueName(dto.getVenueName());
        venue.setAddress(dto.getAddress());
        venue.setDetailAddress(dto.getDetailAddress());
        venue.setCapacity(dto.getCapacity());
    }



    @Override
    public Page<Venue> getVenueListbyKeyword(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return venueRepository.findByVenueNameContaining(keyword, pageable);
        }
        return venueRepository.findAll(pageable);
    }
}