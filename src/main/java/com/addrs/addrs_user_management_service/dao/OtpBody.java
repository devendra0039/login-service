package com.addrs.addrs_user_management_service.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpBody {
    private int otp;
    private String email;
    private LocalDateTime expiryTime;
}
