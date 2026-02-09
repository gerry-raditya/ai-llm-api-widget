package com.rumahhobi.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.rumahhobi.dto.oas.GeminiOas;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/v1beta/models")
@RegisterRestClient(configKey = "gemini-api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface GeminiRestClient {

    @POST
    @Path("/{model}:generateContent")
    GeminiOas.Response generateContent(
        @PathParam("model") String model,
        GeminiOas.Request request,
        @HeaderParam("x-goog-api-key") String apiKey
    );
    
}
