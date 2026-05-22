package br.com.andersondev.domain.news.port;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.shared.Pagination;

import java.util.Optional;

/**
 * Porta de saida para persistencia de noticias.
 * Implementada na infrastructure (adapter JPA).
 */
public interface NewsGateway {

    News save(News news);

    Optional<News> findById(NewsId id);

    Pagination<News> findAll(int page, int size);

    Pagination<News> findByRelatedGameId(GameId relatedGameId, int page, int size);

    void deleteById(NewsId id);
}
