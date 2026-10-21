package org.doit.sistticket.category.service;


import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.dto.ConcertPageRequestDTO;
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
import org.doit.sistticket.category.repository.RoleRepository;
import org.doit.sistticket.review.entity.Reviews;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.doit.sistticket.venue.entity.Venue;
import org.doit.sistticket.venue.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Log4j2
@Service
public class ConcertServiceImpl implements ConcertService {
	private static final String R2_ENDPOINT = "https://c118a7efdddd35d3edac1db3a63ed76d.r2.cloudflarestorage.com";
	private static final String R2_ACCESS_KEY = "8f8a91958a3c06d4ce11ba80f5d60e2f";
	private static final String R2_SECRET_KEY = "5ab97a22e5baa3fe630165a9e770f0eada870d8672aaea80d3258ccbc2440667";
	private static final String R2_BUCKET = "productimage";
	private static final String R2_PUBLIC_URL = "https://pub-3490b121289f419194b634a98c9d4ba5.r2.dev";
	
	
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
    
    @Autowired

    private RoleRepository roleRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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
            
            // 리뷰 추가 부분
            ConcertListResponseDTO dto = entityToListDto(concert, thumbnailUrl);
            
            List<Reviews> reviewList = reviewRepository.findByConcert_ConcertId(concert.getConcertId());
            long reviewCount = reviewList.size();
            double ratingAvg = 0.0;
            if (reviewCount > 0) {
            	double sum = 0;
            	for(Reviews r : reviewList) {
            		sum += r.getRating();
            	}
            	ratingAvg = Math.round((sum / reviewCount) * 10) / 10.0;    	
            }
            
            dto.setRatingAvg(ratingAvg);
            dto.setReviewCount(reviewCount);
            
            return dto;
            
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
       

        // 리뷰 수정 부분
        return concertPage.map(concert -> {

        	 String imgUrl = concertImgRepository.findByConcert_ConcertIdAndIsThumbnail(concert.getConcertId(), true)
                     .map(ConcertImg::getImgUrl)
                     .orElse("");

        	List<Reviews> reviewList = reviewRepository.findByConcert_ConcertId(concert.getConcertId());
        	long reviewCount = reviewList.size();
            double ratingAvg = 0.0;
            if (reviewCount > 0) {
            	double sum = 0;
            	for(Reviews r : reviewList) {
            		sum += r.getRating();
            	}
            	ratingAvg = Math.round((sum / reviewCount) * 10) / 10.0;    	
            }
        	  
            return ConcertListResponseDTO.builder()
                .concertId(concert.getConcertId())
                .concertName(concert.getConcertName())
                .genreName(concert.getGenre() != null ? concert.getGenre().getGenreName() : "기타")
                .venueName(concert.getVenue() != null ? concert.getVenue().getVenueName() : "장소 미정")
                .startDate(concert.getStartDate())
                .endDate(concert.getEndDate())

                .imgUrl(concertImgRepository
                                .findByConcert_ConcertIdAndIsThumbnail(
                                        concert.getConcertId(), true)
                                .map(ConcertImg::getImgUrl)
                                .filter(url -> !url.isBlank())
                                .orElse(null)
                )

                .ratingAvg(ratingAvg)
                .reviewCount(reviewCount)
                .build();
        });
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
        
        String imgUrl = concertImgRepository.findByConcert_ConcertIdAndIsThumbnail(concertId, true)
                .map(ConcertImg::getImgUrl)
                .orElse("");
        // 해당 회차들에 속한 캐스팅 정보만 조회! (회차가 없다면 빈 리스트)
        List<PerformanceCast> casts = performanceIds.isEmpty() ? 
                List.of() : performanceCastRepository.findByPerformance_PerformanceIdIn(performanceIds);

        //인터페이스의 default 변환 메서드를 호출하여 상세 DTO 반환
        return entityToDetailDto(concert, performances, casts, imgUrl);
    }
    
    @Override
    @Transactional
    public Long registerConcert(ConcertRequestDTO dto, List<MultipartFile> imageFiles) {
        
        // 1. ID로 Genre와 Venue 엔티티 조회 (없으면 예외 처리)
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장르입니다."));
                
        Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));

        // 2. 콘서트 기본 정보 먼저 저장 (Concert PK 확보)
        Concert concert = Concert.builder()
                .concertName(dto.getConcertName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .runTime(Duration.ofMinutes(dto.getRunTimeMinutes()))
                .genre(genre)
                .venue(venue)
                .status(ConcertStatus.UPCOMING)
                .build();

        Concert savedConcert = concertRepository.save(concert);

        // 3. S3Client를 이용한 다중 이미지 업로드 및 `concert_imgs` 테이블 저장
        if (imageFiles != null && !imageFiles.isEmpty()) {
            
            S3Configuration s3Configuration = S3Configuration.builder()
                    .chunkedEncodingEnabled(false)
                    .build();

            try (S3Client s3Client = S3Client.builder()
                    .endpointOverride(URI.create(R2_ENDPOINT))
                    .region(Region.of("auto"))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
                    .serviceConfiguration(s3Configuration)
                    .build()) {

                int order = 0;
                for (MultipartFile imageFile : imageFiles) {
                    if (imageFile.isEmpty()) continue;

                    String originalFileName = imageFile.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String savedFileName = uuid + "_" + originalFileName;
                    String objectKey = "concerts/" + savedFileName; // S3 저장 폴더 경로

                    // S3 업로드 요청 객체 생성
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(R2_BUCKET)
                            .key(objectKey)
                            .contentType(imageFile.getContentType())
                            .build();

                    // S3 버킷에 파일 업로드 실행
                    s3Client.putObject(putObjectRequest, 
                            RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));

                    // 퍼블릭 URL 생성
                    String imgUrl = R2_PUBLIC_URL + "/" + objectKey;

                    // 첫 번째 이미지만 썸네일(true)로 지정
                    boolean isThumb = (order == 0);

                    // ConcertImg 엔티티 빌드 및 저장
                    ConcertImg concertImg = ConcertImg.builder()
                            .concert(savedConcert)
                            .imgUrl(imgUrl)
                            .isThumbnail(isThumb)
                            .imgOrder(order)
                            .uuid(uuid)
                            .imgName(originalFileName)
                            .path("concerts/")
                            .build();

                    concertImgRepository.save(concertImg);
                    order++;
                }

            } catch (Exception e) {
                log.error("S3 콘서트 이미지 다중 업로드 중 오류 발생: " + e.getMessage());
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        return savedConcert.getConcertId();
    }


    @Override
    @Transactional
    public void deleteConcert(Long concertId) {
        // 1. 콘서트 존재 확인
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트입니다. ID: " + concertId));

        // 2. 외래 키 충돌 방지를 위해 연관된 이미지 DB 기록 먼저 삭제
        concertImgRepository.deleteByConcert_ConcertId(concertId);

     // ② 회차-배역 매핑(PerformanceCast) 먼저 삭제 (회차나 배역을 지우기 전 필수!)
        performanceCastRepository.deleteByPerformance_Concert_ConcertId(concertId);

        // ③ 회차(Performances) 삭제
        performanceRepository.deleteByConcert_ConcertId(concertId);

        // ④ 배역(Role) 삭제
        roleRepository.deleteByConcert_ConcertId(concertId);
        
        // 3. 콘서트 삭제
        concertRepository.delete(concert);
        
        log.info("==> 콘서트 및 연관 이미지 삭제 완료 ID: " + concertId);
    }


	@Override
	public Long registerConcert(ConcertRequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Transactional
	public void updateConcert(Long concertId, ConcertRequestDTO dto, List<MultipartFile> imageFiles) {
	    
	    // 1. 기존 콘서트 조회 및 정보 수정
	    Concert concert = concertRepository.findById(concertId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘서트입니다. ID: " + concertId));

	    Genre genre = genreRepository.findById(dto.getGenreId())
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장르입니다."));
	    Venue venue = venueRepository.findById(dto.getVenueId())
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));

	    concert.setConcertName(dto.getConcertName());
	    concert.setStartDate(dto.getStartDate());
	    concert.setEndDate(dto.getEndDate());
	    concert.setRunTime(Duration.ofMinutes(dto.getRunTimeMinutes()));
	    concert.setStatus(dto.getStatus());
	    concert.setGenre(genre);
	    concert.setVenue(venue);

	    // 2. 새로운 이미지 파일들이 들어온 경우 (단일 or 다중 전부 지원)
	    if (imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
	        
	        // 💡 ① 기존 연관 이미지 DB 기록 싹 삭제! (중복 썸네일 원천 차단)
	        concertImgRepository.deleteByConcert_ConcertId(concertId);

	        S3Configuration s3Configuration = S3Configuration.builder()
	                .chunkedEncodingEnabled(false)
	                .build();

	        try (S3Client s3Client = S3Client.builder()
	                .endpointOverride(URI.create(R2_ENDPOINT))
	                .region(Region.of("auto"))
	                .credentialsProvider(StaticCredentialsProvider.create(
	                        AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
	                .serviceConfiguration(s3Configuration)
	                .build()) {

	            // 💡 ② 다중 파일 루프 처리
	            for (int i = 0; i < imageFiles.size(); i++) {
	                MultipartFile imageFile = imageFiles.get(i);
	                if (imageFile.isEmpty()) continue;

	                String originalFileName = imageFile.getOriginalFilename();
	                String uuid = UUID.randomUUID().toString();
	                String savedFileName = uuid + "_" + originalFileName;
	                String objectKey = "concerts/" + savedFileName;

	                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
	                        .bucket(R2_BUCKET)
	                        .key(objectKey)
	                        .contentType(imageFile.getContentType())
	                        .build();

	                s3Client.putObject(putObjectRequest, 
	                        RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));

	                String imgUrl = R2_PUBLIC_URL + "/" + objectKey;

	                // 💡 첫 번째 파일(i == 0)만 유일한 썸네일(true), 나머지는 false
	                boolean isThumb = (i == 0);

	                ConcertImg concertImg = ConcertImg.builder()
	                        .concert(concert)
	                        .imgUrl(imgUrl)
	                        .isThumbnail(isThumb)
	                        .imgOrder(i)
	                        .uuid(uuid)
	                        .imgName(originalFileName)
	                        .path("concerts/")
	                        .build();

	                concertImgRepository.save(concertImg);
	            }

	        } catch (Exception e) {
	            log.error("S3 콘서트 수정 이미지 업로드 중 오류 발생: " + e.getMessage());
	            throw new RuntimeException("이미지 수정 중 오류가 발생했습니다.", e);
	        }
	    }
	}

}