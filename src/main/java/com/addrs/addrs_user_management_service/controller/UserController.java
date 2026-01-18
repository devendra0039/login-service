package com.addrs.addrs_user_management_service.controller;

import com.addrs.addrs_user_management_service.config.jwt.JwtUtils;
import com.addrs.addrs_user_management_service.dao.*;
import com.addrs.addrs_user_management_service.exceptionhandler.UserRegistartionException;
import com.addrs.addrs_user_management_service.exceptionhandler.UserUpdateException;
import com.addrs.addrs_user_management_service.service.UserService;
import com.addrs.addrs_user_management_service.dao.ResponseMessage;
import com.addrs.addrs_user_management_service.utility.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody UserBody userBody){
        try {
            log.info("For emailId {} : Creating User",userBody.getEmail());
            return userService.registerUser(userBody);
        } catch (UserRegistartionException | Exception e){
            log.error("For emailId {} : Error while creating User",userBody.getFirstName(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(e.getMessage());
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/register/admincredentials")
    public ResponseEntity<ResponseMessage> registerAdmin(@RequestBody UserBody userBody){
        try {
            log.info("For emailId {} : Creating Admin",userBody.getEmail());
            return userService.registerAdmin(userBody);
        } catch (Exception e){
            log.error("For emailId {} : Error while creating Admin",userBody.getFirstName(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(e.getMessage());
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody UserBody userBody){
        try {
            log.info("For emailId {} : Updating User",userBody.getEmail());
            return userService.updateUserDetails(userBody);
        } catch (UserUpdateException | Exception e){
            log.error("For emailId {} : Error while updating the User",userBody.getEmail(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(e.getMessage());
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/verification")
    public ResponseEntity<ResponseMessage> verifyUser(@RequestBody OtpBody otpBody){
        try {
            log.info("For emailId {} : Verifying User",otpBody.getEmail());
            return userService.verifyUser(otpBody);
        } catch (Exception e){
            log.error("For emailId {} : Error while Verifying the User",otpBody.getEmail(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Error while Verifying the User");
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/send-otp")
    public  ResponseEntity<ResponseMessage> sendOtp(@RequestBody SendOtpBody sendOtpBody) {
        try {
            log.info("For emailId {} : Recevied Request to send Otp", sendOtpBody.getEmail());
             return userService.sendOtp(sendOtpBody);
        } catch (Exception e) {
            ResponseMessage responseMessage = new ResponseMessage();
            log.error("For emailId {} : Error while resending Otp", sendOtpBody.getEmail(),e);
            responseMessage.setMessage("Error while sending otp");
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseMessage> forgottenPassword(@RequestBody ForgottenPasswordBody forgottenPasswordBody) {
        try {
            log.info("For emailId {} : Recevied Request to change Password", forgottenPasswordBody.getEmail());
            return userService.forgottenPassword(forgottenPasswordBody);
        }catch (Exception e){
            log.error("For emailId {} : Error while changing password", forgottenPasswordBody.getEmail(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Error while changing password");
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseMessage> resetPassword(@RequestBody ResetPasswordBody resetPasswordBody) {
        try {
            log.info("For emailId {} : Recevied Request to reset Password", resetPasswordBody.getEmail());
            return userService.resetPassword(resetPasswordBody);
        }catch (Exception e){
            log.error("For emailId {} : Error while reseting password", resetPasswordBody.getEmail(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Error while reseting password");
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @PostMapping("/reset-email")
    public ResponseEntity<ResponseMessage> resetEmail(@RequestBody ResetEmailBody resetEmailBody) {
        try {
            log.info("For emailId {} : Recevied Request to change email", resetEmailBody.getCurrentEmail());
            return userService.resetEmail(resetEmailBody);
        }catch (Exception e){
            log.error("For emailId {} : Error while reseting password", resetEmailBody.getCurrentEmail(),e);
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Error while reseting password");
//            responseMessage.setStatus(StatusCode.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(responseMessage);
        }
    }

    @GetMapping("/getUser")
    public String getUserAccess(@RequestParam("token") String token) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication.getName();
        return jwtUtils.extractUsername(token);
    }
}
