package org.doit.sistticket.venue.controller;

import org.doit.sistticket.venue.domain.VenueRequestDTO;
import org.doit.sistticket.venue.entity.Venue; // 혹은 반환 타입에 맞는 엔티티/DTO 경로 설정
import org.doit.sistticket.venue.service.VenueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/venue")
@RequiredArgsConstructor
@Log4j2
public class VenueApiController {

    private final VenueService venueService;

    // 1. 장소 목록 조회 (페이징 + 키워드 검색) -> 리액트에서 GET /api/venue 로 호출
    @GetMapping
    public ResponseEntity<Page<?>> venueList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "venueId", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("==> [API] 장소 관리 목록 조회 요청 (검색어: " + keyword + ")");
        
        Page<?> venuePage = venueService.getVenueListbyKeyword(keyword, pageable);
        return ResponseEntity.ok(venuePage);
    }

    // 2. 새 장소 등록 처리 -> 리액트에서 POST /api/venue/new 로 호출
    @PostMapping("/new")
    public ResponseEntity<String> registerVenue(@RequestBody VenueRequestDTO dto) {
        log.info("==> [API] 새 장소 등록 요청: " + dto);
        venueService.saveVenue(dto);
        return ResponseEntity.ok("성공적으로 등록되었습니다.");
    }

    // 3. 장소 수정 처리 -> 리액트에서 POST /api/venue/update/{id} 로 호출
    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateVenue(
            @PathVariable("id") Long id, 
            @RequestBody VenueRequestDTO dto) {
        log.info("==> [API] 장소 수정 요청 ID: " + id);
        venueService.updateVenue(id, dto);
        return ResponseEntity.ok("수정되었습니다.");
    }

    // 4. 장소 삭제 처리 -> 리액트에서 POST /api/venue/delete/{id} 로 호출
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteVenue(@PathVariable("id") Long id) {
        log.info("==> [API] 장소 삭제 요청 ID: " + id);
        venueService.deleteVenue(id);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}