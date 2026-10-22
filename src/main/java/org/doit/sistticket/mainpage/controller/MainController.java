package org.doit.sistticket.mainpage.controller;

import java.util.List;

import org.doit.sistticket.mainpage.dto.MainConcertDTO;
import org.doit.sistticket.mainpage.service.MainService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/")
    public String index(Model model) {

        List<MainConcertDTO> latestConcerts = mainService.getLatestConcerts();
        
        model.addAttribute("latestConcerts", latestConcerts);
        
        return "index";
    }
}