package br.com.andersondev.infrastructure.news.persistence;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.news.News;
import br.com.andersondev.domain.news.NewsId;
import br.com.andersondev.domain.news.port.NewsGateway;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NewsJpaGateway implements NewsGateway {

    private final NewsRepository repository;

    public NewsJpaGateway(final NewsRepository repository) {
        this.repository = repository;
    }

    @Override
    public News save(final News news) {
        return this.repository.save(NewsJpaEntity.from(news)).toAggregate();
    }

    @Override
    public Optional<News> findById(final NewsId id) {
        return this.repository.findById(id.getValue()).map(NewsJpaEntity::toAggregate);
    }

    @Override
    public Pagination<News> findAll(final int page, final int size) {
        final var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        final var result = this.repository.findAllByOrderByPublishedAtDesc(pageable);
        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent().stream().map(NewsJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public Pagination<News> findByRelatedGameId(final GameId relatedGameId, final int page, final int size) {
        final var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        final var result = this.repository.findByRelatedGameId(relatedGameId.getValue(), pageable);
        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent().stream().map(NewsJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public void deleteById(final NewsId id) {
        this.repository.deleteById(id.getValue());
    }
}
