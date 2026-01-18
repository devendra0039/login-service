package com.addrs.addrs_user_management_service.events;

import com.addrs.addrs_user_management_service.dao.OtpBody;
import com.addrs.addrs_user_management_service.temp.TemporaryDataStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@Slf4j
public class UserEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    TemporaryDataStorage temporaryDataStorage;


    public void publishUserVerificationEvent(String email, String name){
        try {
            Random rnd = new Random();
            int otp = rnd.nextInt(900000)+100000;
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
            log.info("For emailId {} : Otp generated for user {} is {}",email,name,otp);
            applicationEventPublisher.publishEvent(new UserVerificationEvent(this, email, name, otp));
            OtpBody otpModel = OtpBody.builder()
                    .otp(otp)
                    .email(email)
                    .expiryTime(expiryTime)
                    .build();
            log.info("For emailId {} : Checking if otp is present in temporary storage ",email);
            OtpBody retrivedData = temporaryDataStorage.getOtpData(email);
            if(retrivedData != null){
                log.info("For emailId {} : Removing Otp is present in temporary storage before adding new otp in temporary storage",email);
                temporaryDataStorage.removeOtpData(email);
            }
            log.info("For emailId {} : Saving otp {} in temporary storage ",email,otp);
            temporaryDataStorage.addOtpData(otpModel);
        } catch (Exception e) {
            log.error("For emailId {} : Error while sending otp to user {}",email,name,e);
        }
    }
}
