package org.doit.sistticket.mainpage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.ConcertImg;
import org.doit.sistticket.category.repository.ConcertImgRepository;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.mainpage.dto.MainConcertDTO;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final ConcertRepository concertRepository;
    
    private final ConcertImgRepository concertImgRepository;

	    @Override
	    public List<MainConcertDTO> getLatestConcerts() {
	        List<Concert> concerts = concertRepository.findTop4ByOrderByConcertIdDesc();
	
	        return concerts.stream().map(concert -> {
	            
	
	            String posterUrl = concertImgRepository
	                    .findByConcert_ConcertIdAndIsThumbnail(concert.getConcertId(), true)
	                    .map(ConcertImg::getImgUrl) 
	                    .orElse("/images/sample1.jpg");
	
	            String venueName = (concert.getVenue() != null) ? concert.getVenue().getVenueName() : "장소 미정";
	            String category = (concert.getGenre() != null) ? concert.getGenre().getGenreName() : "기타";
	            
	            return MainConcertDTO.builder()
	                    .concertId(concert.getConcertId())
	                    .title(concert.getConcertName())
	                    .posterUrl(posterUrl) 
	                    .category(category)
	                    .venueName(venueName)
	                    .startDate(concert.getStartDate().toString()) 
	                    .endDate(concert.getEndDate().toString())     
	                    .build();
	        }).collect(Collectors.toList());
	    }
}
