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
	//장르 DB 추가 (콘서트장르)
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
	
	/*
	@Test
	//장르 DB추가(뮤지컬/연극 장르)
	void insertlist2(){
		String[] genrelist_musical = {"뮤지컬","연극"};
		IntStream.rangeClosed(1, 2)
	      .forEach(i->{
	         Genre genre = Genre.builder()
	               .genreName(genrelist_musical[i-1])
	               .build();
	         this.genreRepository.save(genre);
	      });
	}
	
	*/
}
