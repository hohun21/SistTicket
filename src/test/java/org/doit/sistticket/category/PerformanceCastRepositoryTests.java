package org.doit.sistticket.category;

import java.util.ArrayList;
import java.util.List;

import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.repository.ActorRepository;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.entity.Role;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.category.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PerformanceCastRepositoryTests {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private PerformancesRepository performanceRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private PerformanceCastRepository performanceCastRepository;
    /*
    @Test
    void insertPerformanceCastList() {
        // 1. 기초 데이터 전체 조회
        List<Concert> concerts = concertRepository.findAll();
        List<Performances> performances = performanceRepository.findAll();
        List<Role> roles = roleRepository.findAll();
        List<Actor> actors = actorRepository.findAll();

        if (concerts.isEmpty() || performances.isEmpty() || roles.isEmpty() || actors.isEmpty()) {
            throw new IllegalStateException("Concert, Performance, Role, Actor 선행 데이터가 모두 존재해야 합니다!");
        }

        List<PerformanceCast> castList = new ArrayList<>();

        // 2. 각 콘서트별 회차 및 역할, 배우 매핑
        for (Concert concert : concerts) {
            String concertName = concert.getConcertName();

            // 해당 콘서트에 속한 회차들과 역할들 필터링
            List<Performances> concertPerfs = performances.stream()
                    .filter(p -> p.getConcert().getConcertId() == concert.getConcertId())
                    .toList();

            List<Role> concertRoles = roles.stream()
                    .filter(r -> r.getConcert().getConcertId() == concert.getConcertId())
                    .toList();

            if (concertPerfs.isEmpty() || concertRoles.isEmpty()) continue;

            // --- 2-1. 뮤지컬 / 연극 (더블 캐스팅 매핑) ---
            if (concertName.contains("뮤지컬") || concertName.contains("연극")) {
                Role mainRole = concertRoles.stream().filter(r -> r.getRoleName().matches("장발장|팬텀|택시기사|존 스미스")).findFirst().orElse(concertRoles.get(0));
                Role subRole = concertRoles.stream().filter(r -> r.getRoleName().matches("자베르|크리스틴 다에|멀티맨|스탠리 포니")).findFirst().orElse(concertRoles.size() > 1 ? concertRoles.get(1) : concertRoles.get(0));

                // 역할별 배우 선정 (더블 캐스팅용 후보군)
                Actor mainActor1 = findActorByNameOrDefault(actors, concertName.contains("레미제라블") ? "박은태" : "최재림", actors.get(0));
                Actor mainActor2 = findActorByNameOrDefault(actors, concertName.contains("레미제라블") ? "카이" : "전동석", actors.size() > 1 ? actors.get(1) : actors.get(0));
                
                Actor subActor1 = findActorByNameOrDefault(actors, "옥주현", actors.size() > 2 ? actors.get(2) : actors.get(0));
                Actor subActor2 = findActorByNameOrDefault(actors, "김소현", actors.size() > 3 ? actors.get(3) : actors.get(0));

                // 회차별로 다르게 캐스팅 매핑
                for (int i = 0; i < concertPerfs.size(); i++) {
                    Performances perf = concertPerfs.get(i);

                    // 1회차 vs 2, 3회차 배우를 교차로 지정 (더블 캐스팅 효과)
                    Actor currentMain = (i % 2 == 0) ? mainActor1 : mainActor2;
                    Actor currentSub = (i % 2 == 0) ? subActor1 : subActor2;

                    castList.add(PerformanceCast.builder().performance(perf).role(mainRole).actor(currentMain).build());
                    castList.add(PerformanceCast.builder().performance(perf).role(subRole).actor(currentSub).build());
                }

            } 
            // --- 2-2. 일반 단독 콘서트 / 페스티벌 / 내한 공연 ---
            else {
                Role artistRole = concertRoles.get(0);
                Actor matchedActor;

                if (concertName.contains("아이유")) matchedActor = findActorByNameOrDefault(actors, "아이유", actors.get(0));
                else if (concertName.contains("성시경")) matchedActor = findActorByNameOrDefault(actors, "성시경", actors.get(0));
                else if (concertName.contains("박효신")) matchedActor = findActorByNameOrDefault(actors, "박효신", actors.get(0));
                else if (concertName.contains("브루노 마스")) matchedActor = findActorByNameOrDefault(actors, "브루노 마스", actors.get(0));
                else if (concertName.contains("에드 시런")) matchedActor = findActorByNameOrDefault(actors, "에드 시런", actors.get(0));
                else matchedActor = actors.get((int) (concert.getConcertId() % actors.size()));

                // 해당 콘서트의 모든 회차에 동일 아티스트 매핑
                for (Performances perf : concertPerfs) {
                    castList.add(PerformanceCast.builder()
                            .performance(perf)
                            .role(artistRole)
                            .actor(matchedActor)
                            .build());
                }
            }
        }

        // 3. 최종 캐스팅 데이터 일괄 저장
        performanceCastRepository.saveAll(castList);
    }

    private Actor findActorByNameOrDefault(List<Actor> actors, String name, Actor defaultActor) {
        return actors.stream()
                .filter(a -> a.getActorName().contains(name))
                .findFirst()
                .orElse(defaultActor);
    }
    */
}