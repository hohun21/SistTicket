package org.doit.sistticket.category.controller;

import java.util.List;

import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.service.ConcertService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import org.doit.sistticket.category.dto.ConcertPageRequestDTO;
import org.doit.sistticket.category.service.GenreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/concert")
@RequiredArgsConstructor
@Log4j2
public class ConcertRestController {

    private final ConcertService concertService;
    private final GenreService genreService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> registerConcert(
            @RequestPart("dto") ConcertRequestDTO dto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        
        log.info("==>S3 기반 콘서트 및 다중 이미지 등록 요청: " + dto);
        Long concertId = concertService.registerConcert(dto, imageFiles);
        
        return ResponseEntity.ok(concertId);
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateConcert(
            @PathVariable("id") Long concertId,
            @RequestPart("dto") ConcertRequestDTO dto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        
        log.info("==> [API] 콘서트 수정 요청 ID: " + concertId);
        concertService.updateConcert(concertId, dto, imageFiles); // 서비스에 이미지 처리 로직 포함
        
        return ResponseEntity.ok(concertId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable("id") Long concertId) {
        log.info("==> [API] 콘서트 삭제 요청 ID: " + concertId);
        concertService.deleteConcert(concertId);
        
        return ResponseEntity.ok().build();
    }
    
    // 미성 추가
    @GetMapping("/{id}")
    public ResponseEntity<ConcertDetailResponseDTO> getDetail(
            @PathVariable("id") Long concertId) {

        return ResponseEntity.ok(concertService.getDetail(concertId));
    }
    
    @GetMapping
    public ResponseEntity<?> getList(
            @ModelAttribute ConcertPageRequestDTO requestDTO) {

        requestDTO.setPage(Math.max(0, requestDTO.getPage()));
        requestDTO.setSize(Math.max(1, Math.min(50, requestDTO.getSize())));

        String type = requestDTO.getType();

        if (!"n".equals(type) && !"v".equals(type) && !"nv".equals(type)) {
            requestDTO.setType("nv");
        }

        var result = concertService.getConcertList(requestDTO);

        return ResponseEntity.ok(Map.of(
                "content", result.getContent(),
                "number", result.getNumber(),
                "totalPages", result.getTotalPages(),
                "totalElements", result.getTotalElements()
        ));
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getGenres() {
        var genres = genreService.getGenreList().stream()
                .map(genre -> Map.of(
                        "genreId", genre.getGenreId(),
                        "genreName", genre.getGenreName()
                ))
                .toList();

        return ResponseEntity.ok(genres);
    }
    
}