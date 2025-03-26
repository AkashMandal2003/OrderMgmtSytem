package com.jocata.oms.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocata.oms.data.order.OrderDao;
import com.jocata.oms.datamodel.orders.entity.OrderDetails;
import com.jocata.oms.datamodel.orders.enums.OrderStatus;
import com.jocata.oms.datamodel.payments.form.PaymentForm;
import com.jocata.oms.publisher.OrderEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderPaymentEventConsumer {

    private final OrderDao orderDao;
    private final OrderEventPublisher orderEventPublisher;

    public OrderPaymentEventConsumer(OrderDao orderDao, OrderEventPublisher orderEventPublisher) {
        this.orderDao = orderDao;
        this.orderEventPublisher = orderEventPublisher;
    }

    @KafkaListener(topics = "payment-topic", groupId = "orders-group")
    public void processOrderConfirmation(String payment) {
        ObjectMapper mapper = new ObjectMapper();
        PaymentForm paymentForm;
        try {
            paymentForm = mapper.readValue(payment, PaymentForm.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        OrderDetails orderById = orderDao.getOrderById(Integer.valueOf(paymentForm.getOrderId()));
        orderById.setOrderStatus(OrderStatus.CONFIRMED);

        OrderDetails updatedOrder = orderDao.updateOrder(orderById);

        //kafka publisher
        orderEventPublisher.publishOrderEventAfterPayment(String.valueOf(updatedOrder.getOrderId()));


        System.out.println("Order Payment Updated with order id:" + updatedOrder.getOrderId());
    }
}
