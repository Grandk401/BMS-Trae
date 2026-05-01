/**
 * JWT 工具类
 */
package com.bms.utils;

import com.bms.common.Permission;
import com.bms.common.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String username) {
        log.info("生成 JWT Token: username={}", username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return createToken(claims, username);
    }

    public String generateToken(String username, Role role, Permission[] permissions) {
        log.info("生成 JWT Token: username={}, role={}", username, role);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role.name());

        List<String> permissionCodes = new ArrayList<>();
        for (Permission p : permissions) {
            permissionCodes.add(p.getCode());
        }
        claims.put("permissions", permissionCodes);

        return createToken(claims, username);
    }

    public String generateToken(String username, Integer userId, Role role, Permission[] permissions) {
        log.info("生成 JWT Token: username={}, userId={}, role={}", username, userId, role);
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("role", role.name());

        List<String> permissionCodes = new ArrayList<>();
        for (Permission p : permissions) {
            permissionCodes.add(p.getCode());
        }
        claims.put("permissions", permissionCodes);

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Integer.class);
    }

    public Role getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String roleStr = claims.get("role", String.class);
        return Role.valueOf(roleStr);
    }

    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("permissions", List.class);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        boolean isExpired = expiration.before(new Date());
        if (isExpired) {
            log.warn("JWT Token 已过期");
        }
        return isExpired;
    }

    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        boolean isValid = tokenUsername.equals(username) && !isTokenExpired(token);
        if (!isValid) {
            log.warn("JWT Token 验证失败: tokenUsername={}, expectedUsername={}", tokenUsername, username);
        }
        return isValid;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
