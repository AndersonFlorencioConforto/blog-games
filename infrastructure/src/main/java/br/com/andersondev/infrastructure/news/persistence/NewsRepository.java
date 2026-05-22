package br.com.andersondev.infrastructure.news.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<NewsJpaEntity, String> {

    Page<NewsJpaEntity> findByRelatedGameId(String relatedGameId, Pageable pageable);

    Page<NewsJpaEntity> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
