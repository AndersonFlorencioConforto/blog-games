package br.com.andersondev.domain.game.port;

import br.com.andersondev.domain.game.Game;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.game.GameSearchQuery;
import br.com.andersondev.domain.game.GameSummary;
import br.com.andersondev.domain.shared.Pagination;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Porta de saida para persistencia de jogos.
 * Implementada na infrastructure (adapter JPA).
 */
public interface GameGateway {

    Game save(Game game);

    Optional<Game> findById(GameId id);

    /**
     * Busca um jogo aplicando lock pessimista (SELECT FOR UPDATE) para atualizacao
     * transacional da media de avaliacoes (transaction-boundaries / RateGameUseCase).
     */
    Optional<Game> findByIdForUpdate(GameId id);

    boolean existsByTitle(String title);

    /**
     * Verifica unicidade de titulo desconsiderando um id (uso em update).
     */
    boolean existsByTitleAndIdNot(String title, GameId id);

    /**
     * Busca paginada de jogos nao deletados, com filtros opcionais por categoria e plataforma (G-09).
     */
    Pagination<Game> findAll(GameSearchQuery query);

    /**
     * Retorna dados resumidos (id, title, coverImageUrl) dos jogos indicados, indexados por id.
     * Usado para enriquecer listagens cross-aggregate (ex.: estante). Jogos ausentes
     * (inexistentes/deletados) simplesmente nao aparecem no mapa.
     */
    Map<String, GameSummary> findSummariesByIds(List<String> gameIds);
}
