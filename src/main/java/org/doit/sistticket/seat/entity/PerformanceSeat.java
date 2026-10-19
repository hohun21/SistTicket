package org.doit.sistticket.seat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.doit.sistticket.category.entity.Performances;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performances performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;

    @Column(name = "hold_token", length = 36)
    private String holdToken;

    // 지금 선점할 수 있는 좌석인지 확인
    public boolean canHold(LocalDateTime now) {

        if ("AVAILABLE".equals(status)) {
            return true;
        }

        return "HELD".equals(status)
                && holdExpiresAt != null
                && !holdExpiresAt.isAfter(now);
    }

    // 좌석 선점
    public void hold(String holdToken, LocalDateTime holdExpiresAt) {
        this.status = "HELD";
        this.holdToken = holdToken;
        this.holdExpiresAt = holdExpiresAt;
    }

    // 이 토큰으로 아직 확정할 수 있는지 확인
    public boolean canConfirm(String token, LocalDateTime now) {
        return "HELD".equals(status)
                && holdToken != null
                && holdToken.equals(token)
                && holdExpiresAt != null
                && holdExpiresAt.isAfter(now);
    }

    // 결제 승인 후 좌석 확정
    public void confirm() {
        status = "RESERVED";
        holdToken = null;
        holdExpiresAt = null;
    }

}