package org.doit.sistticket.category;

import java.util.List;

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
	void insertVenueList() {
	    List<Venue> venueList = List.of(
	        // 서울 (3곳)
	        Venue.builder().venueName("KSPO DOME (체조경기장)").address("서울특별시 송파구").detailAddress("올림픽공원 내").capacity(15000L).build(),
	        Venue.builder().venueName("세종문화회관 대극장").address("서울특별시 종로구").detailAddress("세종문화회관 본관").capacity(3022L).build(),
	        Venue.builder().venueName("올림픽공원 올림픽홀").address("서울특별시 송파구").detailAddress("올림픽공원 내").capacity(3000L).build(),
	        
	        // 부산 (2곳)
	        Venue.builder().venueName("부산 벡스코 오디토리움").address("부산광역시 해운대구").detailAddress("벡스코 전시장 내").capacity(4000L).build(),
	        Venue.builder().venueName("영화의전당 하늘연극장").address("부산광역시 해운대구").detailAddress("센텀시티 내").capacity(841L).build(),
	        
	        // 대구 (1곳)
	        Venue.builder().venueName("대구 오페라하우스").address("대구광역시 북구").detailAddress("오페라하우스 대공연장").capacity(1620L).build(),
	        
	        // 인천 (1곳)
	        Venue.builder().venueName("인천문화예술회관").address("인천광역시 남동구").detailAddress("대공연장").capacity(1332L).build(),
	        
	        // 광주 (1곳)
	        Venue.builder().venueName("광주예술의전당").address("광주광역시 북구").detailAddress("대극장").capacity(1722L).build(),
	        
	        // 대전 (1곳)
	        Venue.builder().venueName("대전예술의전당").address("대전광역시 서구").detailAddress("아트홀").capacity(1546L).build(),
	        
	        // 경기 (2곳)
	        Venue.builder().venueName("경기아트센터").address("경기도 수원시").detailAddress("대극장").capacity(1542L).build(),
	        Venue.builder().venueName("고양아람누리 아람극장").address("경기도 고양시").detailAddress("일산동구").capacity(1887L).build()
	    );
	
	    this.venueRepository.saveAll(venueList);
	}
	*/
}
