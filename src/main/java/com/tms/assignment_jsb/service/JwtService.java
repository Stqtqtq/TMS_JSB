package com.tms.assignment_jsb.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  public String generateToken(String username, String ipAddress, String browser) {
      Map<String, Object> claims = new HashMap<>();
      claims.put("ipAddress", ipAddress);
      claims.put("browser", browser);

      return Jwts.builder()
              .claims()
              .add(claims)
              .subject(username)
              .issuedAt(new Date())
              .expiration(new Date(System.currentTimeMillis() + 6 * 60 * 60 * 1000)) // 6 hours
              .and()
              .signWith(generateKey())
              .compact();
  }

  private SecretKey generateKey() {
    byte[] decode = Decoders.BASE64.decode(secretKey);

    return Keys.hmacShaKeyFor(decode);
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
            .verifyWith(generateKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public boolean isTokenExpired(String token) {
      return extractClaims(token).getExpiration().before(new Date());
  }

}
