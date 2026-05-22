package br.com.andersondev.infrastructure.game;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao do catalogo de jogos + avaliacoes (G-01, R-03, R-04).
 * Cobre seguranca (ADMIN vs USER), leitura publica e atualizacao da media desnormalizada.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameAndRatingIT {

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
              "pros": ["Historia incrivel"],
              "cons": ["Lento no inicio"],
              "categories": ["RPG", "ACAO"],
              "platforms": ["PC", "PLAYSTATION"]
            }
            """;

    private String createUserAndToken(final Role role) {
        final var id = UserId.from(UUID.randomUUID().toString());
        final var email = role.name().toLowerCase() + "-" + id.getValue() + "@email.com";
        final var user = User.with(
                id, "Tester", br.com.andersondev.domain.user.Email.of(email),
                "hash", role, null, null, null, true, Instant.now(), Instant.now());
        this.userGateway.save(user);
        return this.accessTokenProvider.generate(id, email, role).token();
    }

    @Test
    void givenAdmin_whenCreateGame_thenReturns201() throws Exception {
        final var token = createUserAndToken(Role.ADMIN);

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_BODY.formatted("Witcher " + UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalRatings").value(0))
                .andExpect(jsonPath("$.categories").isArray());
    }

    @Test
    void givenRegularUser_whenCreateGame_thenReturns403() throws Exception {
        final var token = createUserAndToken(Role.USER);

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_BODY.formatted("Forbidden " + UUID.randomUUID())))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAnonymous_whenCreateGame_thenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_BODY.formatted("Anon " + UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenExistingGame_whenGetByIdAndListPublicly_thenReturns200() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);
        final var gameId = createGame(adminToken, "Public " + UUID.randomUUID());

        // detalhe publico (sem token)
        mockMvc.perform(get("/api/v1/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId));

        // listagem publica (sem token)
        mockMvc.perform(get("/api/v1/games?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void givenDuplicateTitle_whenCreateGame_thenReturns409() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);
        final var title = "Dup " + UUID.randomUUID();
        createGame(adminToken, title);

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(GAME_BODY.formatted(title)))
                .andExpect(status().isConflict());
    }

    @Test
    void givenInvalidScore_whenCreateGame_thenReturns422() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Bad %s","description":"d","platformScore":99.0,
                                 "categories":["RPG"],"platforms":["PC"]}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void givenUser_whenRateGame_thenUpdatesAverageAndIsUpsert() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);
        final var userToken = createUserAndToken(Role.USER);
        final var gameId = createGame(adminToken, "Rated " + UUID.randomUUID());

        // primeira avaliacao: 4 estrelas -> media 4.00, total 1
        mockMvc.perform(post("/api/v1/games/" + gameId + "/ratings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stars\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stars").value(4))
                .andExpect(jsonPath("$.totalRatings").value(1))
                .andExpect(jsonPath("$.newAverage").value(4.00));

        // segunda avaliacao do MESMO usuario: atualiza (nao duplica) -> media 2.00, total 1
        mockMvc.perform(post("/api/v1/games/" + gameId + "/ratings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stars\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRatings").value(1))
                .andExpect(jsonPath("$.newAverage").value(2.00));

        // detalhe do jogo reflete a media desnormalizada
        mockMvc.perform(get("/api/v1/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userAverageRating").value(2.00))
                .andExpect(jsonPath("$.totalRatings").value(1));

        // propria avaliacao
        mockMvc.perform(get("/api/v1/games/" + gameId + "/ratings/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stars").value(2));
    }

    @Test
    void givenAnonymous_whenRateGame_thenReturns401() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);
        final var gameId = createGame(adminToken, "NoAuth " + UUID.randomUUID());

        mockMvc.perform(post("/api/v1/games/" + gameId + "/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stars\":4}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenStarsOutOfRange_whenRateGame_thenReturns422() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);
        final var userToken = createUserAndToken(Role.USER);
        final var gameId = createGame(adminToken, "OutOfRange " + UUID.randomUUID());

        mockMvc.perform(post("/api/v1/games/" + gameId + "/ratings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stars\":9}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void givenUnknownGame_whenRateGame_thenReturns404() throws Exception {
        final var userToken = createUserAndToken(Role.USER);

        mockMvc.perform(post("/api/v1/games/" + UUID.randomUUID() + "/ratings")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stars\":4}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAdmin_whenDeleteGame_thenSoftDeletedAndHiddenFromReads() throws Exception {
        final var adminToken = createUserAndToken(Role.ADMIN);
        final var gameId = createGame(adminToken, "ToDelete " + UUID.randomUUID());

        mockMvc.perform(delete("/api/v1/games/" + gameId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // jogo deletado nao aparece no detalhe (404)
        mockMvc.perform(get("/api/v1/games/" + gameId))
                .andExpect(status().isNotFound());
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
}
