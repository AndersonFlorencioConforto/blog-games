package br.com.andersondev.domain.rating;

import java.math.BigDecimal;

/**
 * Resultado agregado das avaliacoes de um jogo: media e total.
 * Usado para sincronizar os campos desnormalizados de Game na mesma transacao.
 */
public record RatingAggregate(BigDecimal average, int total) {

    public static RatingAggregate of(final BigDecimal average, final int total) {
        return new RatingAggregate(average == null ? BigDecimal.ZERO : average, total);
    }

    public static RatingAggregate empty() {
        return new RatingAggregate(BigDecimal.ZERO, 0);
    }
}
