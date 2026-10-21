package org.doit.sistticket.common.exception;

import org.doit.sistticket.common.dto.ApiErrorResponse;
import org.doit.sistticket.payments.controller.PaymentsController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = PaymentsController.class)
public class PaymentExceptionHandler {

	// 잘못된 요청 또는 조회할 정보가 없는 경우
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
			IllegalArgumentException e){
		ApiErrorResponse response = new ApiErrorResponse(
				"INVALID_REQUEST",
				e.getMessage()
				);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	// 현재 상태에서 처리할 수 없는 요청
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalState(
			IllegalStateException e){
		ApiErrorResponse response = new ApiErrorResponse(
				"INVALID_STATE"
				, e.getMessage() 
				);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		
	}
	
	// 서비스에서 발생한 HTTP 상태 코드가 지정된 예외
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(
			ResponseStatusException e){
		ApiErrorResponse response = new ApiErrorResponse(
				"REQUEST_FAILED",
				e.getReason() == null ? "요청을 처리할 수 없습니다." : e.getMessage()
					);
		return ResponseEntity.status(e.getStatusCode()).body(response);
	}
	
	
	
} // class
