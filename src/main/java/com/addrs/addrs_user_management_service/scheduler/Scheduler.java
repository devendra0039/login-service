package com.addrs.addrs_user_management_service.scheduler;

import com.addrs.addrs_user_management_service.temp.TemporaryDataStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scheduler {

    @Autowired
    TemporaryDataStorage temporaryDataStorage;

    @Scheduled(fixedRate = 5*60000)
    public void removeOtpFromTempStorage() {
        log.info("Running scheduler to remove expired otp data from temporary storage in every 5 minute");
        temporaryDataStorage.removeExpiredOtpData();
    }
}
