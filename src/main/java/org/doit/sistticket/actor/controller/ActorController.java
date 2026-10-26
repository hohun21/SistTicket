package org.doit.sistticket.actor.controller;

import org.doit.sistticket.actor.domain.ActorDTO;
import org.doit.sistticket.actor.domain.ActorRequestDTO;
import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.service.ActorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/settings/actor")
@RequiredArgsConstructor
@Log4j2
public class ActorController {

    private final ActorService actorService;
/*
    //배우/아티스트 목록 및 등록 페이지 (자식 테이블 호출용)
    @GetMapping
    public String actorsampleList(Model model) {
        log.info("==> 배우 관리 페이지 요청");
        model.addAttribute("actors", actorService.getActorList());
        model.addAttribute("actorDTO", new ActorDTO());
        return "admin/actor/list"; // templates/admin/actor/list.html 파일 경로
    }
*/
    // 배우/아티스트 목록 (페이징 + 이름 검색 적용)
    @GetMapping
    public String actorList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "actorId", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        log.info("==> 배우 관리 페이지 요청 (검색어: " + keyword + ")");
        
        // 페이징 및 검색이 반영된 목록 조회
        model.addAttribute("actorPage", actorService.getActorList(keyword, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("actorDTO", new ActorRequestDTO()); // 등록 폼 바인딩용
        
        return "settings/actor"; // templates/admin/actor/list.html
    }

    // 배우/아티스트 등록 처리 
    @PostMapping("/new")
    public String registerActor(ActorRequestDTO actorDTO) {
        log.info("==> 새 배우 등록 요청: " + actorDTO);
        actorService.saveActor(actorDTO);
        return "redirect:/settings/actor";
    }

    // 배우 수정 처리 
    @PostMapping("/update/{id}")
    public String updateActor(@PathVariable("id") Long id, ActorRequestDTO actorDTO) {
        log.info("==> 배우 수정 요청 ID: " + id);
        actorService.updateActor(id, actorDTO);
        return "redirect:/settings/actor";
    }

    // 배우 삭제 처리 
    @PostMapping("/delete/{id}")
    public String deleteActor(@PathVariable("id") Long id) {
        log.info("==> 배우 삭제 요청 ID: " + id);
        actorService.deleteActor(id);
        return "redirect:/settings/actor";
    }
    
    @GetMapping("/api/actorlist")
    @ResponseBody
    public Page<Actor> actorApiList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "actorId", direction = Sort.Direction.DESC) Pageable pageable) {
        return actorService.getActorList(keyword, pageable);
    }
}