package com.addrs.addrs_user_management_service.events;
import org.springframework.context.ApplicationEvent;


public class UserVerificationEvent extends ApplicationEvent{
String email;
String name;
int otp;

    public UserVerificationEvent(Object source, String email, String name, int otp) {
        super(source);
        this.email = email;
        this.name = name;
        this.otp = otp;
    }
}


