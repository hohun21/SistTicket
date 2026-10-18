package org.doit.sistticket.seat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.doit.sistticket.category.entity.Performances;

// Performance import는 팀원의 실제 패키지 경로로 수정

@Getter
@Entity
@Table(
    name = "performance_seats",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_performance_seat",
        columnNames = {"performance_id", "seat_id"}
    )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_seat_id")
    private Long performanceSeatId;

    // 공연 회차 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performances performance;

    // 좌석 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;
}