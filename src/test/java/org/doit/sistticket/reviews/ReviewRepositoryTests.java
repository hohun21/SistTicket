package org.doit.sistticket.reviews;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.member.entity.Members;
import org.doit.sistticket.review.entity.Reviews;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

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
		testMember = new Members();
		testEntityManager.persist(testMember);
		testConcert = new Concert();
		testEntityManager.persist(testConcert);
		
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
		
		testEntityManager.flush();
		testEntityManager.clear();
	}
	

}
