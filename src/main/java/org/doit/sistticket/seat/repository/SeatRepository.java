package org.doit.sistticket.seat.repository;

import org.doit.sistticket.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
