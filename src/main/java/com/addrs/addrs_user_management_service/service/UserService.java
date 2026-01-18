package com.addrs.addrs_user_management_service.service;


import com.addrs.addrs_user_management_service.dao.*;
import com.addrs.addrs_user_management_service.entity.User;
import com.addrs.addrs_user_management_service.exceptionhandler.UserRegistartionException;
import com.addrs.addrs_user_management_service.exceptionhandler.UserUpdateException;
import com.addrs.addrs_user_management_service.dao.ResponseMessage;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ResponseMessage> registerUser(UserBody userBody) throws UserRegistartionException;

    ResponseEntity<ResponseMessage> registerAdmin(UserBody userBody) throws Exception;

    ResponseEntity<ResponseMessage> verifyUser(OtpBody otpBody) throws Exception;

    ResponseEntity<ResponseMessage> updateUserDetails(UserBody userBody) throws UserUpdateException;

    ResponseEntity<ResponseMessage> sendOtp(SendOtpBody sendOtpBody) throws Exception;

    ResponseEntity<ResponseMessage> forgottenPassword(ForgottenPasswordBody forgottenPasswordBody) throws Exception;

    ResponseEntity<ResponseMessage> resetPassword(ResetPasswordBody resetPasswordBody) throws Exception;

    ResponseEntity<ResponseMessage> resetEmail(ResetEmailBody resetEmailBody) throws Exception;

    User fetchUserDetailsFromDb(String userName) throws Exception;

}
