package com.rumahhobi.services;

import com.rumahhobi.dto.oas.RegisterDto;
import com.rumahhobi.utils.JwtUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class AuthService {
    
    @Inject
    EntityManager em;

    public String login(String username, String password) {
        System.out.println("\nAuthService.login: " + username + " / " + password);
        Object[] user = (Object[]) em.createNativeQuery("""
                    SELECT u.id, c.client_code, c.subscription_plan
                    FROM app_user u
                    JOIN client c ON u.client_id = c.id
                    WHERE u.username = :u
                      AND u.password_hash = :p
                """)
                .setParameter("u", username)
                .setParameter("p", hash(password))
                .getSingleResult();

        Long userId = ((Number) user[0]).longValue();
        String clientId = (String) user[1];
        String plan = (String) user[2];

        return JwtUtil.generate(userId, clientId, plan);
    }

    private String hash(String pwd) {
        return Integer.toHexString(pwd.hashCode()); // demo
    }

    @Transactional
    public RegisterDto.Response register(RegisterDto.Request req) {
        // =========================
        // 1. Ambil client_id (AMAN)
        // =========================
        var clientResult = em.createNativeQuery("""
            SELECT id FROM client WHERE client_code = :cc
        """)
        .setParameter("cc", req.clientCode)
        .getResultList();

        if (clientResult.isEmpty()) {
            throw new WebApplicationException(
                "Client code tidak ditemukan: " + req.clientCode,
                400
            );
        }

        Long clientId = ((Number) clientResult.get(0)).longValue();

        // =========================
        // 2. Cek username (AMAN)
        // =========================
        var userResult = em.createNativeQuery("""
            SELECT id FROM app_user
            WHERE client_id = :cid AND username = :u
        """)
        .setParameter("cid", clientId)
        .setParameter("u", req.username)
        .getResultList();

        if (!userResult.isEmpty()) {
            throw new WebApplicationException(
                "Username sudah digunakan: " + req.username,
                409
            );
        }

        // =========================
        // 3. Insert user
        // =========================
        em.createNativeQuery("""
            INSERT INTO app_user (client_id, username, password_hash, role)
            VALUES (:cid, :u, :ph, 'USER')
        """)
        .setParameter("cid", clientId)
        .setParameter("u", req.username)
        .setParameter("ph", hash(req.password))
        .executeUpdate();

        // =========================
        // 4. Ambil userId hasil insert
        // =========================
        Tuple tuple = (Tuple) em.createNativeQuery("""
                    SELECT
                        id        AS user_id,
                        client_id AS client_id
                    FROM app_user
                    WHERE client_id = :cid
                      AND username  = :u
                    ORDER BY id DESC
                    LIMIT 1
                """, Tuple.class)
                .setParameter("cid", clientId)
                .setParameter("u", req.username)
                .getSingleResult();


        // =========================
        // 5. Build Response DTO
        // =========================
        RegisterDto.Response res = new RegisterDto.Response();
        res.clientCode= tuple.get("client_id", String.class);
        res.username = req.username;
        res.message = "Registrasi berhasil";

        return res;
    }
}
