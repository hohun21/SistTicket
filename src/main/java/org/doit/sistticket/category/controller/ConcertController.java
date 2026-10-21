package org.doit.sistticket.category.controller;

import org.doit.sistticket.category.dto.ConcertDetailResponseDTO;
import org.doit.sistticket.category.dto.ConcertListResponseDTO;
import org.doit.sistticket.category.dto.ConcertPageRequestDTO;
import org.doit.sistticket.category.service.ConcertService;
import org.doit.sistticket.category.service.GenreService;
import org.doit.sistticket.venue.service.VenueService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ConcertController {

	
	private final ConcertService concertService;
	
	private final VenueService venueService;
	
	private final GenreService genreService;
	
	

	@GetMapping("/concert/list")
	public String concertList(
	        @ModelAttribute("searchDTO") ConcertPageRequestDTO searchDTO,
	        Model model) {
	    
	    Page<ConcertListResponseDTO> concertList = concertService.getConcertList(searchDTO);
	    model.addAttribute("concertList", concertList);
	    model.addAttribute("genres", genreService.getGenreList());
	    
	    return "concert/list_customer";
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
    
    
    @GetMapping("/concert/new")
    public String concertForm(Model model) {
        log.info("==> 관리자 콘서트 등록 폼 페이지 요청");
        
        // 폼에서 셀렉트박스로 고를 수 있게 장소와 장르 목록을 모델에 담아줍니다.
        // (venueRepository, genreRepository를 주입받아 사용하거나 service를 통해 가져옵니다)
        model.addAttribute("venues", venueService.getVenueList()); // 또는 venueRepository.findAll()
        model.addAttribute("genres", genreService.getGenreList()); // 또는 genreRepository.findAll()
        
        return "concert/concertForm"; // templates/admin/concert/form.html
    }
 // 콘서트 수정 폼 페이지 요청 (GET /admin/concert/edit/{id})
    @GetMapping("/concert/edit/{id}")
    public String concertEditForm(@PathVariable("id") Long concertId, Model model) {
        log.info("==> 관리자 콘서트 수정 폼 페이지 요청 ID: " + concertId);
        
        // 1. 수정할 기존 콘서트 데이터 조회 (Detail DTO 또는 Request DTO 활용)
        ConcertDetailResponseDTO concert = concertService.getDetail(concertId);
        
        // 2. 셀렉트박스용 장소/장르 목록
        model.addAttribute("venues", venueService.getVenueList());
        model.addAttribute("genres", genreService.getGenreList());
        
        // 3. 기존 콘서트 데이터를 폼에 전달
        model.addAttribute("concert", concert);
        
        return "concert/concertForm"; // 등록과 똑같은 form.html을 재사용!
    }
}
