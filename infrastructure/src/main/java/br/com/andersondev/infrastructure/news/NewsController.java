package br.com.andersondev.infrastructure.news;

import br.com.andersondev.application.news.create.CreateNewsCommand;
import br.com.andersondev.application.news.delete.DeleteNewsCommand;
import br.com.andersondev.application.news.get.GetNewsByIdCommand;
import br.com.andersondev.application.news.list.ListNewsCommand;
import br.com.andersondev.application.news.update.UpdateNewsCommand;
import br.com.andersondev.infrastructure.api.PageResponse;
import br.com.andersondev.infrastructure.news.models.CreateNewsRequest;
import br.com.andersondev.infrastructure.news.models.NewsResponse;
import br.com.andersondev.infrastructure.news.models.NewsSummaryResponse;
import br.com.andersondev.infrastructure.news.models.UpdateNewsRequest;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
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
 * Controller de noticias (api-catalog secao 2.7).
 * Enxuto: mapeia HTTP -> command e delega para a fachada transacional.
 * Leituras sao publicas; escrita exige role ADMIN.
 */
@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsTransactionalFacade newsFacade;

    public NewsController(final NewsTransactionalFacade newsFacade) {
        this.newsFacade = newsFacade;
    }

    @GetMapping
    public ResponseEntity<PageResponse<NewsSummaryResponse>> listNews(
            @RequestParam(name = "gameId", required = false) final String gameId,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.newsFacade.listNews(ListNewsCommand.with(gameId, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, NewsSummaryResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponse> getNewsById(
            @PathVariable("id") final String id
    ) {
        final var output = this.newsFacade.getNewsById(GetNewsByIdCommand.with(id));
        return ResponseEntity.ok(NewsResponse.from(output));
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<NewsResponse> createNews(
            @RequestBody final CreateNewsRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var command = CreateNewsCommand.with(
                request.title(),
                request.summary(),
                request.content(),
                request.coverImageUrl(),
                principal.userId(),
                request.relatedGameId()
        );
        final var output = this.newsFacade.createNews(command);
        return ResponseEntity
                .created(URI.create("/api/v1/news/" + output.id()))
                .body(NewsResponse.from(output));
    }

    @PutMapping(
            value = "/{id}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<NewsResponse> updateNews(
            @PathVariable("id") final String id,
            @RequestBody final UpdateNewsRequest request
    ) {
        final var command = UpdateNewsCommand.with(
                id,
                request.title(),
                request.summary(),
                request.content(),
                request.coverImageUrl(),
                request.relatedGameId()
        );
        final var output = this.newsFacade.updateNews(command);
        return ResponseEntity.ok(NewsResponse.from(output));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(
            @PathVariable("id") final String id
    ) {
        this.newsFacade.deleteNews(DeleteNewsCommand.with(id));
        return ResponseEntity.noContent().build();
    }
}
