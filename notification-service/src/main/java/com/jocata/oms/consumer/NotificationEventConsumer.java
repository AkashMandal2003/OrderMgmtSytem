package com.jocata.oms.consumer;
 
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocata.oms.request.EmailRequest;
import com.jocata.oms.request.OrderForm;
import com.jocata.oms.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NotificationEventConsumer {

    private final EmailService emailService;

    public NotificationEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void processNotification(String order) {
        ObjectMapper mapper = new ObjectMapper();
        OrderForm orderForm;
        try {
            orderForm = mapper.readValue(order, OrderForm.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        emailService.sendEmailByMimeMessage(null,"amiakash956482@gmail.com","Order Details",orderForm);
        System.out.println("Email sent with order id:" + orderForm.getOrderId());
    }


    @KafkaListener(topics = "order-payment-topic", groupId = "notification-group")
    public void processNotificationAfterOrder(String orderId) {

        EmailRequest emailRequest=new EmailRequest();

        emailRequest.setTo("amiakash956482@gmail.com");
        emailRequest.setSubject("Order Confirmation");
        emailRequest.setMessage("Your payment has been confirmed and order has been confirmed having order id "+orderId);

        try {
            emailService.sendEmail(emailRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Email sent with order id:" + orderId);
    }
}