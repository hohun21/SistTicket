package org.doit.sistticket.category;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.doit.sistticket.category.entity.PerformanceStatus;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PerformanceRepositoryTests {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private PerformancesRepository performanceRepository;
    /*
    @Test
    void insertPerformancesList() {
        List<Concert> concerts = concertRepository.findAll();

        if (concerts.isEmpty()) {
            throw new IllegalStateException("Concert 데이터가 먼저 존재해야 합니다!");
        }

        List<Performances> performanceList = new ArrayList<>();
        
        // [수정] 고정 날짜 대신 현재 시스템 시각을 기준으로 상태 판별
        LocalDateTime now = LocalDateTime.now();

        for (Concert concert : concerts) {
            LocalDateTime concertStart = concert.getStartDate();
            LocalDateTime concertEnd = concert.getEndDate();

            // 1. 콘서트 기간(일수) 계산
            long totalDays = ChronoUnit.DAYS.between(concertStart.toLocalDate(), concertEnd.toLocalDate());

            // 2. 콘서트 기간에 맞춰 1~3개의 회차 수 동적 결정
            int performanceCount;
            if (totalDays >= 7) {
                performanceCount = 3; // 장기 공연/뮤지컬은 3회차
            } else if (totalDays >= 1) {
                performanceCount = 2; // 1~2일짜리 공연은 2회차
            } else {
                performanceCount = 1; // 당일치기 콘은 1회차
            }

            // 3. 회차별 일정 및 상세 정보 생성
            for (int i = 0; i < performanceCount; i++) {
                LocalDateTime perfDate;

                if (performanceCount == 1) {
                    perfDate = concertStart;
                } else if (performanceCount == 2) {
                    perfDate = (i == 0) ? concertStart : concertStart.plusDays(1);
                } else {
                    // 3개인 경우 시작일, 중간, 종료일에 걸쳐 배치
                    perfDate = concertStart.plusDays(i * (totalDays / 2));
                    if (perfDate.isAfter(concertEnd)) {
                        perfDate = concertEnd;
                    }
                }

                // 예매 시작: 공연일 기준 30일 전
                LocalDateTime bookingStart = perfDate.minusDays(30);
                
                // 예매 마감: 공연 시작 2시간 전
                LocalDateTime bookingEnd = perfDate.minusHours(2);

                // 현재 시각과 비교하여 상태(Status) 자동 판별
                PerformanceStatus status;
                if (perfDate.isBefore(now)) {
                    status = PerformanceStatus.FINISHED;
                } else if (perfDate.toLocalDate().equals(now.toLocalDate())) {
                    status = PerformanceStatus.ONGOING;
                } else {
                    status = PerformanceStatus.UPCOMING;
                }

                Performances performance = Performances.builder()
                        .concert(concert)
                        .performanceDate(perfDate)
                        .bookingStart(bookingStart)
                        .bookingEnd(bookingEnd)
                        .status(status)
                        .build();

                performanceList.add(performance);
            }
        }

        performanceRepository.saveAll(performanceList);
    }
    */
}