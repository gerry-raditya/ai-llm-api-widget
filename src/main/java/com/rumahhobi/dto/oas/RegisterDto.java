package com.rumahhobi.dto.oas;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class RegisterDto {
      @Schema(
        name = "RegisterRequest",
        description = "Request registrasi user"
    )
    public static class Request {

        @Schema(description = "Client / Tenant ID", examples = "rumahhobi")
        public String clientCode;

        @Schema(description = "Username login", examples = "user1")
        public String username;

        @Schema(description = "Password plain text", examples = "password123")
        public String password;
    }

    @Schema(
        name = "RegisterResponse",
        description = "Response registrasi user"
    )
    public static class Response {

        @Schema(examples = "Registrasi berhasil")
        public String message;

        @Schema(examples = "rumahhobi")
        public String clientCode;

        @Schema(examples = "user1")
        public String username;
    }
}
