package org.doit.sistticket.category.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class ConcertController {

	
	 @GetMapping("/concert/list")
	    public String index() {
	        return "concert/list";
	    }
}
