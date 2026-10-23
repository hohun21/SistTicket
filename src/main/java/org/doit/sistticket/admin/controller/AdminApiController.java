package org.doit.sistticket.admin.controller;

import lombok.RequiredArgsConstructor;
import org.doit.sistticket.admin.domain.*;
import org.doit.sistticket.admin.service.AdminService;
import org.doit.sistticket.member.domain.MemberDTO;
import org.doit.sistticket.member.service.MemberService;
import org.doit.sistticket.orders.service.OrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;
    private final MemberService memberService;
    private final OrdersService ordersService;

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
    @GetMapping("/reservation-cancellations")
    public ResponseEntity<List<AdminReservationDTO>> selectCancellres(){
        List<AdminReservationDTO> rcdto =
                this.ordersService.getCancellationReservations();
        return ResponseEntity.ok(rcdto);
    }
    @GetMapping("/reservations/{ordersId}")
    public ResponseEntity<List<AdminReservationDetailDTO>> getresdetail
            (@PathVariable("ordersId") Long ordersId){
        List<AdminReservationDetailDTO> rddto =
                this.ordersService.getAllReservationDetails(ordersId);
        return ResponseEntity.ok(rddto);
    }
    @GetMapping("/reservation-cancellations/{reservationId}")
    public ResponseEntity<List<AdminReservationDetailDTO>> getrescanceldetail
            (@PathVariable("reservationId") Long reservationId){
        List<AdminReservationDetailDTO> rdcdto =
                this.ordersService.getCancellationReservationDetails(reservationId);
        return ResponseEntity.ok(rdcdto);
    }
    @PatchMapping("/reservation-cancellations/{orderDetailId}/approve")
    public ResponseEntity<Integer> approveCancellation(
            @PathVariable Long orderDetailId
    ){
        int updated = this.ordersService.cancelReservations(orderDetailId);
        if(updated == 0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

}
