package com.addrs.addrs_user_management_service.service;

import com.addrs.addrs_user_management_service.dao.*;
import com.addrs.addrs_user_management_service.entity.User;
import com.addrs.addrs_user_management_service.events.UserEventPublisher;
import com.addrs.addrs_user_management_service.exceptionhandler.UserRegistartionException;
import com.addrs.addrs_user_management_service.exceptionhandler.UserUpdateException;
import com.addrs.addrs_user_management_service.repository.UserRepository;
import com.addrs.addrs_user_management_service.temp.TemporaryDataStorage;
import com.addrs.addrs_user_management_service.utility.Constants;
import com.addrs.addrs_user_management_service.dao.ResponseMessage;
import com.addrs.addrs_user_management_service.utility.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserEventPublisher userEventPublisher;

    @Autowired
    TemporaryDataStorage temporaryDataStorage;

    @Override
    public ResponseEntity<ResponseMessage> registerUser(UserBody userBody) throws UserRegistartionException {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            log.info("For emailId {} : Checking if user is already present in DB", userBody.getEmail());
            User userDetails = fetchUserDetailsFromDb(userBody.getEmail());
            if (userDetails == null) {
                log.info("For emailId {} : User is not present ", userBody.getEmail());
                addUserDetailsInDb(userBody, Constants.USER, false);
                log.info("For emailId {} : Publishing user verification event", userBody.getEmail());
                userEventPublisher.publishUserVerificationEvent(userBody.getEmail(), userBody.getFirstName());
                responseMessage.setMessage("Succefully user got created please verify your emailId : " + userBody.getEmail() + " to activate");
//                responseMessage.setStatus(StatusCode.CREATED);
                return ResponseEntity.status(StatusCode.CREATED).body(responseMessage);
            } else if (!userDetails.getActive()) {
                log.info("For emailId {} : User is present but not active", userBody.getEmail());
                updateUserDetailsInDb(userBody, userDetails);
                log.info("For emailId {} : Publishing user verification event", userBody.getEmail());
                userEventPublisher.publishUserVerificationEvent(userBody.getEmail(), userBody.getFirstName());
                responseMessage.setMessage("Succefully user got created please verify your emailId : " + userBody.getEmail() + " to activate");
//                responseMessage.setStatus(StatusCode.CREATED);
                return ResponseEntity.status(StatusCode.CREATED).body(responseMessage);
            } else {
                log.info("For emailId {} : User Already exist with this emailId {} ", userBody.getEmail(), userBody.getEmail());
                responseMessage.setMessage("User Already exist with this emailId " + userBody.getEmail());
//                responseMessage.setStatus(StatusCode.FORBIDDEN);
                return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
            }

        } catch (Exception e) {
            log.error("For emailId {} : Error while creating User {}", userBody.getEmail(), userBody.getFirstName());
            throw new UserRegistartionException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> registerAdmin(UserBody userBody) throws Exception {
        ResponseMessage responseMessage = new ResponseMessage();
        log.info("For emailId {} : Checking if Admin is already present in DB", userBody.getEmail());
        User userDetails = fetchUserDetailsFromDb(userBody.getEmail());
        if (userDetails == null) {
            log.info("For emailId {} : Admin is not present ", userBody.getEmail());
            addUserDetailsInDb(userBody, Constants.ADMIN, true);
            responseMessage.setMessage("Succefully Admin got created");
//            responseMessage.setStatus(StatusCode.CREATED);
            return ResponseEntity.status(StatusCode.CREATED).body(responseMessage);
        } else if (!userDetails.getActive()) {
            log.info("For emailId {} : Admin is present but not active", userBody.getEmail());
            updateUserDetailsInDb(userBody, userDetails);
            responseMessage.setMessage("Succefully Admin got updated");
//            responseMessage.setStatus(StatusCode.ACCEPTED);
            return ResponseEntity.status(StatusCode.ACCEPTED).body(responseMessage);
        } else {
            log.info("For emailId {} : Admin Already exist with this emailId {} ", userBody.getEmail(), userBody.getEmail());
            responseMessage.setMessage("Admin Already exist with this emailId " + userBody.getEmail());
//            responseMessage.setStatus(StatusCode.FORBIDDEN);
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> verifyUser(OtpBody otpBody) throws Exception {
        ResponseMessage responseMessage = new ResponseMessage();
        User user = fetchUserDetailsFromDb(otpBody.getEmail());
        log.info("For emailId {} : Fetched User from DB for verification", otpBody.getEmail());
        int generatedOTP = temporaryDataStorage.getOtpData(otpBody.getEmail()).getOtp();
        log.info("For emailId {} : Generated OTP is {} and Received OTP from user is {}", otpBody.getEmail(), generatedOTP, otpBody.getOtp());
        if (generatedOTP == otpBody.getOtp()) {
            user.setActive(true);
            userRepository.save(user);
            log.info("For emailId {} : User verified successfully", otpBody.getEmail());
            responseMessage.setMessage("User verified successfully");
//            responseMessage.setStatus(StatusCode.OK);
            return ResponseEntity.status(StatusCode.OK).body(responseMessage);
        } else {
            user.setActive(false);
            log.info("For emailId {} : User verification unsuccessful", otpBody.getEmail());
            responseMessage.setMessage("Invalid OTP");
//            responseMessage.setStatus(StatusCode.FORBIDDEN);
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> updateUserDetails(UserBody userBody) throws UserUpdateException {
        try {
            ResponseMessage responseMessage = new ResponseMessage();
            User userDetails = fetchUserDetailsFromDb(userBody.getEmail());
            log.info("For emailId {} : Fetched User from DB for update", userBody.getEmail());
            updateUserDetailsInDb(userBody, userDetails);
            log.info("For emailId {} : User got updated successfully", userBody.getEmail());
            responseMessage.setMessage("User got updated successfully");
//            responseMessage.setStatus(StatusCode.ACCEPTED);
            return ResponseEntity.status(StatusCode.ACCEPTED).body(responseMessage);
        } catch (Exception e) {
            log.error("For emailId {} : Error while updating the User", userBody.getEmail(), e);
            throw new UserUpdateException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> sendOtp(SendOtpBody sendOtpBody) throws Exception {
        User userDetails = fetchUserDetailsFromDb(sendOtpBody.getEmail());
        log.info("For emailId {} : Sending OTP", sendOtpBody.getEmail());
        userEventPublisher.publishUserVerificationEvent(sendOtpBody.getEmail(), userDetails.getFirstName()+" "+userDetails.getLastName());
        log.info("For emailId {} : OTP Sent successfully", sendOtpBody.getEmail());
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("Succefully OTP sent to " + sendOtpBody.getEmail());
        return ResponseEntity.status(StatusCode.OK).body(responseMessage);
    }

    @Override
    public ResponseEntity<ResponseMessage> forgottenPassword(ForgottenPasswordBody forgottenPasswordBody) throws Exception {
        User userDetails = fetchUserDetailsFromDb(forgottenPasswordBody.getEmail());
        ResponseMessage responseMessage = new ResponseMessage();
        log.info("For emailId {} : Fetched User from DB for password reset", forgottenPasswordBody.getEmail());
        if(forgottenPasswordBody.getOtp() == temporaryDataStorage.getOtpData(forgottenPasswordBody.getEmail()).getOtp()) {
            log.info("For emailId {} : OTP matched for password reset", forgottenPasswordBody.getEmail());
            userDetails.setPassword(passwordEncoder.encode(forgottenPasswordBody.getNewPassword()));
            userRepository.save(userDetails);
            log.info("For emailId {} : Password reset successfully", forgottenPasswordBody.getEmail());
            responseMessage.setMessage("Password reset successfully");
            return ResponseEntity.status(StatusCode.OK).body(responseMessage);
        }else {
            responseMessage.setMessage("OTP did not match for password reset");
            log.info("For emailId {} : OTP did not match for password reset", forgottenPasswordBody.getEmail());
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }
    }

    @Override
    public ResponseEntity<ResponseMessage> resetPassword(ResetPasswordBody forgottenPasswordBody) throws Exception {
        User userDetails = fetchUserDetailsFromDb(forgottenPasswordBody.getEmail());
        log.info("For emailId {} : Fetched User from DB for password reset", forgottenPasswordBody.getEmail());
        ResponseMessage responseMessage = new ResponseMessage();
        if(passwordEncoder.matches(forgottenPasswordBody.getCurrentPassword(), userDetails.getPassword())) {
            log.info("For emailId {} : Current password matched for password reset", forgottenPasswordBody.getEmail());
            userDetails.setPassword(passwordEncoder.encode(forgottenPasswordBody.getNewPassword()));
            userRepository.save(userDetails);
            log.info("For emailId {} : Password reset successfully", forgottenPasswordBody.getEmail());
            responseMessage.setMessage("Password reset successfully");
//            responseMessage.setStatus(StatusCode.ACCEPTED);
            return ResponseEntity.status(StatusCode.ACCEPTED).body(responseMessage);
        }else{
            log.info("For emailId {} : Current password did not match for password reset", forgottenPasswordBody.getEmail());
            responseMessage.setMessage("Current password did not match for password reset");
//            responseMessage.setStatus(StatusCode.FORBIDDEN);
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }

    }

    @Override
    public ResponseEntity<ResponseMessage> resetEmail(ResetEmailBody resetEmailBody) throws Exception {
        User userDetails = fetchUserDetailsFromDb(resetEmailBody.getCurrentEmail());
        log.info("For emailId {} : Fetched User from DB for email reset", resetEmailBody.getCurrentEmail());
        ResponseMessage responseMessage = new ResponseMessage();
        if(resetEmailBody.getOtp() == temporaryDataStorage.getOtpData(resetEmailBody.getCurrentEmail()).getOtp()) {
            log.info("For emailId {} : OTP matched for email reset", resetEmailBody.getCurrentEmail());
            userDetails.setEmail(resetEmailBody.getNewEmail());
            userRepository.save(userDetails);
            log.info("For emailId {} : Email changed successfully to new email {}", resetEmailBody.getCurrentEmail(), resetEmailBody.getNewEmail());
            responseMessage.setMessage("Email changed successfully");
//            responseMessage.setStatus(StatusCode.ACCEPTED);
            return ResponseEntity.status(StatusCode.ACCEPTED).body(responseMessage);
        }else {
            log.info("For emailId {} : OTP did not match for email reset", resetEmailBody.getCurrentEmail());
            responseMessage.setMessage("OTP did not match for email reset");
//            responseMessage.setStatus(StatusCode.FORBIDDEN);
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }
    }

    private void addUserDetailsInDb(UserBody userBody, String type, boolean status) {
        User user = User.builder()
                .firstName(userBody.getFirstName())
                .lastName(userBody.getLastName())
                .email(userBody.getEmail())
                .gender(userBody.getGender())
                .dateOfBirth(userBody.getDateOfBirth())
                .mobileNumber(userBody.getMobileNumber())
                .password(passwordEncoder.encode(userBody.getPassword()))
                .role(type)
                .active(status)
                .build();

        userRepository.save(user);
        log.info("For emailId {} : User/Admin details saved successfully into DB with user id {} and user name {} {}", user.getEmail(), user.getId(), user.getFirstName(), user.getLastName());
    }

    private void updateUserDetailsInDb(UserBody userBody, User user) {
        user.setFirstName(userBody.getFirstName());
        user.setLastName(userBody.getLastName());
        user.setGender(userBody.getGender());
        user.setDateOfBirth(userBody.getDateOfBirth());
        user.setMobileNumber(userBody.getMobileNumber());
        userRepository.save(user);
        log.info("For emailId {} : User/Admin details updated successfully into DB with user id {} and user name {} {}", user.getEmail(), user.getId(), user.getFirstName(), user.getLastName());
    }

    @Override
    public User fetchUserDetailsFromDb(String email) throws Exception {
        log.info("For emailId {} : Fetching user details from DB", email);
        try {
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            log.error("For emailId {} : Error while fetching user details from DB", email, e);
            throw new Exception(e);
        }
    }
}

