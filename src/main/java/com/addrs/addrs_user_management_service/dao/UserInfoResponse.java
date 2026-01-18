package com.addrs.addrs_user_management_service.dao;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class UserInfoResponse {
    private Long id;
    private String email;
    private String fullName;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private Long mobileNumber;
    private List<String> roles;
}
