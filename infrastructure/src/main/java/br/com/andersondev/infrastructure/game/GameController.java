package br.com.andersondev.infrastructure.game;

import br.com.andersondev.application.game.create.CreateGameCommand;
import br.com.andersondev.application.game.delete.DeleteGameCommand;
import br.com.andersondev.application.game.list.ListGamesCommand;
import br.com.andersondev.application.game.update.UpdateGameCommand;
import br.com.andersondev.infrastructure.api.PageResponse;
import br.com.andersondev.infrastructure.game.models.GameListItemResponse;
import br.com.andersondev.infrastructure.game.models.GameRequest;
import br.com.andersondev.infrastructure.game.models.GameResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller do catalogo de jogos (api-catalog secao 3). Enxuto: mapeia HTTP -&gt; command
 * e delega para a fachada transacional. Autorizacao: writes ADMIN (security-matrix G-01),
 * leitura publica (matchers em SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameTransactionalFacade gameFacade;

    public GameController(final GameTransactionalFacade gameFacade) {
        this.gameFacade = gameFacade;
    }

    @GetMapping
    public ResponseEntity<PageResponse<GameListItemResponse>> list(
            @RequestParam(name = "category", required = false) final String category,
            @RequestParam(name = "platform", required = false) final String platform,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") final String sort,
            @RequestParam(name = "dir", defaultValue = "desc") final String direction
    ) {
        final var pagination = this.gameFacade.list(
                ListGamesCommand.with(page, size, sort, direction, category, platform));
        return ResponseEntity.ok(PageResponse.from(pagination, GameListItemResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getById(@PathVariable("id") final String id) {
        return ResponseEntity.ok(GameResponse.from(this.gameFacade.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameResponse> create(@Valid @RequestBody final GameRequest request) {
        final var output = this.gameFacade.create(CreateGameCommand.with(
                request.title(),
                request.description(),
                request.editorialReview(),
                request.platformScore(),
                request.pros(),
                request.cons(),
                request.coverImageUrl(),
                request.categories(),
                request.platforms()
        ));
        return ResponseEntity
                .created(URI.create("/api/v1/games/" + output.id()))
                .body(GameResponse.from(output));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameResponse> update(
            @PathVariable("id") final String id,
            @Valid @RequestBody final GameRequest request
    ) {
        final var output = this.gameFacade.update(UpdateGameCommand.with(
                id,
                request.title(),
                request.description(),
                request.editorialReview(),
                request.platformScore(),
                request.pros(),
                request.cons(),
                request.coverImageUrl(),
                request.categories(),
                request.platforms()
        ));
        return ResponseEntity.ok(GameResponse.from(output));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") final String id) {
        this.gameFacade.delete(DeleteGameCommand.with(id));
        return ResponseEntity.noContent().build();
    }
}
