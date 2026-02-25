package com.jaymin.taskmanager.security.jwt;

import com.jaymin.taskmanager.security.CustomUserDetails;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secreteKey;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Autowired
    CustomUserDetails userDetails;
    public String generateAccessToken(userDetails ){
        return generateToken(new HashMap<>(),userDetails,accessTokenExpiration);
    }
}
