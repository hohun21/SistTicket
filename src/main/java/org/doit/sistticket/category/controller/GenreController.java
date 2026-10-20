package org.doit.sistticket.category.controller;

import org.doit.sistticket.category.dto.GenreDTO;
import org.doit.sistticket.category.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/admin/genre")
@RequiredArgsConstructor
@Log4j2
public class GenreController {

    private final GenreService genreService;

    // 장르 관리 페이지 (목록 + 등록 폼)
    @GetMapping
    public String genreList(Model model) {
        model.addAttribute("genres", genreService.getGenreList());
        model.addAttribute("genreDTO", new GenreDTO());
        return "admin/genre/list"; // templates/admin/genre/list.html
    }

    // 장르 등록 처리
    @PostMapping("/new")
    public String registerGenre(@ModelAttribute GenreDTO genreDTO) {
        log.info("==> 새 장르 등록 요청: " + genreDTO);
        genreService.saveGenre(genreDTO);
        return "redirect:/admin/genre";
    }

    // 장르 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteGenre(@PathVariable("id") Long id) {
        log.info("==> 장르 삭제 요청 ID: " + id);
        genreService.deleteGenre(id);
        return "redirect:/admin/genre";
    }
}