package org.doit.sistticket.orders.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.orders.enums.OrdersDetailStatus;
import org.doit.sistticket.seat.entity.PerformanceSeat;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders_detail")
public class OrdersDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_seat_id")
    private PerformanceSeat performanceSeat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    // 주문, 좌석 처리 규칙!
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrdersDetailStatus status;

    @Column(name = "price")
    private BigDecimal price;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected OrdersDetail() {
    }
    
    public OrdersDetail(
    		PerformanceSeat performanceSeat,
    		Orders orders,
    		OrdersDetailStatus status,
    		BigDecimal price) {
    	this.performanceSeat = performanceSeat;
    	this.orders = orders;
    	this.status = status;
    	this.price = price;
    }
    
    public void confirm() {
    	this.status = OrdersDetailStatus.CONFIRMED;
    }
    
    public void expire() {
    	this.status = OrdersDetailStatus.EXPIRED;
    }
    
    public void cancel() {
        this.status = OrdersDetailStatus.CANCELLED;
    }
    
    public void cancelRequest() {
        this.status = OrdersDetailStatus.CANCELREQUEST;
    }
    
} // class
