package com.addrs.addrs_user_management_service.controller;


import com.addrs.addrs_user_management_service.config.jwt.JwtUtils;
import com.addrs.addrs_user_management_service.dao.ResponseMessage;
import com.addrs.addrs_user_management_service.dao.SignInBody;
import com.addrs.addrs_user_management_service.dao.UserInfoResponse;
import com.addrs.addrs_user_management_service.entity.User;
import com.addrs.addrs_user_management_service.service.UserService;
import com.addrs.addrs_user_management_service.utility.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody SignInBody signInBody) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInBody.getUserName(), signInBody.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetail customUserDetails = (CustomUserDetail) authentication.getPrincipal();
            User userDetails = userService.fetchUserDetailsFromDb(customUserDetails.getUsername());
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(customUserDetails);
            List<String> roles = customUserDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                    .id(userDetails.getId())
                    .fullName(customUserDetails.getFullname())
                    .email(customUserDetails.getUsername())
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .dateOfBirth(userDetails.getDateOfBirth())
                    .mobileNumber(userDetails.getMobileNumber())
                    .gender(userDetails.getGender())
                    .roles(roles)
                    .build();

            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Sucsessfully User Loged In");
            responseMessage.setData(userInfoResponse);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(responseMessage);
        } catch (Exception e) {
            log.error("For emailId {} : Error while validating User", signInBody.getUserName(), e);
            UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                    .id(null)
                    .fullName(null)
                    .email(null)
                    .firstName(null)
                    .lastName(null)
                    .dateOfBirth(null)
                    .mobileNumber(null)
                    .gender(null)
                    .roles(null)
                    .build();
            ResponseMessage responseMessage = new ResponseMessage();
            String rspMsg = e.getMessage().equalsIgnoreCase("Bad credentials") ? "Bad credentials" : "Error while validating User";
            responseMessage.setMessage(rspMsg);
            responseMessage.setData(userInfoResponse);
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }
    }

    @GetMapping("/getUserDetails")
    public ResponseEntity<ResponseMessage> registerUser(HttpServletRequest request) {
        try {
            String token = jwtUtils.getToken(request);
            String username = jwtUtils.extractUsername(token);
            User userDetails = userService.fetchUserDetailsFromDb(username);
            Long id = userDetails.getId();
            String fullName = userDetails.getFirstName() + " " + userDetails.getLastName();
            List<String> roles = new ArrayList<>();
            roles.add(userDetails.getRole());
            UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                    .id(id)
                    .fullName(fullName)
                    .email(username)
                    .firstName(userDetails.getFirstName())
                    .lastName(userDetails.getLastName())
                    .dateOfBirth(userDetails.getDateOfBirth())
                    .mobileNumber(userDetails.getMobileNumber())
                    .gender(userDetails.getGender())
                    .roles(roles)
                    .build();
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Succefully fetched the UserDetails");
            responseMessage.setData(userInfoResponse);
            return ResponseEntity.ok().body(responseMessage);
        } catch (Exception e) {
            log.error("For emailId {} : Error while validating cookie", e);
            UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                    .id(null)
                    .fullName(null)
                    .email(null)
                    .firstName(null)
                    .lastName(null)
                    .dateOfBirth(null)
                    .mobileNumber(null)
                    .gender(null)
                    .roles(null)
                    .build();
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Error while fetching user details");
            responseMessage.setData(userInfoResponse);
            return ResponseEntity.status(StatusCode.FORBIDDEN).body(responseMessage);
        }
    }

    @GetMapping("/signout")
    public ResponseEntity<String> signout(){
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString()).body("Successfully Loged Out");
    }
}