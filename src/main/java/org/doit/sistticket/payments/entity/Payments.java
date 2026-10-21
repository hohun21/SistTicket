package org.doit.sistticket.payments.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.orders.entity.Orders;
import org.doit.sistticket.payments.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    // jpa용 생성자
    protected Payments() {
    }
    
    // 새로운 결제 생성
    public Payments(
    		String paymentMethod,
    		BigDecimal amount,
    		Orders orders) {
    	this.paymentMethod = paymentMethod;
    	this.amount = amount;
    	this.orders = orders;
    	this.status = PaymentStatus.PENDING;
    }
    
    // 결제 성공 처리
    public void completePayment(String paymentKey, LocalDateTime paidAt) {
    	this.paymentKey = paymentKey;
    	this.status = PaymentStatus.SUCCESS;
    	this.paidAt = paidAt;
    }
    
    // 결제 실패 처리
    public void failPayment() {
    	this.status = PaymentStatus.FAILED;
    }
    
    // 
    public void cancelPayment(LocalDateTime cancelledAt) {
    	if(this.status != PaymentStatus.SUCCESS) {
    		throw new IllegalStateException("성공한 결제만 취소할 수 있습니다.");
    	}
    	this.status = PaymentStatus.CANCELLED;
    	this.cancelledAt = cancelledAt;
    }
    
    
} // class
