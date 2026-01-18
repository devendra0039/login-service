package com.addrs.addrs_user_management_service.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/user")
@RestController
public class Health {
    @GetMapping(value = "/health")
    public String health() {
        return "OK user-managemetn-service";
    }
}
