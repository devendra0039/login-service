package com.addrs.addrs_user_management_service.config.jwt;

import com.addrs.addrs_user_management_service.utility.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Value("${com.addrs-user-management-service.cookie.jwtCookieName}")
    private String jwtCookieName;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String authHeader = request.getHeader("Authorization");
            String jwtToken = null;
            String username = null;
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                jwtToken = authHeader.substring(7);
                username = jwtUtils.extractUsername(jwtToken);
            }else if(request.getCookies() != null && request.getCookies().length != 0){
                Cookie cookie = Arrays.stream(request.getCookies()).toList().stream().filter(n-> n.getName().equalsIgnoreCase(jwtCookieName)).findFirst().orElse(null);
                if (cookie != null) {
                    jwtToken = cookie.getValue();
                    username = jwtUtils.extractUsername(jwtToken);
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Exception Occurred while authenticating user: {}", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
