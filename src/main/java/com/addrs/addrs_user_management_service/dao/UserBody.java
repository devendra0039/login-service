package com.addrs.addrs_user_management_service.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBody {
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private Long mobileNumber;
    private String password;
}
