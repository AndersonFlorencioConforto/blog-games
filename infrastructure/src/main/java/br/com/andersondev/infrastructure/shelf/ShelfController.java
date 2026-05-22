package br.com.andersondev.infrastructure.shelf;

import br.com.andersondev.application.shelf.add.AddToShelfCommand;
import br.com.andersondev.application.shelf.list.ListShelfCommand;
import br.com.andersondev.application.shelf.remove.RemoveFromShelfCommand;
import br.com.andersondev.application.shelf.update.UpdateShelfStatusCommand;
import br.com.andersondev.infrastructure.api.PageResponse;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
import br.com.andersondev.infrastructure.shelf.models.AddToShelfRequest;
import br.com.andersondev.infrastructure.shelf.models.ShelfItemResponse;
import br.com.andersondev.infrastructure.shelf.models.ShelfListItemResponse;
import br.com.andersondev.infrastructure.shelf.models.UpdateShelfStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
 * Controller da estante do usuario (api-catalog secao 4). Leitura publica por usuario;
 * escrita exige autenticacao e o dono e sempre o principal autenticado (nunca do path/body).
 */
@RestController
@RequestMapping("/api/v1")
public class ShelfController {

    private final ShelfTransactionalFacade shelfFacade;

    public ShelfController(final ShelfTransactionalFacade shelfFacade) {
        this.shelfFacade = shelfFacade;
    }

    @GetMapping("/users/{userId}/shelf")
    public ResponseEntity<PageResponse<ShelfListItemResponse>> list(
            @PathVariable("userId") final String userId,
            @RequestParam(name = "status", required = false) final String status,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.shelfFacade.list(ListShelfCommand.with(userId, status, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, ShelfListItemResponse::from));
    }

    @PostMapping("/shelf")
    public ResponseEntity<ShelfItemResponse> add(
            @Valid @RequestBody final AddToShelfRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.shelfFacade.add(
                AddToShelfCommand.with(principal.userId(), request.gameId(), request.status()));
        return ResponseEntity
                .created(URI.create("/api/v1/shelf/" + output.gameId()))
                .body(ShelfItemResponse.from(output));
    }

    @PutMapping("/shelf/{gameId}")
    public ResponseEntity<ShelfItemResponse> update(
            @PathVariable("gameId") final String gameId,
            @Valid @RequestBody final UpdateShelfStatusRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.shelfFacade.updateStatus(
                UpdateShelfStatusCommand.with(principal.userId(), gameId, request.status()));
        return ResponseEntity.ok(ShelfItemResponse.from(output));
    }

    @DeleteMapping("/shelf/{gameId}")
    public ResponseEntity<Void> remove(
            @PathVariable("gameId") final String gameId,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.shelfFacade.remove(RemoveFromShelfCommand.with(principal.userId(), gameId));
        return ResponseEntity.noContent().build();
    }
}
