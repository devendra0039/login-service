package com.addrs.addrs_user_management_service.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordBody {
    private String email;
    private String currentPassword;
    private String newPassword;
}
