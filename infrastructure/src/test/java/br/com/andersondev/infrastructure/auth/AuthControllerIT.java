package br.com.andersondev.infrastructure.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String json(final String body) {
        return body;
    }

    @Test
    void givenValidRegistration_whenRegister_thenCreatesUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {"name":"Anderson","email":"reg1@email.com","password":"MinhaSenh@123"}
                                """)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("reg1@email.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void givenDuplicateEmail_whenRegister_thenReturns409() throws Exception {
        final var payload = """
                {"name":"Anderson","email":"dup@email.com","password":"MinhaSenh@123"}
                """;
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isConflict());
    }

    @Test
    void givenWeakPassword_whenRegister_thenReturns422() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Anderson","email":"weak@email.com","password":"weak"}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void givenRegisteredUser_whenLogin_thenReturnsTokens() throws Exception {
        register("login@email.com");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"login@email.com","password":"MinhaSenh@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void givenWrongPassword_whenLogin_thenReturns401() throws Exception {
        register("wrongpw@email.com");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"wrongpw@email.com","password":"WrongPass@1"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidRefreshToken_whenRefresh_thenRotatesTokens() throws Exception {
        register("refresh@email.com");
        final var tokens = login("refresh@email.com");
        final var refreshToken = tokens.get("refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        // o token antigo agora foi revogado (rotacao) -> 401
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidRefreshToken_whenRefresh_thenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"00000000-0000-0000-0000-000000000000\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthenticatedUser_whenLogout_thenBlocklistsTokenAndReturns204() throws Exception {
        register("logout@email.com");
        final var tokens = login("logout@email.com");
        final var accessToken = tokens.get("accessToken").asText();
        final var refreshToken = tokens.get("refreshToken").asText();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isNoContent());

        // apos logout o access token esta na denylist -> nova chamada autenticada falha 401
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoToken_whenLogout_thenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAnyEmail_whenForgotPassword_thenAlwaysReturns202() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ghost@email.com\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void givenInvalidResetToken_whenResetPassword_thenReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"00000000-0000-0000-0000-000000000000","newPassword":"NovaSenha@456"}
                                """))
                .andExpect(status().isBadRequest());
    }

    private void register(final String email) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Anderson\",\"email\":\"" + email
                                + "\",\"password\":\"MinhaSenh@123\"}"))
                .andExpect(status().isCreated());
    }

    private JsonNode login(final String email) throws Exception {
        final var result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"MinhaSenh@123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
