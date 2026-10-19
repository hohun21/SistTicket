package org.doit.sistticket.category.controller;

import org.doit.sistticket.category.dto.ConcertRequestDTO;
import org.doit.sistticket.category.service.ConcertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/admin/concert")
@RequiredArgsConstructor
@Log4j2
public class ConcertRestController {

    private final ConcertService concertService;

    // 1. 콘서트 등록 (POST /api/admin/concert)
    @PostMapping
    public ResponseEntity<Long> registerConcert(@RequestBody ConcertRequestDTO dto) {
        log.info("==> [API] 콘서트 등록 요청: " + dto);
        Long concertId = concertService.registerConcert(dto);
        return ResponseEntity.ok(concertId);
    }

    // 2. 콘서트 수정 (PUT /api/admin/concert/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateConcert(@PathVariable("id") Long concertId, @RequestBody ConcertRequestDTO dto) {
        log.info("==> [API] 콘서트 수정 요청 ID: " + concertId + ", 데이터: " + dto);
        concertService.updateConcert(concertId, dto);
        return ResponseEntity.ok().build();
    }

    // 3. 콘서트 삭제 (DELETE /api/admin/concert/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable("id") Long concertId) {
        log.info("==> [API] 콘서트 삭제 요청 ID: " + concertId);
        concertService.deleteConcert(concertId);
        return ResponseEntity.ok().build();
    }
}