package org.doit.sistticket.admin.service;

import lombok.RequiredArgsConstructor;
import org.doit.sistticket.admin.domain.AdminDashBoardDTO;
import org.doit.sistticket.member.domain.MemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    public AdminDashBoardDTO getDashboard(){
        return AdminDashBoardDTO.builder()
                .totalReservations(0L)
                .totalMembers(0L)
                .totalConcerts(0L)
                .totalRevenue(0L)
                .build();
    }


}
