package com.rumahhobi.controllers;

import java.util.Map;

import com.rumahhobi.dto.oas.LoginOas;
import com.rumahhobi.dto.oas.RegisterDto;
import com.rumahhobi.services.AuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthControllers {
    
    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(LoginOas.Request req) {
        System.out.println("LOGIN MASUK, RAW BODY = " + req.username);
        String token = authService.login(
                req.username,
                req.password
        );

        return Response.ok(
            Map.of(
                "token", token,
                "tokenType", "Bearer"
            )
        ).build();
    }


    @POST
    @Path("/register")
    public Response register(RegisterDto.Request req) {
        var res = authService.register(req);
        return Response.ok(res).build();
    }
}
