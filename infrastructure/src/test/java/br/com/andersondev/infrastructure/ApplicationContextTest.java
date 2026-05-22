package br.com.andersondev.infrastructure;

import br.com.andersondev.infrastructure.auth.AuthTransactionalFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke test: garante que o contexto Spring sobe e que o grafo de beans
 * (casos de uso, gateways, seguranca, JPA) esta corretamente conectado.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @Autowired
    private AuthTransactionalFacade authFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertNotNull(authFacade);
        assertNotNull(objectMapper);
    }
}
