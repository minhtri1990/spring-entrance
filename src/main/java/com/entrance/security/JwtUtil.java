package com.entrance.security;

import com.entrance.constant.Role;
import com.entrance.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expirationDateIn1Hour}")
    private int tokenExpiration;
    @Value("${jwt.expirationDateIn7Day}")
    private int refreshTokenExpiration;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    public String generateToken(User dto, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Role.ROLE, role);
        return doGenerateToken(claims, dto.getEmail());
    }

    public String doGenerateRefreshToken(User dto, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Role.ROLE, role);
        return doGenerateRefreshToken(claims, dto.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    public String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        List<SimpleGrantedAuthority> roles = null;
        String role = claims.get(Role.ROLE, String.class);
        if (Role.ADMIN.equalsIgnoreCase(role)) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_PREFIX + Role.ADMIN));
        }
        if (Role.USER.equalsIgnoreCase(role)) {
            roles = Arrays.asList(new SimpleGrantedAuthority(Role.ROLE_PREFIX + Role.USER));
        }
        return roles;
    }
}
