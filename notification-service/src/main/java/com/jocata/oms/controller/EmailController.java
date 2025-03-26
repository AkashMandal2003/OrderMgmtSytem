package com.jocata.oms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocata.oms.request.EmailRequest;
import com.jocata.oms.request.OrderForm;
import com.jocata.oms.service.EmailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest){
        String sendEmail;
        try {
            sendEmail = emailService.sendEmail(emailRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(sendEmail);
    }

    @PostMapping(value = "/send-email-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestPart("order") String order
            ) {
        ObjectMapper mapper = new ObjectMapper();
        OrderForm orderForm;
        try {
            orderForm = mapper.readValue(order, OrderForm.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String sendEmail = emailService.sendEmailByMimeMessage(file,to,subject, orderForm);
        return ResponseEntity.ok(sendEmail);
    }


}
