package com.addrs.addrs_user_management_service.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetEmailBody {
    private String currentEmail;
    private String newEmail;
    private int otp;
}
