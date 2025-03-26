package com.jocata.oms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocata.oms.data.payments.dao.PaymentDao;
import com.jocata.oms.datamodel.payments.entity.PaymentDetails;
import com.jocata.oms.datamodel.payments.enums.PaymentMethod;
import com.jocata.oms.datamodel.payments.enums.PaymentStatus;
import com.jocata.oms.datamodel.payments.form.PaymentForm;
import com.jocata.oms.publisher.PaymentEventPublisher;
import com.jocata.oms.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;
    private final PaymentEventPublisher paymentEventPublisher;
    private final ObjectMapper objectMapper;

    public PaymentServiceImpl(PaymentDao paymentDao, PaymentEventPublisher paymentEventPublisher, ObjectMapper objectMapper) {
        this.paymentDao = paymentDao;
        this.paymentEventPublisher = paymentEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentForm generatePaymentDetails(PaymentForm paymentForm) {

        PaymentDetails paymentDetails = paymentFormToPaymentDetails(paymentForm);
        PaymentDetails savedPayment = paymentDao.createPayment(paymentDetails);

        PaymentForm paymentResponse = paymentDetailsToPaymentForm(savedPayment);
        String paymentJson;
        try {
            paymentJson=objectMapper.writeValueAsString(paymentResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //kafka publisher
        paymentEventPublisher.publishPaymentEvent(paymentJson);

        return paymentResponse;

    }

    @Override
    public PaymentForm getPaymentDetailsByOrderId(Integer orderId) {

        PaymentDetails paymentByOrderId = paymentDao.findPaymentByOrderId(orderId);
        return paymentDetailsToPaymentForm(paymentByOrderId);
    }

    @Override
    public PaymentForm getPaymentDetailsById(Integer paymentId) {

        PaymentDetails payment = paymentDao.getPayment(paymentId);
        return paymentDetailsToPaymentForm(payment);

    }

    @Override
    public PaymentForm updatePaymentDetails(Integer paymentId, PaymentForm paymentForm) {

        PaymentDetails payment = paymentDao.getPayment(paymentId);
        payment.setPaymentStatus(PaymentStatus.valueOf(paymentForm.getPaymentStatus()));
        PaymentDetails paymentDetails = paymentDao.updatePayment(payment);

        return paymentDetailsToPaymentForm(paymentDetails);
    }

    private PaymentDetails paymentFormToPaymentDetails(PaymentForm paymentForm) {

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setOrderId(Integer.valueOf(paymentForm.getOrderId()));
        paymentDetails.setPaymentMethod(PaymentMethod.valueOf(paymentForm.getPaymentMethod()));
        paymentDetails.setPaymentStatus(PaymentStatus.valueOf(paymentForm.getPaymentStatus()));
        paymentDetails.setTransactionId(UUID.randomUUID().toString());
        paymentDetails.setAmount(new BigDecimal(paymentForm.getAmount()));

        return paymentDetails;
    }

    private PaymentForm paymentDetailsToPaymentForm(PaymentDetails paymentDetails) {

        PaymentForm paymentForm = new PaymentForm();
        paymentForm.setOrderId(String.valueOf(paymentDetails.getOrderId()));
        paymentForm.setPaymentMethod(paymentDetails.getPaymentMethod().toString());
        paymentForm.setPaymentStatus(paymentDetails.getPaymentStatus().toString());
        paymentForm.setTransactionId(paymentDetails.getTransactionId());
        paymentForm.setAmount(paymentDetails.getAmount().toString());
        paymentForm.setPaymentDate(String.valueOf(paymentDetails.getPaymentDate()));

        return paymentForm;
    }

}
