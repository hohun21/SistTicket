package org.doit.sistticket.admin.service;

import org.doit.sistticket.admin.domain.AdminDashBoardDTO;
import org.springframework.stereotype.Service;

@Service
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
