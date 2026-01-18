package com.addrs.addrs_user_management_service.temp;

import com.addrs.addrs_user_management_service.dao.OtpBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@ApplicationScope
@Slf4j
public class TemporaryDataStorage {
    private HashSet<OtpBody> otpData = new HashSet<>();

    public void addOtpData(OtpBody otpModel){
        otpData.add(otpModel);
    }

    public OtpBody getOtpData(String email){
        log.info("For emailId {} : Searching otp in temporary storage ",email);
        return otpData.stream().filter(otpModel -> otpModel.getEmail().equals(email)).findFirst().orElse(null);
        // Traditional way of fetching data from temporary storage
//        for(OtpModel otpModel : otpData){
//            if(otpModel.getEmail().equals(email)){
//                log.info("For emailId {} : Found entry in temporary storage {} ",email,otpModel);
//                return otpModel;
//            }
//        }
//        return null;
    }

    public void removeOtpData(String email){
        otpData.remove(getOtpData(email));
    }

    public void removeExpiredOtpData(){
        log.info("Befor Removing expired otp data from temporary storage {}",otpData);
        otpData.removeIf(otpBody -> otpBody.getExpiryTime().isBefore(LocalDateTime.now()));
        log.info("After Removing expired otp data from temporary storage {}",otpData);
    }
}
