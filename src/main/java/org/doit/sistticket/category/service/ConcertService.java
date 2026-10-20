package org.doit.sistticket.category.service;

import java.util.List;
import java.util.stream.Collectors;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.dto.ConcertPageRequestDTO;
import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ConcertService {

	// (2-1) Entity + 썸네일 -> 목록용 DTO 변환 (default)
    default ConcertListResponseDTO entityToListDto(Concert concert, String imgUrl) {
        return ConcertListResponseDTO.builder()
                .concertId(concert.getConcertId())
                .concertName(concert.getConcertName())
                .startDate(concert.getStartDate())
                .endDate(concert.getEndDate())
                .status(concert.getStatus())
                .genreName(concert.getGenre() != null ? concert.getGenre().getGenreName() : "그외 장르")
                .venueName(concert.getVenue() != null ? concert.getVenue().getVenueName() : "공연장 미정")
                .imgUrl(imgUrl) // 
                .build();
    }

    // (2-2) Entity -> 상세용 DTO 변환 (default) - 회차 + 캐스팅 정보
    default ConcertDetailResponseDTO entityToDetailDto(
            Concert concert, 
            List<Performances> performances, 
            List<PerformanceCast> casts, String imgUrl) {
        
        List<ConcertDetailResponseDTO.PerformanceInfoItem> perfItems = performances.stream()
                .map(perf -> {
                    List<ConcertDetailResponseDTO.CastInfoItem> castItems = casts.stream()
                            .filter(c -> c.getPerformance().getPerformanceId() == perf.getPerformanceId())
                            .map(c -> ConcertDetailResponseDTO.CastInfoItem.builder()
                                    .roleName(c.getRole().getRoleName())
                                    .actorName(c.getActor().getActorName())
                                    .build())
                            .collect(Collectors.toList());

                    return ConcertDetailResponseDTO.PerformanceInfoItem.builder()
                            .performanceId(perf.getPerformanceId())
                            .performanceDate(perf.getPerformanceDate())
                            .bookingStart(perf.getBookingStart())
                            .bookingEnd(perf.getBookingEnd())
                            .status(perf.getStatus().name())
                            .casts(castItems)
                            .build();
                })
                .collect(Collectors.toList());

        return ConcertDetailResponseDTO.builder()
                .concertId(concert.getConcertId())
                .concertName(concert.getConcertName())
                .startDate(concert.getStartDate())
                .endDate(concert.getEndDate())
                .status(concert.getStatus())
                .genreName(concert.getGenre() != null ? concert.getGenre().getGenreName() : "일반")
                .venueName(concert.getVenue() != null ? concert.getVenue().getVenueName() : "공연장 미정")
                .venueAddress(concert.getVenue() != null ? concert.getVenue().getAddress() : "")
                .performanceList(perfItems)
                .runTimeMinutes(concert.getRunTime() != null ? (long) concert.getRunTime().toMinutes() : 0) //
                .imgUrl(imgUrl)
                .build();
    }


    // 상세 보기 (Concert ID로 회차 및 캐스팅 정보)
    ConcertDetailResponseDTO getDetail(Long concertId);

	Long registerConcert(ConcertRequestDTO dto);

	void updateConcert(Long concertId, ConcertRequestDTO dto, List<MultipartFile> imageFiles);

	void deleteConcert(Long concertId);


	Page<ConcertListResponseDTO> getConcertList(ConcertPageRequestDTO requestDTO);


	Long registerConcert(ConcertRequestDTO dto, List<MultipartFile> imageFiles);
}
