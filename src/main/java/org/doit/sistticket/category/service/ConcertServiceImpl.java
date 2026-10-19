package org.doit.sistticket.category.service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.ConcertImg;
import org.doit.sistticket.category.entity.ConcertStatus;
import org.doit.sistticket.category.entity.Genre;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.repository.ConcertImgRepository;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.GenreRepository;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ConcertServiceImpl implements ConcertService {

    @Autowired
    private ConcertRepository concertRepository;
    

    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private GenreRepository genreRepository;
    
    
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
    
    @Override
    @Transactional
    public Long registerConcert(ConcertRequestDTO dto) {
        // 1. 장소 및 장르 조회
        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소 ID입니다."));
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장르 ID입니다."));

        Duration runTime = dto.getRunTimeMinutes() != null ? 
                Duration.ofMinutes(dto.getRunTimeMinutes()) : null;

        // 2. Concert 저장
        Concert concert = Concert.builder()
                .concertName(dto.getConcertName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .venue(venue)
                .genre(genre)
                .status(dto.getStatus() != null ? dto.getStatus() : ConcertStatus.UPCOMING)
                .runTime(runTime)
                .build();

        Concert savedConcert = concertRepository.save(concert);

        // 3. 대표 썸네일 저장 (isThumbnail = true)
        if (dto.getThumbnailUrl() != null && !dto.getThumbnailUrl().isBlank()) {
            ConcertImg thumbnail = ConcertImg.builder()
                    .concert(savedConcert)
                    .imgUrl(dto.getThumbnailUrl())
                    .isThumbnail(true)
                    .imgOrder(0) // 썸네일은 순서 0 또는 1
                    .build();
            concertImgRepository.save(thumbnail);
        }

        // 4.다중 상세 이미지 저장 (isThumbnail = false, 반복문 처리)
        if (dto.getDetailImgUrls() != null && !dto.getDetailImgUrls().isEmpty()) {
            int order = 1;
            for (String imgUrl : dto.getDetailImgUrls()) {
                if (imgUrl != null && !imgUrl.isBlank()) {
                    ConcertImg detailImg = ConcertImg.builder()
                            .concert(savedConcert)
                            .imgUrl(imgUrl)
                            .isThumbnail(false)
                            .imgOrder(order++) // 순차적으로 증가
                            .build();
                    concertImgRepository.save(detailImg);
                }
            }
        }

        return savedConcert.getConcertId();
    }

	@Override
	public void updateConcert(Long concertId, ConcertRequestDTO dto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteConcert(Long concertId) {
		// TODO Auto-generated method stub
		
	}
    

}