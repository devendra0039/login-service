package com.addrs.addrs_user_management_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${com.addrs-user-management-service.mail.username}")
    String userName;
    @Value("${com.addrs-user-management-service.mail.password}")
    String password;

    @Value("${com.addrs-user-management-service.mail.host}")
    String host;

    @Value("${com.addrs-user-management-service.mail.port}")
    int port;

    @Value("${com.addrs-user-management-service.mail.protocol}")
    String protocol;
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(userName);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.debug", true);
        return mailSender;
    }

}
