package br.com.andersondev.infrastructure.rating;

import br.com.andersondev.application.rating.get.GetMyRatingQuery;
import br.com.andersondev.application.rating.rate.RateGameCommand;
import br.com.andersondev.infrastructure.rating.models.MyRatingResponse;
import br.com.andersondev.infrastructure.rating.models.RateGameRequest;
import br.com.andersondev.infrastructure.rating.models.RateGameResponse;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de avaliacoes (api-catalog secao 3). Avaliar/ler propria avaliacao exige
 * autenticacao (security-matrix); usuario banido ja e rejeitado no filtro JWT.
 * O userId vem do principal autenticado, nunca do corpo da requisicao.
 */
@RestController
@RequestMapping("/api/v1/games/{gameId}/ratings")
public class RatingController {

    private final RatingTransactionalFacade ratingFacade;

    public RatingController(final RatingTransactionalFacade ratingFacade) {
        this.ratingFacade = ratingFacade;
    }

    @PostMapping
    public ResponseEntity<RateGameResponse> rate(
            @PathVariable("gameId") final String gameId,
            @Valid @RequestBody final RateGameRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.ratingFacade.rate(
                RateGameCommand.with(gameId, principal.userId(), request.stars()));
        return ResponseEntity.ok(RateGameResponse.from(output));
    }

    @GetMapping("/me")
    public ResponseEntity<MyRatingResponse> myRating(
            @PathVariable("gameId") final String gameId,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.ratingFacade.getMyRating(
                GetMyRatingQuery.with(gameId, principal.userId()));
        return ResponseEntity.ok(MyRatingResponse.from(output));
    }
}
