package com.jocata.oms.service.impl;


import com.jocata.oms.request.EmailRequest;
import com.jocata.oms.request.OrderForm;
import com.jocata.oms.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public String sendEmail(EmailRequest emailRequest){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRequest.getTo());
        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getMessage());
        mailSender.send(message);
        return "Email sent";
    }

    @Override
    public String sendEmailByMimeMessage(MultipartFile file, String to, String subject, OrderForm orderForm) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(Map.of(
                    "recipientName", orderForm.getCustomerName(),
                    "orderId", orderForm.getOrderId(),
                    "downloadLink", "WWW.GOOGLE.COM",
                    "products", orderForm.getOrderItems(),
                    "totalAmount", orderForm.getTotalPrice()
            ));
            String emailContent = templateEngine.process("template", context);

            helper.setText(emailContent, true);

            if (!file.isEmpty()) {
                InputStreamSource fileSource = new ByteArrayResource(file.getBytes());
                helper.addAttachment(file.getOriginalFilename(), fileSource, file.getContentType());
            } else {
                return "Error: No file uploaded!";
            }

            mailSender.send(message);
            return "Email sent successfully!";
        } catch (MessagingException | IOException e) {
            return "Error sending email: " + e.getMessage();
        }
    }


}
