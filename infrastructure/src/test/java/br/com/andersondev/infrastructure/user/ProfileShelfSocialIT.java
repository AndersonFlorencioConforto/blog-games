package br.com.andersondev.infrastructure.user;

import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.Role;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import br.com.andersondev.infrastructure.security.JjwtAccessTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao da fatia Perfil + Estante + Social (PR-*, S-*, F-*).
 * Cobre seguranca (publico vs autenticado), idempotencia de follow, contadores,
 * CRUD da estante e visibilidade publica.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileShelfSocialIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserGateway userGateway;

    @Autowired
    private JjwtAccessTokenProvider accessTokenProvider;

    private static final String GAME_BODY = """
            {
              "title": "%s",
              "description": "RPG de mundo aberto",
              "platformScore": 9.5,
              "categories": ["RPG"],
              "platforms": ["PC"]
            }
            """;

    private record TestUser(String id, String token) {
    }

    private TestUser createUser(final Role role) {
        final var id = UserId.from(UUID.randomUUID().toString());
        final var email = role.name().toLowerCase() + "-" + id.getValue() + "@email.com";
        final var user = User.with(
                id, "Tester", Email.of(email), "hash", role,
                null, null, null, true, Instant.now(), Instant.now());
        this.userGateway.save(user);
        final var token = this.accessTokenProvider.generate(id, email, role).token();
        return new TestUser(id.getValue(), token);
    }

    private String createGame(final String adminToken, final String title) throws Exception {
        final var result = mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_BODY.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn();
        final JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asText();
    }

    // --- Perfil ---

    @Test
    void givenExistingUser_whenGetProfilePublicly_thenReturns200WithCounts() throws Exception {
        final var user = createUser(Role.USER);

        mockMvc.perform(get("/api/v1/users/" + user.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.followersCount").value(0))
                .andExpect(jsonPath("$.followingCount").value(0));
    }

    @Test
    void givenUnknownUser_whenGetProfile_thenReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticated_whenGetMe_thenReturnsEmailAndRole() throws Exception {
        final var user = createUser(Role.USER);

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.id()))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void givenAnonymous_whenGetMe_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenAuthenticated_whenUpdateProfile_thenReturns200() throws Exception {
        final var user = createUser(Role.USER);

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Novo Nome","bio":"Gamer","avatarUrl":"https://cdn.example.com/a.jpg"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"))
                .andExpect(jsonPath("$.bio").value("Gamer"));
    }

    @Test
    void givenInvalidProfile_whenUpdateProfile_thenReturns422() throws Exception {
        final var user = createUser(Role.USER);

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"a\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // --- Social (Follow) ---

    @Test
    void givenTwoUsers_whenFollowAndUnfollow_thenCountsAreConsistent() throws Exception {
        final var alice = createUser(Role.USER);
        final var bob = createUser(Role.USER);

        // alice segue bob (idempotente: duas chamadas)
        mockMvc.perform(post("/api/v1/users/" + bob.id() + "/follow")
                        .header("Authorization", "Bearer " + alice.token()))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/v1/users/" + bob.id() + "/follow")
                        .header("Authorization", "Bearer " + alice.token()))
                .andExpect(status().isNoContent());

        // bob tem 1 seguidor, alice segue 1
        mockMvc.perform(get("/api/v1/users/" + bob.id()))
                .andExpect(jsonPath("$.followersCount").value(1));
        mockMvc.perform(get("/api/v1/users/" + alice.id()))
                .andExpect(jsonPath("$.followingCount").value(1));

        // listagem publica de seguidores de bob inclui alice
        mockMvc.perform(get("/api/v1/users/" + bob.id() + "/followers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(alice.id()));

        // listagem publica de seguindo de alice inclui bob
        mockMvc.perform(get("/api/v1/users/" + alice.id() + "/following"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(bob.id()));

        // alice deixa de seguir
        mockMvc.perform(delete("/api/v1/users/" + bob.id() + "/follow")
                        .header("Authorization", "Bearer " + alice.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/" + bob.id()))
                .andExpect(jsonPath("$.followersCount").value(0));
    }

    @Test
    void givenSelf_whenFollow_thenReturns400() throws Exception {
        final var user = createUser(Role.USER);

        mockMvc.perform(post("/api/v1/users/" + user.id() + "/follow")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenUnknownTarget_whenFollow_thenReturns404() throws Exception {
        final var user = createUser(Role.USER);

        mockMvc.perform(post("/api/v1/users/" + UUID.randomUUID() + "/follow")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnonymous_whenFollow_thenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/users/" + UUID.randomUUID() + "/follow"))
                .andExpect(status().isUnauthorized());
    }

    // --- Estante (Shelf) ---

    @Test
    void givenUserAndGame_whenShelfCrudAndPublicView_thenWorks() throws Exception {
        final var admin = createUser(Role.ADMIN);
        final var owner = createUser(Role.USER);
        final var gameId = createGame(admin.token(), "Shelf Game " + UUID.randomUUID());

        // adiciona a estante (201)
        mockMvc.perform(post("/api/v1/shelf")
                        .header("Authorization", "Bearer " + owner.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gameId\":\"" + gameId + "\",\"status\":\"JA_TENHO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId").value(gameId))
                .andExpect(jsonPath("$.status").value("JA_TENHO"));

        // duplicado -> 409
        mockMvc.perform(post("/api/v1/shelf")
                        .header("Authorization", "Bearer " + owner.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gameId\":\"" + gameId + "\",\"status\":\"FAVORITO\"}"))
                .andExpect(status().isConflict());

        // atualiza status (200)
        mockMvc.perform(put("/api/v1/shelf/" + gameId)
                        .header("Authorization", "Bearer " + owner.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"FAVORITO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAVORITO"));

        // estante publica (sem token) traz dados resumidos do jogo
        mockMvc.perform(get("/api/v1/users/" + owner.id() + "/shelf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].game.id").value(gameId))
                .andExpect(jsonPath("$.content[0].status").value("FAVORITO"));

        // filtro por status sem correspondencia
        mockMvc.perform(get("/api/v1/users/" + owner.id() + "/shelf?status=JA_TENHO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        // remove (204)
        mockMvc.perform(delete("/api/v1/shelf/" + gameId)
                        .header("Authorization", "Bearer " + owner.token()))
                .andExpect(status().isNoContent());

        // remover novamente -> 404
        mockMvc.perform(delete("/api/v1/shelf/" + gameId)
                        .header("Authorization", "Bearer " + owner.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnknownGame_whenAddToShelf_thenReturns404() throws Exception {
        final var owner = createUser(Role.USER);

        mockMvc.perform(post("/api/v1/shelf")
                        .header("Authorization", "Bearer " + owner.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gameId\":\"" + UUID.randomUUID() + "\",\"status\":\"JA_TENHO\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenItemNotInShelf_whenUpdateStatus_thenReturns404() throws Exception {
        final var owner = createUser(Role.USER);

        mockMvc.perform(put("/api/v1/shelf/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + owner.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"FAVORITO\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnonymous_whenAddToShelf_thenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/shelf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gameId\":\"" + UUID.randomUUID() + "\",\"status\":\"JA_TENHO\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidStatus_whenAddToShelf_thenReturns422() throws Exception {
        final var admin = createUser(Role.ADMIN);
        final var owner = createUser(Role.USER);
        final var gameId = createGame(admin.token(), "Bad Status " + UUID.randomUUID());

        mockMvc.perform(post("/api/v1/shelf")
                        .header("Authorization", "Bearer " + owner.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gameId\":\"" + gameId + "\",\"status\":\"NAO_EXISTE\"}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
