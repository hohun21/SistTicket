package org.doit.sistticket.category.controller;

import java.util.List;

import org.doit.sistticket.actor.entity.Actor;
import org.doit.sistticket.actor.repository.ActorRepository;
import org.doit.sistticket.category.entity.Concert;
import org.doit.sistticket.category.entity.PerformanceCast;
import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.category.entity.Role;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.category.repository.PerformanceCastRepository;
import org.doit.sistticket.category.repository.PerformancesRepository;
import org.doit.sistticket.category.repository.RoleRepository;
import org.doit.sistticket.category.service.ConcertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/settings/concert/{concertId}/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final RoleRepository roleRepository;
    private final ActorRepository actorRepository; // 배우 리포지토리
    private final PerformancesRepository performancesRepository;
    private final PerformanceCastRepository performanceCastRepository;
    private final ConcertRepository concertRepository;

    @GetMapping("/new")
    public String performanceForm(@PathVariable("concertId") Long concertId, Model model) {
        // 1. 이 콘서트에 등록된 배역 목록 조회
        List<Role> roles = roleRepository.findByConcert_ConcertId(concertId);
        
        // 2. 전체 배우 목록 조회 (배우 선택 셀렉트박스용)
        List<Actor> actors = actorRepository.findAll();
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));
        model.addAttribute("concert", concert); 
        model.addAttribute("concertId", concertId);
        model.addAttribute("roles", roles);       // 배역 목록 전달
        model.addAttribute("actors", actors);     // 배우 목록 전달
        
        return "settings/concert/performanceForm";
    }
    
 // 회차 수정 폼 페이지 (Update)
    @GetMapping("/{performanceId}/edit")
    public String performanceEditForm(
            @PathVariable("concertId") Long concertId,
            @PathVariable("performanceId") Long performanceId,
            Model model) {
        
        // 1. 수정할 회차 데이터 조회
        Performances performance = performancesRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));
        
        // 2. 해당 회차에 이미 매핑되어 있던 캐스팅 정보 조회 (선택사항이지만 수정 시 필요)
        List<PerformanceCast> existingCasts = performanceCastRepository.findByPerformance_PerformanceId(performanceId);
        
        // 3. 배역 목록 및 배우 목록 조회
        List<Role> roles = roleRepository.findByConcert_ConcertId(concertId);
        List<Actor> actors = actorRepository.findAll();
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));
        model.addAttribute("concert", concert); 
        model.addAttribute("concertId", concertId);
        model.addAttribute("performance", performance); // 기존 회차 데이터
        model.addAttribute("existingCasts", existingCasts); // 기존 캐스팅 데이터
        model.addAttribute("roles", roles);
        model.addAttribute("actors", actors);
        
        return "settings/concert/performanceForm"; // 등록과 같은 폼 사용!
    }
}