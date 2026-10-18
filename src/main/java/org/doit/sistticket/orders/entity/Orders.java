package org.doit.sistticket.orders.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.category.entity.Performances;
import org.doit.sistticket.member.entity.Members;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long ordersId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Members member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performances performance;

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // JPA
    protected Orders() {
    }
    
    // 새로운 주문 만들때
    public Orders(
    		Members members,
    		Performances performances,
    		String orderNo,
    		BigDecimal totalAmount) {
    	this.member = members;
    	this.performance = performances;
    	this.orderNo = orderNo;
    	this.totalAmount = totalAmount;
    }
    
} // class
