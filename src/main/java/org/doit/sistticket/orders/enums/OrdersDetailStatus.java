package org.doit.sistticket.orders.enums;

public enum OrdersDetailStatus {
	PENDING,		// 주문 생성, 결제 대기
	CONFIRMED,		// 결제 성공, 예약 확정
	CANCELLED,		// 주문 취소
	EXPIRED,		// 결제 유효 시간 만료
	CANCELREQUEST // 취소요청

}
