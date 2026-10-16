package org.doit.sistticket.category;

import java.util.stream.IntStream;

import org.doit.sistticket.category.entity.Genre;
import org.doit.sistticket.category.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GenreRepositoryTests {

	@Autowired
	private GenreRepository genreRepository;
	
	/*
	//장르 DB 추가
	@Test
	void insertlist() {
		String[] genrelist = {"아이돌", "발라드/R&B", "힙합/EDM", "페스티벌", "인디/록", "내한"};
		
	      IntStream.rangeClosed(1, 6)
	      .forEach(i->{
	         Genre genre = Genre.builder()
	               .genreName(genrelist[i-1])
	               .build();
	         this.genreRepository.save(genre);
	         
	         
	      });
	}
	*/
}
