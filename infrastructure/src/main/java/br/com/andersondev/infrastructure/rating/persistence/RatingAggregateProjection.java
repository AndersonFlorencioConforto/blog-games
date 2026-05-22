package br.com.andersondev.infrastructure.rating.persistence;

/**
 * Projecao JPQL para AVG/COUNT das avaliacoes de um jogo.
 * AVG sobre coluna inteira retorna Double no Hibernate (convertido para BigDecimal no gateway).
 */
public record RatingAggregateProjection(Double average, long total) {
}
