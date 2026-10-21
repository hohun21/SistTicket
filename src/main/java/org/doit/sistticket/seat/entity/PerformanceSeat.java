package org.doit.sistticket.seat.entity;

import java.time.LocalDateTime;

import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.member.entity.Members;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hold_member_id")
    private Members holdMember;

    // 공연 회차와 기본 좌석을 연결한 새 회차별 좌석 생성
    public static PerformanceSeat create(Performances performance, Seat seat) {

        if (performance == null || seat == null) {
            throw new IllegalArgumentException("공연 회차와 좌석 정보가 필요합니다.");
        }
        PerformanceSeat performanceSeat = new PerformanceSeat();
        performanceSeat.performance = performance;
        performanceSeat.seat = seat;
        performanceSeat.status = "AVAILABLE";

        return performanceSeat;
    }
    
    // 현재 좌석을 선점할 수 있는지 확인
    public boolean canHold(LocalDateTime now) {
        if ("AVAILABLE".equals(status)) {
            return true;
        }

        return "HELD".equals(status)
                && holdExpiresAt != null
                && !holdExpiresAt.isAfter(now);
    }

    // 기존 주문·결제 서비스 호환용 임시 선점
    public void hold(LocalDateTime expiresAt) {
        this.status = "HELD";
        this.holdToken = null;
        this.holdExpiresAt = expiresAt;
    }

    // 토큰을 이용하는 좌석 홀드 기능
    public void hold(String holdToken, LocalDateTime expiresAt, Members member) {
        this.status = "HELD";
        this.holdToken = holdToken;
        this.holdExpiresAt = expiresAt;
        this.holdMember = member;
    }

    // 해당 토큰으로 예약 확정이 가능한지 확인
    public boolean canConfirm(String token, LocalDateTime now) {
        return "HELD".equals(status)
                && holdToken != null
                && holdToken.equals(token)
                && holdExpiresAt != null
                && holdExpiresAt.isAfter(now);
    }

    // 기존 주문·결제 서비스 호환용 예약 확정
    public void reserve() {
        this.status = "RESERVED";
        this.holdToken = null;
        this.holdExpiresAt = null;
        this.holdMember = null;
    }

    // 좌석 팀원의 예약 확정 메서드
    public void confirm() {
        reserve();
    }

    // 좌석 반환
    public void release() {
        this.status = "AVAILABLE";
        this.holdToken = null;
        this.holdExpiresAt = null;
        this.holdMember = null;
    }
    
   
}
