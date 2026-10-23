package org.doit.sistticket.admin.controller;

import lombok.RequiredArgsConstructor;
import org.doit.sistticket.admin.domain.*;
import org.doit.sistticket.admin.service.AdminService;
import org.doit.sistticket.member.domain.MemberDTO;
import org.doit.sistticket.member.service.MemberService;
import org.doit.sistticket.orders.service.OrdersService;
import org.doit.sistticket.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;
    private final MemberService memberService;
    private final OrdersService ordersService;
    private final ReviewService reviewService;

    @GetMapping("/dashboard")
    public AdminDashBoardDTO dashBoardDTO(){
        return adminService.getDashboard();
    }

    @GetMapping("/members")
    public List<AdminMemberDTO> allMembers() {
        List<AdminMemberDTO> mdto = this.memberService.findAllAdminMembers();
        return mdto;
    }

    @PatchMapping("/members/{memberId}")
    public ResponseEntity<Void> updateMember(
            @PathVariable("memberId") Long memberId,
            @RequestBody AdminMemberUpdateDTO dto){
        memberService.updateMember(memberId,dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> checkAdmin(){
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<AdminReservationDTO>> selectAllReservations(){
        List<AdminReservationDTO> rdto = this.ordersService.getAllReservations();
        return ResponseEntity.ok(rdto);
    }

    @GetMapping("/reservations/{ordersId}")
    public ResponseEntity<List<AdminReservationDetailDTO>> getresdetail
            (@PathVariable("ordersId") Long ordersId){
        List<AdminReservationDetailDTO> rddto =
                this.ordersService.getAllReservationDetails(ordersId);
        return ResponseEntity.ok(rddto);
    }
    
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReviewByAdmin(@PathVariable("reviewId") Long reviewId) {
        try {
            reviewService.remove(reviewId);
            return ResponseEntity.ok(Map.of("success", true, "message", "관리자 권한으로 리뷰가 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "리뷰 삭제에 실패했습니다."));
        }
    }
}
