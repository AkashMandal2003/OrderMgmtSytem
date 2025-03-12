package com.jocata.oms.data.payments.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.payments.dao.PaymentDao;
import com.jocata.oms.datamodel.payments.PaymentDetails;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDaoImpl implements PaymentDao {

    private final HibernateConfig hibernateConfig;

    public PaymentDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public PaymentDetails createPayment(PaymentDetails paymentDetails) {
        return hibernateConfig.saveEntity(paymentDetails);
    }

    @Override
    public PaymentDetails getPayment(Integer paymentId) {
        return hibernateConfig.findEntityById(PaymentDetails.class, paymentId);
    }

    @Override
    public PaymentDetails findPaymentByOrderId(Integer orderId) {
        return hibernateConfig.findEntityByCriteria(PaymentDetails.class,"orderId",orderId);
    }

    @Override
    public PaymentDetails updatePayment(PaymentDetails paymentDetails) {
        return hibernateConfig.updateEntity(paymentDetails);
    }
}
