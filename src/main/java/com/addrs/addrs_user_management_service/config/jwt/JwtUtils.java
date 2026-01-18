package com.addrs.addrs_user_management_service.config.jwt;

import com.addrs.addrs_user_management_service.utility.CustomUserDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {
    @Value("${com.addrs-user-management-service.cookie.jwtCookieName}")
    private String jwtCookieName;

    @Value("${com.addrs-user-management-service.cookie.path}")
    private String path;

    @Value("${com.addrs-user-management-service.cookie.jwtSecret}")
    private String secretKey;

    @Value("${com.addrs-user-management-service.cookie.maxAge}")
    private String maxAge;


    public ResponseCookie generateJwtCookie(CustomUserDetail userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        String jwt = generateTokenFromUsername(claims,userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName, jwt).path(path).maxAge(Long.parseLong(maxAge)*60).httpOnly(true).secure(false).build();
        return cookie;
    }
    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookieName, null).path(path).maxAge(0).build();
        return cookie;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenFromUsername(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 1000 *60* Long.parseLong(maxAge)))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getToken(HttpServletRequest request) {
        String jwtToken = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            jwtToken = authHeader.substring(7);
        } else if (request.getCookies().length != 0) {
            jwtToken = Arrays.stream(request.getCookies()).toList().stream().filter(n -> n.getName().equalsIgnoreCase(jwtCookieName)).findFirst().get().getValue();
        }
        return jwtToken;
    }

}
