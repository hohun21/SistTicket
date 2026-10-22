package org.doit.sistticket.category.controller;


import org.doit.sistticket.category.dto.PerformanceRequestDTO;
import org.doit.sistticket.category.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/concert/{concertId}/performance")
@RequiredArgsConstructor
@Log4j2
public class PerformanceRestController {

    private final PerformanceService performanceService;

    @PostMapping("")
    public ResponseEntity<Void> registerPerformance(
            @PathVariable("concertId") Long concertId,
            @RequestBody PerformanceRequestDTO dto) {
        
        log.info("==> 회차 및 캐스팅 등록 요청: " + dto);
        performanceService.registerPerformance(concertId, dto);
        
        return ResponseEntity.ok().build();
    }
    
 // 회차 수정 API
    @PutMapping("/{performanceId}")
    public ResponseEntity<Void> updatePerformance(
            @PathVariable("performanceId") Long performanceId,
            @RequestBody PerformanceRequestDTO dto) {
        performanceService.updatePerformance(performanceId, dto);
        return ResponseEntity.ok().build();
    }

    // 회차 삭제 API
    @DeleteMapping("/{performanceId}")
    public ResponseEntity<Void> deletePerformance(@PathVariable("performanceId") Long performanceId) {
        performanceService.deletePerformance(performanceId);
        return ResponseEntity.ok().build();
    }
}