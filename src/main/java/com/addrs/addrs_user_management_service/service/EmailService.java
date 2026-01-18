package com.addrs.addrs_user_management_service.service;

public interface EmailService {
    public void sendOtp(String email, String subject, String mesage);
}
