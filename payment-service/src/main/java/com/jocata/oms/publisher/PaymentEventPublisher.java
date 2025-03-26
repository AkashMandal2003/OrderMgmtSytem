package com.jocata.oms.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;

    }

    public void publishPaymentEvent(String payment) {

        kafkaTemplate.send("payment-topic", payment);

    }
}
