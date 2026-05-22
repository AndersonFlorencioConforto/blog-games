package br.com.andersondev.domain.shared;

import java.util.List;
import java.util.function.Function;

/**
 * Contrato de paginacao puro de dominio (sem dependencia de framework).
 * Espelha o contrato HTTP do api-catalog: {@code content, page, size, totalElements, totalPages}.
 *
 * @param <T> tipo do item paginado
 */
public record Pagination<T>(
        int page,
        int size,
        long totalElements,
        List<T> content
) {

    public Pagination {
        content = content == null ? List.of() : List.copyOf(content);
    }

    public int totalPages() {
        if (this.size <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) this.totalElements / (double) this.size);
    }

    /**
     * Converte os itens preservando os metadados de pagina.
     */
    public <R> Pagination<R> map(final Function<T, R> mapper) {
        final var mapped = this.content.stream().map(mapper).toList();
        return new Pagination<>(this.page, this.size, this.totalElements, mapped);
    }
}
