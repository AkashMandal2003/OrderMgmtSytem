package com.jocata.oms.service;

import com.jocata.oms.request.EmailRequest;

public interface EmailService {

    String sendEmail(EmailRequest emailRequest);

    String sendEmailByMimeMessage(EmailRequest emailRequest);
}
