package org.doit.sistticket.mainpage.controller;

import java.util.List;

import org.doit.sistticket.mainpage.dto.MainConcertDTO;
import org.doit.sistticket.mainpage.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainApiController {

    private final MainService mainService;

    @GetMapping("/latest")
    public ResponseEntity<List<MainConcertDTO>> getLatestConcerts() {
        List<MainConcertDTO> latestConcerts = mainService.getLatestConcerts();
        return ResponseEntity.ok(latestConcerts);
    }
}