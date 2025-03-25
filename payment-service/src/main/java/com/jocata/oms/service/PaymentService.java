package com.jocata.oms.service;

import com.jocata.oms.datamodel.payments.form.PaymentForm;

public interface PaymentService {

    PaymentForm generatePaymentDetails(PaymentForm PaymentForm);

    PaymentForm getPaymentDetailsByOrderId(Integer orderId);

    PaymentForm getPaymentDetailsById(Integer paymentId);

    PaymentForm updatePaymentDetails(Integer paymentId, PaymentForm paymentForm);

}
