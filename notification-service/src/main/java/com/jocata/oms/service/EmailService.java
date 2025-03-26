package com.jocata.oms.service;

import com.jocata.oms.request.EmailRequest;
import com.jocata.oms.request.OrderForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmailService {

    String sendEmail(EmailRequest emailRequest) throws IOException;

    String sendEmailByMimeMessage(MultipartFile file, String to, String subject, OrderForm orderForm);
}
