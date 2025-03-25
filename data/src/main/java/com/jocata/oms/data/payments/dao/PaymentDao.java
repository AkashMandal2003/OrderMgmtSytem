package com.jocata.oms.data.payments.dao;

import com.jocata.oms.datamodel.payments.entity.PaymentDetails;

public interface PaymentDao {

    PaymentDetails createPayment(PaymentDetails paymentDetails);
    PaymentDetails getPayment(Integer paymentId);
    PaymentDetails findPaymentByOrderId(Integer orderId);
    PaymentDetails updatePayment(PaymentDetails paymentDetails);

}
