package org.doit.sistticket.actor;

import java.util.List;

import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.repository.ActorRepository;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ActorRepositoryTests {

	@Autowired
	private ActorRepository actorRepository;
	
	/*
	@Test
	//DB 배우 추가...
	void insertActorList() {
	    List<Actor> actorList = List.of(
	        Actor.builder().actorName("아이유").description("대한민국 대표 보컬리스트 겸 배우").profileUrl("dummy").build(),
	        Actor.builder().actorName("에드 시런").description("영국 출신의 세계적인 싱어송라이터").profileUrl("dummy").build(),
	        Actor.builder().actorName("성시경").description("감미로운 목소리의 발라드 가수").profileUrl("dummy").build(),
	        Actor.builder().actorName("조승우").description("무대와 스크린을 넘나드는 명품 배우").profileUrl("dummy").build(),
	        Actor.builder().actorName("옥주현").description("뮤지컬계의 독보적인 디바").profileUrl("dummy").build(),
	        Actor.builder().actorName("박효신").description("대체불가한 가창력의 명품 보컬").profileUrl("dummy").build(),
	        Actor.builder().actorName("카리나").description("트렌디한 매력의 글로벌 아티스트").profileUrl("dummy").build(),
	        Actor.builder().actorName("조정석").description("관객을 사로잡는 티켓파워 배우").profileUrl("dummy").build(),
	        Actor.builder().actorName("박은태").description("폭발적인 가창력과 섬세한 연기의 뮤지컬 배우").profileUrl("dummy").build(),
	        Actor.builder().actorName("브루노 마스").description("전 세계를 매료시킨 최고의 팝 싱어송라이터").profileUrl("dummy").build()
	    );

	    this.actorRepository.saveAll(actorList);
	}
	*/
}
