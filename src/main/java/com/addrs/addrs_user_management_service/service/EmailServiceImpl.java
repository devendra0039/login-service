package com.addrs.addrs_user_management_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender emailSender;
    @Override
    public void sendOtp(String receiverEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiverEmail);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }
}