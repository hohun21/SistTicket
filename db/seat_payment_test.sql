-- Seat / order / payment integration fixture for a fresh MySQL 8 test server.
-- Run once on a LOCAL or dedicated TEST server. Never run against the shared DB.
-- This script creates only the tables needed for seat -> order -> payment tests.
-- Test accounts: seat1@example.test and seat2@example.test / Test1234!
-- The stored password is a BCrypt hash. Do not reuse these accounts elsewhere.
-- Connect the application's local datasource to sistticket_seat_test before testing.

-- 1. Database
CREATE DATABASE sistticket_seat_test
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE sistticket_seat_test;

-- 2. Tables (based on the current entity mappings)
CREATE TABLE members (
    members_id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    status INT NOT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (members_id),
    UNIQUE KEY uk_members_email (email)
) ENGINE=InnoDB;

CREATE TABLE members_roles (
    members_id BIGINT NOT NULL,
    `role` VARCHAR(255) NOT NULL,
    PRIMARY KEY (members_id, `role`),
    CONSTRAINT fk_members_roles_member
        FOREIGN KEY (members_id) REFERENCES members (members_id)
) ENGINE=InnoDB;

CREATE TABLE venues (
    venue_id BIGINT NOT NULL AUTO_INCREMENT,
    venue_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    detail_address VARCHAR(255) NULL,
    capacity BIGINT NOT NULL,
    PRIMARY KEY (venue_id)
) ENGINE=InnoDB;

CREATE TABLE genre (
    genre_id BIGINT NOT NULL AUTO_INCREMENT,
    genre_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (genre_id)
) ENGINE=InnoDB;

CREATE TABLE concerts (
    concert_id BIGINT NOT NULL AUTO_INCREMENT,
    venue_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    start_date DATETIME(6) NULL,
    end_date DATETIME(6) NULL,
    concert_name VARCHAR(255) NOT NULL,
    run_time DECIMAL(21, 0) NULL,
    status VARCHAR(255) NULL,
    PRIMARY KEY (concert_id),
    CONSTRAINT fk_concerts_venue
        FOREIGN KEY (venue_id) REFERENCES venues (venue_id),
    CONSTRAINT fk_concerts_genre
        FOREIGN KEY (genre_id) REFERENCES genre (genre_id)
) ENGINE=InnoDB;

CREATE TABLE performances (
    performance_id BIGINT NOT NULL AUTO_INCREMENT,
    concert_id BIGINT NOT NULL,
    performance_date DATETIME(6) NULL,
    booking_start DATETIME(6) NULL,
    booking_end DATETIME(6) NULL,
    status VARCHAR(255) NULL,
    PRIMARY KEY (performance_id),
    CONSTRAINT fk_performances_concert
        FOREIGN KEY (concert_id) REFERENCES concerts (concert_id)
) ENGINE=InnoDB;

CREATE TABLE seats_grade (
    seat_grade_id BIGINT NOT NULL AUTO_INCREMENT,
    grade_name VARCHAR(30) NOT NULL,
    description VARCHAR(255) NULL,
    price INT NOT NULL,
    PRIMARY KEY (seat_grade_id)
) ENGINE=InnoDB;

CREATE TABLE seats (
    seat_id BIGINT NOT NULL AUTO_INCREMENT,
    seat_grade_id BIGINT NOT NULL,
    row_label VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    PRIMARY KEY (seat_id),
    CONSTRAINT fk_seats_grade
        FOREIGN KEY (seat_grade_id) REFERENCES seats_grade (seat_grade_id)
) ENGINE=InnoDB;

CREATE TABLE performance_seats (
    performance_seat_id BIGINT NOT NULL AUTO_INCREMENT,
    performance_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    hold_token VARCHAR(36) NULL,
    hold_expires_at DATETIME(6) NULL,
    hold_member_id BIGINT NULL,
    PRIMARY KEY (performance_seat_id),
    UNIQUE KEY uk_performance_seat (performance_id, seat_id),
    KEY idx_performance_seats_hold_token (hold_token),
    CONSTRAINT fk_performance_seats_performance
        FOREIGN KEY (performance_id) REFERENCES performances (performance_id),
    CONSTRAINT fk_performance_seats_seat
        FOREIGN KEY (seat_id) REFERENCES seats (seat_id),
    CONSTRAINT fk_performance_seats_hold_member
        FOREIGN KEY (hold_member_id) REFERENCES members (members_id)
) ENGINE=InnoDB;

CREATE TABLE orders (
    orders_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    performance_id BIGINT NOT NULL,
    order_no VARCHAR(50) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (orders_id),
    UNIQUE KEY uk_orders_order_no (order_no),
    CONSTRAINT fk_orders_member
        FOREIGN KEY (member_id) REFERENCES members (members_id),
    CONSTRAINT fk_orders_performance
        FOREIGN KEY (performance_id) REFERENCES performances (performance_id)
) ENGINE=InnoDB;

CREATE TABLE orders_detail (
    order_detail_id BIGINT NOT NULL AUTO_INCREMENT,
    performance_seat_id BIGINT NULL,
    orders_id BIGINT NULL,
    status VARCHAR(30) NULL,
    price DECIMAL(12, 2) NULL,
    created_at DATETIME(6) NULL,
    PRIMARY KEY (order_detail_id),
    CONSTRAINT fk_orders_detail_seat
        FOREIGN KEY (performance_seat_id) REFERENCES performance_seats (performance_seat_id),
    CONSTRAINT fk_orders_detail_order
        FOREIGN KEY (orders_id) REFERENCES orders (orders_id)
) ENGINE=InnoDB;

CREATE TABLE payments (
    payment_id BIGINT NOT NULL AUTO_INCREMENT,
    orders_id BIGINT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_key VARCHAR(255) NULL,
    amount DECIMAL(12, 2) NULL,
    status VARCHAR(30) NULL,
    paid_at DATETIME(6) NULL,
    cancelled_at DATETIME(6) NULL,
    PRIMARY KEY (payment_id),
    CONSTRAINT fk_payments_order
        FOREIGN KEY (orders_id) REFERENCES orders (orders_id)
) ENGINE=InnoDB;

-- 3. Minimal users, one performance, and the agreed 80-seat layout.
START TRANSACTION;

INSERT INTO members
    (members_id, email, password, name, phone, status, created_at, updated_at)
VALUES
    (1, 'seat1@example.test', '$2a$10$VBd0dMI8Z05c7oYO8HXSWOUSdRAGeWHtbx.8Ga/.VJfdRswCeBnxC',
     '좌석테스트1', '01000000001', 0, NOW(6), NOW(6)),
    (2, 'seat2@example.test', '$2a$10$VBd0dMI8Z05c7oYO8HXSWOUSdRAGeWHtbx.8Ga/.VJfdRswCeBnxC',
     '좌석테스트2', '01000000002', 0, NOW(6), NOW(6));

INSERT INTO members_roles (members_id, `role`)
VALUES (1, 'USER'), (2, 'USER');

INSERT INTO venues (venue_id, venue_name, address, detail_address, capacity)
VALUES (1, '테스트 공연장', '테스트 주소', '1층 공연홀', 80);

INSERT INTO genre (genre_id, genre_name)
VALUES (1, '뮤지컬');

INSERT INTO concerts
    (concert_id, venue_id, genre_id, start_date, end_date, concert_name, status)
VALUES
    (1, 1, 1, '2030-01-01 00:00:00', '2030-01-31 23:59:59',
     '좌석·결제 테스트 공연', 'OPEN');

INSERT INTO performances
    (performance_id, concert_id, performance_date, booking_start, booking_end, status)
VALUES
    (1, 1, '2030-01-15 19:30:00', '2026-01-01 00:00:00',
     '2030-01-15 18:00:00', 'OPEN');

INSERT INTO seats_grade (seat_grade_id, grade_name, description, price)
VALUES
    (1, 'S', '1층 A·B열', 200000),
    (2, 'A', '1층 C·D열 / 2층 F열', 150000),
    (3, 'R', '1층 E열 / 2층 G·H열', 130000);

-- IDs are stable: A1=1, E10=50, F1=51, H10=80.
INSERT INTO seats (seat_id, seat_grade_id, row_label, seat_number)
SELECT (seat_rows.row_no - 1) * 10 + seat_nums.seat_no,
       CASE
           WHEN seat_rows.row_label IN ('A', 'B') THEN 1
           WHEN seat_rows.row_label IN ('C', 'D', 'F') THEN 2
           ELSE 3
       END,
       seat_rows.row_label,
       seat_nums.seat_no
FROM (
    SELECT 1 AS row_no, 'A' AS row_label UNION ALL
    SELECT 2, 'B' UNION ALL SELECT 3, 'C' UNION ALL SELECT 4, 'D' UNION ALL
    SELECT 5, 'E' UNION ALL SELECT 6, 'F' UNION ALL SELECT 7, 'G' UNION ALL
    SELECT 8, 'H'
) AS seat_rows
CROSS JOIN (
    SELECT 1 AS seat_no UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL
    SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
) AS seat_nums
ORDER BY seat_rows.row_no, seat_nums.seat_no;

INSERT INTO performance_seats
    (performance_seat_id, performance_id, seat_id, status)
SELECT seat_id, 1, seat_id, 'AVAILABLE'
FROM seats
ORDER BY seat_id;

COMMIT;

-- Orders, order details, and payments intentionally start empty; the API creates
-- PENDING records after a test user holds seats and creates an order.

-- 4. Quick checks
SELECT COUNT(*) AS seat_count FROM performance_seats WHERE performance_id = 1;
SELECT s.row_label, g.grade_name, COUNT(*) AS seats_in_row
FROM seats AS s
JOIN seats_grade AS g ON g.seat_grade_id = s.seat_grade_id
GROUP BY s.row_label, g.grade_name
ORDER BY s.row_label;
SELECT COUNT(*) AS initial_order_count FROM orders;
SELECT COUNT(*) AS initial_payment_count FROM payments;
