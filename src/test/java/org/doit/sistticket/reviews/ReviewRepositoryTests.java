package org.doit.sistticket.reviews;



import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.review.entity.Reviews;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@Disabled
@DataJpaTest
class ReviewRepositoryTests {

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private TestEntityManager testEntityManager;
	
	private Members testMember;
	private Concert testConcert;
	
	@BeforeEach
	void setUp() {
		// 1. Venue (공연장) 필수값 세팅
		org.doit.sistticket.venue.entity.Venue testVenue = new org.doit.sistticket.venue.entity.Venue();
		testVenue.setVenueName("테스트 공연장");
		testVenue.setAddress("서울시 강남구");
		testVenue.setCapacity(1000L);
		testEntityManager.persist(testVenue);
		
		// 2. Genre (장르) 필수값 세팅
		org.doit.sistticket.category.entity.Genre testGenre = new org.doit.sistticket.category.entity.Genre();
		testGenre.setGenreName("발라드");
		testEntityManager.persist(testGenre);

		// 3. Members (회원) 필수값 세팅
		testMember = new Members();
		testMember.setEmail("test@test.com");
		testMember.setPassword("1234");
		testMember.setName("테스터");
		testMember.setPhone("010-1234-5678");
		testEntityManager.persist(testMember);
		
		// 4. Concert (공연) 필수값 세팅 (위에서 만든 공연장과 장르 조립)
		testConcert = new Concert();
		testConcert.setConcertName("테스트 콘서트");
		testConcert.setVenue(testVenue);
		testConcert.setGenre(testGenre);
		testEntityManager.persist(testConcert);
		
		// 5. Reviews 세팅 (기존 코드와 동일)
		Reviews review1 = Reviews.builder()
				.member(testMember)
				.concert(testConcert)
				.rating(5)
				.text("첫 번째 리뷰입니다.")
				.build();
		
		Reviews review2 = Reviews.builder()
				.member(testMember)
				.concert(testConcert)
				.rating(4)
				.text("두 번째 리뷰입니다.")
				.build();
		
		testEntityManager.persist(review1);
		testEntityManager.persist(review2);
		
		// 6. DB에 강제 반영 후 기억 지우기 (캐시 비우기)
		testEntityManager.flush();
		testEntityManager.clear();
	}
	
	@Test
	void findByConcert_ConcertIdTests() {
		Long targetConcertId = testConcert.getConcertId();
		
		List<Reviews> result = reviewRepository.findByConcert_ConcertId(targetConcertId);
		
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(2);
		assertThat(result.get(0).getText()).isEqualTo("첫 번째 리뷰입니다.");
		
	}

}
