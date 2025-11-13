package com.chaos.schoollib.security;

import com.chaos.schoollib.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm; // (0.12.5 不再需要)
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // (0.12.5 推荐)
import java.util.Date;
import java.util.stream.Collectors;

/**
 *
 * JWT Token 的生成和验证
 *
 */
@Component
public class JwtTokenProvider {

    private final long jwtExpiration;
    // 使用 SecretKey 对象
    private final SecretKey key;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration-ms}") long jwtExpiration
    ) {
        this.jwtExpiration = jwtExpiration;

        // 将 base64 字符串解码为 byte[]
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        // 使用 Keys 工具从 byte[] 生成 SecretKey
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     */
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.jwtExpiration);

        String roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                // signWith 只需要 SecretKey，它已包含算法
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder() // 1. 使用 0.12.5 的 parserBuilder()
                .setSigningKey(key)          // 2. 设置秘钥 (SecretKey)
                .build()                     // 3. (关键修复) 构建解析器
                .parseClaimsJws(token)       // 4. 解析
                .getBody();
        return claims.getSubject();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()           // 1. 使用 0.12.5 的 parserBuilder()
                    .setSigningKey(key)    // 2. 设置秘钥 (SecretKey)
                    .build()               // 3. (关键修复) 构建解析器
                    .parseClaimsJws(token); // 4. 解析

            return true;
        } catch (Exception ex) {
            // (日志) 例如：
            // logger.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }
}
