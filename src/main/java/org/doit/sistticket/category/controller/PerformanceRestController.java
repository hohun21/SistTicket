package org.doit.sistticket.category.controller;


import org.doit.sistticket.category.dto.PerformanceRequestDTO;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.category.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final PerformancesRepository performancesRepository;
    private final PerformanceCastRepository performanceCastRepository;

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
    
 // 회차 단건 상세 조회 API (수정 모드 데이터 로드용 - 회차 정보 + 캐스팅 목록 함께 전송)
    @GetMapping("/{performanceId}")
    public ResponseEntity<?> getPerformanceDetail(
            @PathVariable("concertId") Long concertId,
            @PathVariable("performanceId") Long performanceId) {
        
        Performances performance = performancesRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));
        
        // PerformanceController의 로직을 참고하여 기존 캐스팅 목록 함께 조회[cite: 29]
        var existingCasts = performanceCastRepository.findByPerformance_PerformanceId(performanceId);
        
        // 프론트엔드가 회차 기본정보와 캐스팅을 모두 누락 없이 받을 수 있도록 맵으로 패키징
        return ResponseEntity.ok(java.util.Map.of(
                "performance", performance,
                "casts", existingCasts
        ));
    }
}