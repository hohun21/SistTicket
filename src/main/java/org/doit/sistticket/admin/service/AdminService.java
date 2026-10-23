package org.doit.sistticket.admin.service;

import lombok.RequiredArgsConstructor;
import org.doit.sistticket.admin.domain.AdminDashBoardDTO;
import org.doit.sistticket.admin.domain.AdminTopConcertDTO;
import org.doit.sistticket.category.repository.ConcertRepository;
import org.doit.sistticket.member.domain.MemberDTO;
import org.doit.sistticket.member.repository.MemberRepository;
import org.doit.sistticket.orders.repository.OrdersDetailRepository;
import org.doit.sistticket.orders.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final ConcertRepository concertRepository;
    private final OrdersDetailRepository ordersDetailRepository;

    public AdminDashBoardDTO getDashboard(){

        long totalMembers = this.memberRepository.countByStatus();
        long totalConcerts = this.concertRepository.countBystatus();
        long totalReservations = this.ordersDetailRepository.countBystatus();
        long totalSales = this.ordersDetailRepository.countPrice();

        return new AdminDashBoardDTO(
                totalMembers,
                totalConcerts,
                totalReservations,
                totalSales
        );
    }
    // Top 콘서트 조회
    public List<AdminTopConcertDTO> getTopConcert(){
        List<AdminTopConcertDTO> dto =this.concertRepository.findTopConcertsBySales();
        return dto;
    }


}
