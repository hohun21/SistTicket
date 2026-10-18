package org.doit.sistticket.category;

import java.util.ArrayList;
import java.util.List;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.Role;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RoleRepositoryTests {
	
	@Autowired
	private ConcertRepository concertRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	/*
	//역할DB 추가
	@Test
    void insertRoleList() {
        List<Concert> concerts = concertRepository.findAll();

        if (concerts.isEmpty()) {
            throw new IllegalStateException("Concert 데이터가 먼저 존재해야 합니다!");
        }

        List<Role> roleList = new ArrayList<>();

        for (Concert concert : concerts) {
            String name = concert.getConcertName();

            // 1. 아이유 단독 콘서트 - The Winning [서울 / 부산]
            if (name.contains("아이유 단독 콘서트")) {
                roleList.add(Role.builder().concert(concert).roleName("메인 아티스트").description("아이유 단독 보컬 및 퍼포먼스").build());
            }
            // 2. 성시경의 축가 콘서트 [서울]
            else if (name.contains("성시경의 축가 콘서트")) {
                roleList.add(Role.builder().concert(concert).roleName("메인 보컬").description("성시경 감성 발라드 라이브 공연").build());
            }
            // 3. 박효신 콘서트 - SOUL TREE [서울]
            else if (name.contains("박효신 콘서트")) {
                roleList.add(Role.builder().concert(concert).roleName("메인 아티스트").description("박효신 소울 트리 단독 콘서트").build());
            }
            // 4. 서울 힙합 페스티벌 파티 [서울]
            else if (name.contains("서울 힙합 페스티벌")) {
                roleList.add(Role.builder().concert(concert).roleName("헤드라이너").description("힙합 페스티벌 메인 래퍼 크루").build());
                roleList.add(Role.builder().concert(concert).roleName("서브 래퍼").description("힙합 페스티벌 서브 아티스트").build());
            }
            // 5. 부산 클럽 EDM 라이브 [부산]
            else if (name.contains("부산 클럽 EDM 라이브")) {
                roleList.add(Role.builder().concert(concert).roleName("헤드디제이").description("EDM 파티 메인 DJ 라이브").build());
            }
            // 6. 한여름 밤의 뮤직 페스티벌 [대구]
            else if (name.contains("한여름 밤의 뮤직 페스티벌")) {
                roleList.add(Role.builder().concert(concert).roleName("페스티벌 아티스트").description("뮤직 페스티벌 초청 뮤지션").build());
            }
            // 7. 인디 밴드 봄날의 콘서트 [인천]
            else if (name.contains("인디 밴드 봄날의 콘서트")) {
                roleList.add(Role.builder().concert(concert).roleName("보컬/기타").description("인디 밴드 메인 프론트맨").build());
            }
            // 8. 홍대 인디 락 페스티벌 라이브 [경기]
            else if (name.contains("홍대 인디 락 페스티벌")) {
                roleList.add(Role.builder().concert(concert).roleName("락 밴드 보컬").description("인디 락 페스티벌 라이브 밴드").build());
            }
            // 9. 브루노 마스 내한 공연 [서울]
            else if (name.contains("브루노 마스")) {
                roleList.add(Role.builder().concert(concert).roleName("메인 아티스트").description("브루노 마스 내한 단독 공연").build());
            }
            // 10. 에드 시런 월드투어 내한 [인천]
            else if (name.contains("에드 시런")) {
                roleList.add(Role.builder().concert(concert).roleName("싱어송라이터").description("에드 시런 월드투어 라이브").build());
            }
            // 11. 뮤지컬 <레미제라블> [서울]
            else if (name.contains("레미제라블")) {
                roleList.add(Role.builder().concert(concert).roleName("장발장").description("뮤지컬 <레미제라블> 주인공 장발장 역").build());
                roleList.add(Role.builder().concert(concert).roleName("자베르").description("뮤지컬 <레미제라블> 집념의 경감 자베르 역").build());
            }
            // 12. 뮤지컬 <오페라의 유령> [대구]
            else if (name.contains("오페라의 유령")) {
                roleList.add(Role.builder().concert(concert).roleName("팬텀").description("뮤지컬 <오페라의 유령> 유령 역").build());
                roleList.add(Role.builder().concert(concert).roleName("크리스틴 다에").description("뮤지컬 <오페라의 유령> 천상의 목소리 크리스틴 역").build());
            }
            // 13. 대학로 인기 연극 <택시 시뮬레이터> [대전]
            else if (name.contains("택시 시뮬레이터")) {
                roleList.add(Role.builder().concert(concert).roleName("택시기사").description("연극 <택시 시뮬레이터> 주인공 기사 역").build());
                roleList.add(Role.builder().concert(concert).roleName("멀티맨").description("연극 <택시 시뮬레이터> 멀티 조연 역").build());
            }
            // 14. 앵콜 연극 <라이어> [광주]
            else if (name.contains("라이어")) {
                roleList.add(Role.builder().concert(concert).roleName("존 스미스").description("연극 <라이어> 주인공 존 스미스 역").build());
                roleList.add(Role.builder().concert(concert).roleName("스탠리 포니").description("연극 <라이어> 친구 스탠리 역").build());
            }
            // 기본 예외 처리용
            else {
                roleList.add(Role.builder().concert(concert).roleName("메인 퍼포머").description(name + " 출연진").build());
            }
        }

        roleRepository.saveAll(roleList);
    }
	*/
}

