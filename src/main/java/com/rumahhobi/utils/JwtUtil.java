package com.rumahhobi.utils;

import io.smallrye.jwt.build.Jwt;

public class JwtUtil {
    public static String generate(
        Long userId,
        String clientId,
        String plan
    ) {
        return Jwt.issuer("ai-agent")
                .upn(String.valueOf(userId))
                .groups("USER")
                .claim("clientId", clientId)
                .claim("plan", plan)
                .expiresIn(3600)
                .sign();
    }
}
