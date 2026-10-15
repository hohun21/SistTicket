package org.doit.sistticket.payments.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.doit.sistticket.orders.entity.Orders;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "status")
    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    protected Payments() {
    }
}
