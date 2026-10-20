package org.doit.sistticket.orders.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrdersCreateRequestDTO {

	// memberId는 프론트x , 컨트롤러에서 로그인한 회원정보를 사용할 것.
	private Long performanceId;
	private List<Long> performanceSeatIds;
	private String paymentMethod;
	private String holdToken;
}
