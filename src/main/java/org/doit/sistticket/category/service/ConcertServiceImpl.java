package org.doit.sistticket.category.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.dto.ConcertPageRequestDTO;
import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.dto.ImageUploadDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public Page<ConcertListResponseDTO> getConcertList(ConcertPageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by(Sort.Direction.DESC, "concertId"));

        // @Query 단일 메서드로 장르 + type별(n, v, nv) 검색 한번에 처리
        Page<Concert> concertPage = concertRepository.searchConcerts(
                requestDTO.getGenreId(),
                requestDTO.getType(),
                requestDTO.getSearchKeyword(),
                pageable
        );

        return concertPage.map(concert -> ConcertListResponseDTO.builder()
                .concertId(concert.getConcertId())
                .concertName(concert.getConcertName())
                .genreName(concert.getGenre() != null ? concert.getGenre().getGenreName() : "기타")
                .venueName(concert.getVenue() != null ? concert.getVenue().getVenueName() : "장소 미정")
                .startDate(concert.getStartDate())
                .endDate(concert.getEndDate())
                .imgUrl("")
                .build());
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
    public Long registerConcert(ConcertRequestDTO dto) {
    	
    	// 1. ID로 Genre와 Venue 엔티티 조회 (없으면 예외 처리)
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장르입니다."));
                
        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        // 1. 콘서트 엔티티 저장
        Concert concert = Concert.builder()
                .concertName(dto.getConcertName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .runTime(Duration.ofMinutes(dto.getRunTimeMinutes()))
                .genre(genre) // 💡 장르 객체 연결
                .venue(venue) // 💡 장소 객체 연결
                .status(ConcertStatus.UPCOMING) // 💡 기본 상태값 지정해주기
                .build();

        Concert savedConcert = concertRepository.save(concert);

        // 2. 이미지 정보 저장 (UploadResultDTO 활용)
        if (dto.getImageDTOs() != null && !dto.getImageDTOs().isEmpty()) {
            int order = 0;
            for (ImageUploadDTO imgDto : dto.getImageDTOs()) {
                // 첫 번째 이미지를 썸네일(true)로 지정하거나 별도 구분 로직 적용 가능
                boolean isThumb = (order == 0); 

                ConcertImg concertImg = ConcertImg.builder()
                        .concert(savedConcert)
                        .uuid(imgDto.getUuid())
                        .imgName(imgDto.getFileName())
                        .path(imgDto.getFolderPath())
                        .isThumbnail(isThumb)
                        .imgOrder(order++)
                        .build();

                concertImgRepository.save(concertImg);
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
	private String makeFolder(String uploadFolder) {
	    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	    String folderPath = str.replace("/", File.separator);
	    
	    File uploadPath = new File(uploadFolder, folderPath);
	    if (!uploadPath.exists()) {
	        uploadPath.mkdirs();
	    }
	    return folderPath;
	}
	private ImageUploadDTO saveFile(MultipartFile multipartFile, String uploadFolder) {
	    String originalName = multipartFile.getOriginalFilename();
	    
	    // IE나 Edge 등 경로가 포함되어 들어오는 경우 파일명만 추출
	    String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
	    
	    String uuid = UUID.randomUUID().toString();
	    
	    // 날짜 폴더 생성 (예: 2026/06/01)
	    String folderPath = makeFolder(uploadFolder);
	    
	    String saveName = uuid + "_" + fileName;
	    Path savePath = Paths.get(uploadFolder, folderPath, saveName);
	    
	    try {
	        multipartFile.transferTo(savePath.toFile()); // 실제 파일 저장
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return new ImageUploadDTO(fileName, uuid, folderPath);
	}


}