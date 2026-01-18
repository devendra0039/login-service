package com.addrs.addrs_user_management_service.utility;

import com.addrs.addrs_user_management_service.entity.User;
import com.addrs.addrs_user_management_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Value("${com.addrs-user-management-service.cookie.jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("For username {} : Fetching user details from DB for authentication", username);
        User user = userRepository.findByEmail(username);
        if (user == null) {
            log.error("For username {} : User not found", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        if (!user.getActive()) {
            log.error("For username {} : User found but not not active", username);
            throw new UsernameNotFoundException("User found but not active with username: " + username);
        }
        return new CustomUserDetail(user);
    }

}
