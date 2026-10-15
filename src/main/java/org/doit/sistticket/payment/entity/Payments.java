package org.doit.sistticket.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

<<<<<<< HEAD:src/main/java/org/doit/sistticket/payment/entity/Payments.java
import org.doit.sistticket.order.entity.Orders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
=======
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
>>>>>>> origin/main:src/main/java/org/doit/SistTicket/payment/entity/Payments.java
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

<<<<<<< HEAD:src/main/java/org/doit/sistticket/payment/entity/Payments.java
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
=======
    @Column(name = "orders_id", nullable = false)
    private Long ordersId;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 30)
>>>>>>> origin/main:src/main/java/org/doit/SistTicket/payment/entity/Payments.java
    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

<<<<<<< HEAD:src/main/java/org/doit/sistticket/payment/entity/Payments.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

=======
>>>>>>> origin/main:src/main/java/org/doit/SistTicket/payment/entity/Payments.java
    protected Payments() {
    }
}
