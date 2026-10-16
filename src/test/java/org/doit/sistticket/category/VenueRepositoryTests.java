package org.doit.sistticket.category;

import java.util.stream.IntStream;

import org.doit.sistticket.category.entity.Genre;
import org.doit.sistticket.category.repository.GenreRepository;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VenueRepositoryTests {

	@Autowired
	private VenueRepository venueRepository;
	
	/*
	//장소 DB 추가
	@Test
	void insertlist() {
		String[] genrelist = {"아이돌", "발라드/R&B", "힙합/EDM", "페스티벌", "인디/록", "내한"};
		
	      IntStream.rangeClosed(1, 6)
	      .forEach(i->{
	         Venue venue = Venue.builder()
	        		 .venueName(null)
	        		 //.address()
	        		 .detailAddress("빌딩")
	               .build();
	         this.venueRepository.save(venue);
	         
	      });
	}
	*/
}
