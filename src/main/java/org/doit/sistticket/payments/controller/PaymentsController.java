
package org.doit.sistticket.payments.controller;

import org.doit.sistticket.payments.dto.PaymentCompleteRequest;
import org.doit.sistticket.payments.dto.PaymentResponse;
import org.doit.sistticket.payments.service.PaymentsService;
import org.doit.sistticket.security.CustomerUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;

    // 모의 결제 성공 처리
    @PostMapping("/{paymentId}/complete")
    public PaymentResponse completePayment(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentCompleteRequest request,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();

        paymentsService.completePayment(
                paymentId,
                request.paymentKey(),
                memberId
        );

        return paymentsService.getPaymentResponse(paymentId, memberId);
    }

    // 모의 결제 실패 처리
    @PostMapping("/{paymentId}/fail")
    public PaymentResponse failPayment(
            @PathVariable("paymentId") Long paymentId,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();

        paymentsService.failPayment(paymentId, memberId);

        return paymentsService.getPaymentResponse(paymentId, memberId);
    }

    // 실패한 결제 재시도
    @PostMapping("/{paymentId}/retry")
    public PaymentResponse retryPayment(
            @PathVariable("paymentId") Long paymentId,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();

        paymentsService.retryPayment(paymentId, memberId);

        return paymentsService.getPaymentResponse(paymentId, memberId);
    }
}
