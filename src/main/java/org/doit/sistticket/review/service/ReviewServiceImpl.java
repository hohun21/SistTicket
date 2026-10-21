package org.doit.sistticket.review.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.doit.sistticket.review.dto.ReviewDTO;
import org.doit.sistticket.review.entity.Reviews;
import org.doit.sistticket.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {
	
	private final ReviewRepository reviewRepository;

	private static final String R2_ENDPOINT = "https://c118a7efdddd35d3edac1db3a63ed76d.r2.cloudflarestorage.com";
	private static final String R2_ACCESS_KEY = "8f8a91958a3c06d4ce11ba80f5d60e2f";
	private static final String R2_SECRET_KEY = "5ab97a22e5baa3fe630165a9e770f0eada870d8672aaea80d3258ccbc2440667";
	private static final String R2_BUCKET = "productimage";
	private static final String R2_PUBLIC_URL = "https://pub-3490b121289f419194b634a98c9d4ba5.r2.dev";
	
	@Override
	public Long register(ReviewDTO reviewDTO) {
		log.info("등록된 게시글 DTO 확인: " + reviewDTO);
		Reviews entity = dtoToEntity(reviewDTO);
		reviewRepository.save(entity);
		return entity.getReviewId();
	}

	@Override
	@Transactional
	public Long registerWithImage(ReviewDTO reviewDTO, MultipartFile imageFile) {
		String imgUrl = null;

		if (imageFile != null && !imageFile.isEmpty()) {
			S3Configuration s3Configuration = S3Configuration.builder().chunkedEncodingEnabled(false).build();
			try (S3Client s3Client = S3Client.builder()
					.endpointOverride(URI.create(R2_ENDPOINT))
					.region(Region.of("auto"))
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
					.serviceConfiguration(s3Configuration)
					.build()) {

				String originalFileName = imageFile.getOriginalFilename();
				String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
				String objectKey = "reviews/" + savedFileName;

				PutObjectRequest putObjectRequest = PutObjectRequest.builder()
						.bucket(R2_BUCKET)
						.key(objectKey)
						.contentType(imageFile.getContentType())
						.build();

				s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));
				imgUrl = R2_PUBLIC_URL + "/" + objectKey;

			} catch (Exception e) {
				log.error("S3 리뷰 이미지 업로드 중 오류 발생: " + e.getMessage());
			}
		}

		reviewDTO.setImgUrl(imgUrl);
		Reviews entity = dtoToEntity(reviewDTO);
		reviewRepository.save(entity);
		
		return entity.getReviewId();
	}

	@Override
	public List<ReviewDTO> getReviewsByConcertId(Long concertId) {


	    List<Reviews> result =
	            reviewRepository.findByConcert_ConcertId(concertId);

	    return result.stream()
	            .map(entity -> entityToDto(entity))
	            .collect(Collectors.toList());

	}

	@Override
	public List<ReviewDTO> getReviewsByMemberId(Long memberId) {


	    List<Reviews> result =
	            reviewRepository.findByMember_MemberId(memberId);

	    return result.stream()
	            .map(entity -> entityToDto(entity))
	            .collect(Collectors.toList());

	}
	
	@Transactional
	@Override
	public void modify(ReviewDTO reviewDTO) {
		Optional<Reviews> result = reviewRepository.findById(reviewDTO.getReviewId());
		if(result.isPresent()) {
			Reviews review = result.get();
			review.setText(reviewDTO.getText());
			review.setRating(reviewDTO.getRating());
			review.setImgUrl(reviewDTO.getImgUrl());
		}
	}

	@Transactional
	@Override
	public void modifyWithImage(ReviewDTO reviewDTO, MultipartFile imageFile, boolean deleteImage) {
		Reviews review = reviewRepository.findById(reviewDTO.getReviewId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

		String imgUrl = review.getImgUrl();

		if (deleteImage) {
			imgUrl = null;
		}
		
		else if (imageFile != null && !imageFile.isEmpty()) {
			S3Configuration s3Configuration = S3Configuration.builder().chunkedEncodingEnabled(false).build();
			try (S3Client s3Client = S3Client.builder()
					.endpointOverride(URI.create(R2_ENDPOINT))
					.region(Region.of("auto"))
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(R2_ACCESS_KEY, R2_SECRET_KEY)))
					.serviceConfiguration(s3Configuration)
					.build()) {

				String originalFileName = imageFile.getOriginalFilename();
				String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
				String objectKey = "reviews/" + savedFileName;

				PutObjectRequest putObjectRequest = PutObjectRequest.builder()
						.bucket(R2_BUCKET)
						.key(objectKey)
						.contentType(imageFile.getContentType())
						.build();

				s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));
				imgUrl = R2_PUBLIC_URL + "/" + objectKey;

			} catch (Exception e) {
				log.error("S3 리뷰 이미지 수정 업로드 중 오류 발생: " + e.getMessage());
			}
		}

		review.setText(reviewDTO.getText());
		review.setRating(reviewDTO.getRating());
		review.setImgUrl(imgUrl);
		
		reviewDTO.setImgUrl(imgUrl);
	}
	
	@Override
	public void remove(Long reviewId) {
		reviewRepository.deleteById(reviewId);
	}

}