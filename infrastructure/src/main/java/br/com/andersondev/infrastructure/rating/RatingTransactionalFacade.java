package br.com.andersondev.infrastructure.rating;

import br.com.andersondev.application.rating.get.GetMyRatingQuery;
import br.com.andersondev.application.rating.get.GetMyRatingUseCase;
import br.com.andersondev.application.rating.get.MyRatingOutput;
import br.com.andersondev.application.rating.rate.RateGameCommand;
import br.com.andersondev.application.rating.rate.RateGameOutput;
import br.com.andersondev.application.rating.rate.RateGameUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fronteira transacional do RateGame (transaction-boundaries): upsert de rating +
 * atualizacao da media desnormalizada do jogo na mesma transacao (lock pessimista
 * aplicado no gateway). Leitura da propria avaliacao e readOnly.
 */
@Service
public class RatingTransactionalFacade {

    private final RateGameUseCase rateGameUseCase;
    private final GetMyRatingUseCase getMyRatingUseCase;

    public RatingTransactionalFacade(
            final RateGameUseCase rateGameUseCase,
            final GetMyRatingUseCase getMyRatingUseCase
    ) {
        this.rateGameUseCase = rateGameUseCase;
        this.getMyRatingUseCase = getMyRatingUseCase;
    }

    @Transactional
    public RateGameOutput rate(final RateGameCommand command) {
        return this.rateGameUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public MyRatingOutput getMyRating(final GetMyRatingQuery query) {
        return this.getMyRatingUseCase.execute(query);
    }
}
