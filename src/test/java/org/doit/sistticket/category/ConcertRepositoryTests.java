package org.doit.sistticket.category;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.Genre;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.GenreRepository;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConcertRepositoryTests {

	@Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private GenreRepository genreRepository;
//
    /*
    @Test
    void insertConcertList() {
        List<Genre> genres = genreRepository.findAll();
        List<Venue> venues = venueRepository.findAll();

        if (genres.isEmpty() || venues.isEmpty()) {
            throw new IllegalStateException("Genre 또는 Venue 데이터가 먼저 입력되어 있어야 합니다!");
        }

        List<Concert> concertList = List.of(
            // 1. 아이돌 (genre_id: 1)
            Concert.builder()
                .concertName("아이유 단독 콘서트 - The Winning [서울]")
                .startDate(LocalDateTime.of(2026, 11, 5, 18, 0))
                .endDate(LocalDateTime.of(2026, 11, 5, 21, 0))
                .runTime(Duration.ofMinutes(180))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(0))
                .genre(genres.get(0))
                .build(),
            Concert.builder()
                .concertName("아이유 단독 콘서트 - The Winning [부산]")
                .startDate(LocalDateTime.of(2026, 11, 12, 18, 0))
                .endDate(LocalDateTime.of(2026, 11, 12, 21, 0))
                .runTime(Duration.ofMinutes(180))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(3))
                .genre(genres.get(0))
                .build(),

            // 2. 발라드/R&B (genre_id: 2)
            Concert.builder()
                .concertName("성시경의 축가 콘서트 [서울]")
                .startDate(LocalDateTime.of(2026, 10, 1, 19, 0))
                .endDate(LocalDateTime.of(2026, 10, 30, 21, 30))
                .runTime(Duration.ofMinutes(150))
                .status(ConcertStatus.ONGOING) // 기존 0L -> ONGOING
                .venue(venues.get(1))
                .genre(genres.get(1))
                .build(),
            Concert.builder()
                .concertName("박효신 콘서트 - SOUL TREE [서울]")
                .startDate(LocalDateTime.of(2026, 12, 10, 19, 0))
                .endDate(LocalDateTime.of(2026, 12, 12, 22, 0))
                .runTime(Duration.ofMinutes(180))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(0))
                .genre(genres.get(1))
                .build(),

            // 3. 힙합/EDM (genre_id: 3)
            Concert.builder()
                .concertName("서울 힙합 페스티벌 파티 [서울]")
                .startDate(LocalDateTime.of(2026, 11, 25, 17, 0))
                .endDate(LocalDateTime.of(2026, 11, 25, 22, 0))
                .runTime(Duration.ofMinutes(300))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(2))
                .genre(genres.get(2))
                .build(),
            Concert.builder()
                .concertName("부산 클럽 EDM 라이브 [부산]")
                .startDate(LocalDateTime.of(2026, 8, 2, 20, 0))
                .endDate(LocalDateTime.of(2026, 8, 3, 2, 0))
                .runTime(Duration.ofMinutes(360))
                .status(ConcertStatus.FINISHED) // 기존 -1L -> FINISHED
                .venue(venues.get(4))
                .genre(genres.get(2))
                .build(),

            // 4. 페스티벌 (genre_id: 4)
            Concert.builder()
                .concertName("한여름 밤의 뮤직 페스티벌 [대구]")
                .startDate(LocalDateTime.of(2026, 8, 15, 14, 0))
                .endDate(LocalDateTime.of(2026, 8, 17, 22, 0))
                .runTime(Duration.ofMinutes(480))
                .status(ConcertStatus.FINISHED) // 기존 -1L -> FINISHED
                .venue(venues.get(5))
                .genre(genres.get(3))
                .build(),

            // 5. 인디/록 (genre_id: 5)
            Concert.builder()
                .concertName("인디 밴드 봄날의 콘서트 [인천]")
                .startDate(LocalDateTime.of(2026, 11, 30, 18, 0))
                .endDate(LocalDateTime.of(2026, 11, 30, 20, 30))
                .runTime(Duration.ofMinutes(150))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(6))
                .genre(genres.get(4))
                .build(),
            Concert.builder()
                .concertName("홍대 인디 락 페스티벌 라이브 [경기]")
                .startDate(LocalDateTime.of(2026, 12, 5, 17, 0))
                .endDate(LocalDateTime.of(2026, 12, 5, 21, 0))
                .runTime(Duration.ofMinutes(240))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(9))
                .genre(genres.get(4))
                .build(),

            // 6. 내한 (genre_id: 6)
            Concert.builder()
                .concertName("브루노 마스 내한 공연 [서울]")
                .startDate(LocalDateTime.of(2026, 11, 15, 20, 0))
                .endDate(LocalDateTime.of(2026, 11, 15, 22, 30))
                .runTime(Duration.ofMinutes(150))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(0))
                .genre(genres.get(5))
                .build(),
            Concert.builder()
                .concertName("에드 시런 월드투어 내한 [인천]")
                .startDate(LocalDateTime.of(2026, 12, 20, 19, 30))
                .endDate(LocalDateTime.of(2026, 12, 20, 22, 0))
                .runTime(Duration.ofMinutes(150))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(6))
                .genre(genres.get(5))
                .build(),

            // 7. 뮤지컬 (genre_id: 7)
            Concert.builder()
                .concertName("뮤지컬 <레미제라블> [서울]")
                .startDate(LocalDateTime.of(2026, 10, 1, 14, 0))
                .endDate(LocalDateTime.of(2026, 10, 31, 17, 0))
                .runTime(Duration.ofMinutes(170))
                .status(ConcertStatus.ONGOING) // 기존 0L -> ONGOING
                .venue(venues.get(1))
                .genre(genres.get(6))
                .build(),
            Concert.builder()
                .concertName("뮤지컬 <오페라의 유령> [대구]")
                .startDate(LocalDateTime.of(2026, 11, 1, 15, 0))
                .endDate(LocalDateTime.of(2026, 11, 30, 18, 0))
                .runTime(Duration.ofMinutes(160))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(5))
                .genre(genres.get(6))
                .build(),

            // 8. 연극 (genre_id: 8)
            Concert.builder()
                .concertName("대학로 인기 연극 <택시 시뮬레이터> [대전]")
                .startDate(LocalDateTime.of(2026, 10, 10, 15, 0))
                .endDate(LocalDateTime.of(2026, 10, 25, 18, 0))
                .runTime(Duration.ofMinutes(100))
                .status(ConcertStatus.ONGOING) // 기존 0L -> ONGOING
                .venue(venues.get(8))
                .genre(genres.get(7))
                .build(),
            Concert.builder()
                .concertName("앵콜 연극 <라이어> [광주]")
                .startDate(LocalDateTime.of(2026, 11, 1, 16, 0))
                .endDate(LocalDateTime.of(2026, 11, 15, 19, 0))
                .runTime(Duration.ofMinutes(110))
                .status(ConcertStatus.UPCOMING) // 기존 1L -> UPCOMING
                .venue(venues.get(7))
                .genre(genres.get(7))
                .build()
        );

        concertRepository.saveAll(concertList);
    }
    */
}
