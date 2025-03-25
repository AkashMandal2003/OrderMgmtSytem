package com.jocata.oms.controller;

import com.jocata.oms.request.EmailRequest;
import com.jocata.oms.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        String sendEmail = emailService.sendEmail(emailRequest);
        return ResponseEntity.ok(sendEmail);
    }


}
