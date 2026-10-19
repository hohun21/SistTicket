package org.doit.sistticket.category.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.ConcertImg;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.repository.ConcertImgRepository;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConcertServiceImpl implements ConcertService {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertImgRepository concertImgRepository; // 썸네일 조회용

    @Autowired
    private PerformancesRepository performanceRepository; // 회차 조회용

    @Autowired
    private PerformanceCastRepository performanceCastRepository; // 캐스팅 조회용

    @Override
    public List<ConcertListResponseDTO> getList() {
        // 1. 모든 콘서트 조회
        List<Concert> concerts = concertRepository.findAll();

        // 2. 각 콘서트별 썸네일 URL을 조회하여 DTO로 변환
        return concerts.stream().map(concert -> {
            String thumbnailUrl = concertImgRepository
                    .findByConcert_ConcertIdAndIsThumbnail(concert.getConcertId(), true)
                    .map(ConcertImg::getImgUrl)
                    .orElse("/images/default.jpg"); // 이미지가 없을 경우 대체 경로

            return entityToListDto(concert, thumbnailUrl);
        }).collect(Collectors.toList());
    }

    @Override
    public ConcertDetailResponseDTO getDetail(Long concertId) {
        //특정 콘서트 단건 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트 ID입니다: " + concertId));

        // 해당 콘서트 ID에 속한 회차 목록만
        List<Performances> performances = performanceRepository.findByConcert_ConcertId(concertId);

        // 조회된 회차들의 ID만 추출
        List<Long> performanceIds = performances.stream()
                .map(Performances::getPerformanceId)
                .collect(Collectors.toList());

        // 해당 회차들에 속한 캐스팅 정보만 조회! (회차가 없다면 빈 리스트)
        List<PerformanceCast> casts = performanceIds.isEmpty() ? 
                List.of() : performanceCastRepository.findByPerformance_PerformanceIdIn(performanceIds);

        //인터페이스의 default 변환 메서드를 호출하여 상세 DTO 반환
        return entityToDetailDto(concert, performances, casts);
    }
}