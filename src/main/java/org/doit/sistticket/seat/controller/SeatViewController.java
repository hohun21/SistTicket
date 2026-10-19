package org.doit.sistticket.seat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SeatViewController {

    @GetMapping("/seat/select")
    public String selectSeat() {
        return "seat/select";
    }
}