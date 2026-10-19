package org.doit.sistticket.seat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.doit.sistticket.seat.repository.PerformanceSeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final PerformanceSeatRepository performanceSeatRepository;

    @Transactional
    public void confirmAfterPayment(String holdToken) {
        if (holdToken == null || holdToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "선점 토큰이 없습니다.");
        }

        List<PerformanceSeat> seats =
                performanceSeatRepository.findByHoldTokenForUpdate(holdToken);

        if (seats.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "선점된 좌석을 찾을 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        for (PerformanceSeat seat : seats) {
            if (!seat.canConfirm(holdToken, now)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "선점 시간이 만료되었습니다.");
            }
        }

        for (PerformanceSeat seat : seats) {
            seat.confirm();
        }
    }
}