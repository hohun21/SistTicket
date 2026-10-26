package org.doit.sistticket.actor.controller;

import org.doit.sistticket.actor.domain.ActorRequestDTO;
import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.service.ActorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/actor")
@RequiredArgsConstructor
@Log4j2
public class ActorApiController {

    private final ActorService actorService;

    // 1. 배우 목록 조회 (페이징 + 검색) -> 리액트에서 GET /api/actor 로 호출
    @GetMapping
    public ResponseEntity<Page<Actor>> actorList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "actorId", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("==> [API] 배우 목록 조회 요청 (검색어: " + keyword + ")");
        Page<Actor> actorPage = actorService.getActorList(keyword, pageable);
        return ResponseEntity.ok(actorPage);
    }

    // 2. 새 배우 등록 처리 -> 리액트에서 POST /api/actor/new 로 호출
    @PostMapping("/new")
    public ResponseEntity<String> registerActor(@RequestBody ActorRequestDTO actorDTO) {
        log.info("==> [API] 새 배우 등록 요청: " + actorDTO);
        actorService.saveActor(actorDTO);
        return ResponseEntity.ok("성공적으로 등록되었습니다.");
    }

    // 3. 배우 수정 처리 -> 리액트에서 POST /api/actor/update/{id} 로 호출
    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateActor(
            @PathVariable("id") Long id, 
            @RequestBody ActorRequestDTO actorDTO) {
        log.info("==> [API] 배우 수정 요청 ID: " + id);
        actorService.updateActor(id, actorDTO);
        return ResponseEntity.ok("수정되었습니다.");
    }

    // 4. 배우 삭제 처리 -> 리액트에서 POST /api/actor/delete/{id} 로 호출
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteActor(@PathVariable("id") Long id) {
        log.info("==> [API] 배우 삭제 요청 ID: " + id);
        actorService.deleteActor(id);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}