package com.jocata.oms.publisher;
 
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;
 
@Service
public class OrderEventPublisher {
 
    private final KafkaTemplate<String, String> kafkaTemplate;
 
    public OrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;

    }
 
    public void publishOrderEvent(String order) {

        kafkaTemplate.send("order-topic", order);

    }

    public void publishOrderEventAfterPayment(String orderId) {

        kafkaTemplate.send("order-payment-topic", orderId);

    }

}
 