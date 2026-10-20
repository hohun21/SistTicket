package org.doit.sistticket.category.controller;

import java.util.List;

import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.service.ConcertService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/concert")
@RequiredArgsConstructor
@Log4j2
public class ConcertRestController {

    private final ConcertService concertService;

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

    // 3. 콘서트 삭제 (DELETE /api/admin/concert/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable("id") Long concertId) {
        log.info("==> [API] 콘서트 삭제 요청 ID: " + concertId);
        concertService.deleteConcert(concertId);
        return ResponseEntity.ok().build();
    }
}