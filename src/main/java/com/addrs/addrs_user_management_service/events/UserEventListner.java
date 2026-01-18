package com.addrs.addrs_user_management_service.events;

import com.addrs.addrs_user_management_service.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventListner {

   @Autowired
   EmailService emailService;

    @EventListener
    public void handleUserVerificationEvent(UserVerificationEvent userVerificationEvent) {
        log.info("For emailId {} : Sending otp {} to user {} ",userVerificationEvent.email,userVerificationEvent.otp,userVerificationEvent.name);
        String messageBody = "Hi! " + userVerificationEvent.name + " your one time password (OTP) for Addrs account creation is " + userVerificationEvent.otp;
        emailService.sendOtp(userVerificationEvent.email,"Addrs One Time Password",messageBody);
    }
}
