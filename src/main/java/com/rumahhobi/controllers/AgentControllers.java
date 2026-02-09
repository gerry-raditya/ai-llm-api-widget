package com.rumahhobi.controllers;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.rumahhobi.dto.oas.AiAgentDto;
import com.rumahhobi.services.AgentService;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/agents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AgentControllers {

    private final AgentService agentService;

    @Inject
    JsonWebToken jwt;

    public AgentControllers(AgentService agentService) {
        this.agentService = agentService;
    }


    @POST
    @RolesAllowed("USER")
    public Response chat(
            @Context SecurityContext ctx,
            AiAgentDto.Request req) {

        System.out.println("AUTH HEADER = " +
                ctx.getUserPrincipal());

        System.out.println("JWT name = " + jwt.getName());
        System.out.println("JWT groups = " + jwt.getGroups());

        req.userId = jwt.getName();
        req.clientId = jwt.getClaim("clientId");
        req.subscriptionPlan = jwt.getClaim("plan");

        return Response.ok(agentService.process(req)).build();
    }

}
