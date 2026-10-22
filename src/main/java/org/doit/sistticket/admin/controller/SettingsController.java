package org.doit.sistticket.admin.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
@Log4j2
public class SettingsController {

    // 관리자
    @GetMapping("/main")
    public String settingsMain() {
        log.info("==> 관리자 메인 설정 페이지 요청");
        return "settings/main"; 
    }

}