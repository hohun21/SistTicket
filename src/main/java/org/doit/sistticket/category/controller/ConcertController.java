package org.doit.sistticket.category.controller;

import java.util.List;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.service.ConcertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ConcertController {

	
	private final ConcertService concertService;

    // 콘서트 목록 화면 요청 (GET /concert/list)
    @GetMapping("/concert/list")
    public String concertList(Model model) {
        log.info("==> 콘서트 목록 페이지 요청");

        List<ConcertListResponseDTO> concertList = concertService.getList();

        model.addAttribute("concertList", concertList);

        return "concert/list";
    }
    
    // 상세 보기 페이지 요청
    @GetMapping("/concert/{id}")
    public String concertDetail(@PathVariable("id") Long concertId, Model model) {
    	 log.info("==> 콘서트 상세보기: " + concertId);
        // ConcertService를 통해 콘서트 + 회차 + 캐스팅이 모두 담긴 상세 DTO
        ConcertDetailResponseDTO concertDetail = concertService.getDetail(concertId);
        
        model.addAttribute("concert", concertDetail);
        
        return "concert/detail"; // src/main/resources/templates/concert/detail.html
    }
}
