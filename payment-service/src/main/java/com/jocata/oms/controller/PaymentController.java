package com.jocata.oms.controller;

import com.jocata.oms.datamodel.payments.form.PaymentForm;
import com.jocata.oms.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentForm> createPayment(@RequestBody PaymentForm paymentForm) {
        PaymentForm paymentDetails = paymentService.generatePaymentDetails(paymentForm);
        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentForm> getPaymentByOrderId(@PathVariable Integer orderId) {
        PaymentForm paymentDetailsByOrderId = paymentService.getPaymentDetailsByOrderId(orderId);
        return new ResponseEntity<>(paymentDetailsByOrderId, HttpStatus.OK);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentForm> getPaymentById(@PathVariable Integer paymentId) {
        PaymentForm paymentDetailsById = paymentService.getPaymentDetailsById(paymentId);
        return new ResponseEntity<>(paymentDetailsById, HttpStatus.OK);
    }

    @PutMapping("/{paymentId}/update")
    public ResponseEntity<PaymentForm> updatePayment(@PathVariable Integer paymentId, @RequestBody PaymentForm paymentForm) {
        PaymentForm paymentDetails = paymentService.updatePaymentDetails(paymentId, paymentForm);
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }

}
