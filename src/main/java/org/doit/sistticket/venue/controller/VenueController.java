package org.doit.sistticket.venue.controller;

import org.doit.sistticket.venue.domain.VenueDTO;
import org.doit.sistticket.venue.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/admin/venue")
@RequiredArgsConstructor
@Log4j2
public class VenueController {

    private final VenueService venueService;

    // 1. 장소 목록 및 등록 페이지
    @GetMapping
    public String venueList(Model model) {
        log.info("==> 장소 관리 페이지 요청");
        model.addAttribute("venues", venueService.getVenueList());
        model.addAttribute("venueDTO", new VenueDTO());
        return "admin/venue/list"; // templates/admin/venue/list.html 파일 경로
    }

    // 2. 장소 등록 처리
    @PostMapping("/new")
    public String registerVenue(VenueDTO venueDTO) {
        log.info("==> 새 장소 등록 요청: " + venueDTO);
        venueService.saveVenue(venueDTO);
        return "redirect:/admin/venue";
    }

    // 3. 장소 삭제 처리 
    @PostMapping("/delete/{id}")
    public String deleteVenue(@PathVariable("id") Long id) {
        log.info("==> 장소 삭제 요청 ID: " + id);
        venueService.deleteVenue(id);
        return "redirect:/admin/venue";
    }
}