package org.doit.sistticket.venue.controller;

import org.doit.sistticket.venue.domain.VenueDTO;
import org.doit.sistticket.venue.domain.VenueRequestDTO;
import org.doit.sistticket.venue.service.VenueService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/settings/venue")
@RequiredArgsConstructor
@Log4j2
public class VenueController {

    private final VenueService venueService;
/*
    // 1. 장소 목록 및 등록 페이지
    @GetMapping
    public String venueList(Model model) {
        log.info("==> 장소 관리 페이지 요청");
        model.addAttribute("venues", venueService.getVenueList());
        model.addAttribute("venueDTO", new VenueDTO());
        return "admin/venue/list"; // templates/admin/venue/list.html 파일 경로
    }
*/
    @GetMapping
    public String venueList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "venueId", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        log.info("==> 장소 관리 페이지 요청 (검색어: " + keyword + ")");
        
        model.addAttribute("venuePage", venueService.getVenueListbyKeyword(keyword, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("venueDTO", new VenueRequestDTO());
        
        return "/settings/venue"; // templates/admin/venue/list.html
    }

    @PostMapping("/new")
    public String registerVenue(VenueRequestDTO dto) {
        venueService.saveVenue(dto);
        return "redirect:/settings/venue";
    }

    @PostMapping("/update/{id}")
    public String updateVenue(@PathVariable("id") Long id, VenueRequestDTO dto) {
        venueService.updateVenue(id, dto);
        return "redirect:/settings/venue";
    }

    @PostMapping("/delete/{id}")
    public String deleteVenue(@PathVariable("id") Long id) {
        venueService.deleteVenue(id);
        return "redirect:/settings/venue";
    }
}