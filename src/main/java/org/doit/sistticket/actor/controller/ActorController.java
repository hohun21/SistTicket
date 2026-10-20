package org.doit.sistticket.actor.controller;

import org.doit.sistticket.actor.domain.ActorDTO;
import org.doit.sistticket.actor.service.ActorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/admin/actor")
@RequiredArgsConstructor
@Log4j2
public class ActorController {

    private final ActorService actorService;

    //배우/아티스트 목록 및 등록 페이지
    @GetMapping
    public String actorList(Model model) {
        log.info("==> 배우 관리 페이지 요청");
        model.addAttribute("actors", actorService.getActorList());
        model.addAttribute("actorDTO", new ActorDTO());
        return "admin/actor/list"; // templates/admin/actor/list.html 파일 경로
    }

    //배우/아티스트 등록 처리 
    @PostMapping("/new")
    public String registerActor(ActorDTO actorDTO) {
        log.info("==> 새 배우 등록 요청: " + actorDTO);
        actorService.saveActor(actorDTO);
        return "redirect:/admin/actor";
    }

    //배우 삭제 처리 
    @PostMapping("/delete/{id}")
    public String deleteActor(@PathVariable("id") Long id) {
        log.info("==> 배우 삭제 요청 ID: " + id);
        actorService.deleteActor(id);
        return "redirect:/admin/actor";
    }
}