package br.com.andersondev.infrastructure.api;

import br.com.andersondev.domain.shared.Pagination;

import java.util.List;
import java.util.function.Function;

/**
 * Envelope HTTP de paginacao conforme api-catalog:
 * {@code content, page, size, totalElements, totalPages}.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <D, T> PageResponse<T> from(final Pagination<D> pagination, final Function<D, T> mapper) {
        return new PageResponse<>(
                pagination.content().stream().map(mapper).toList(),
                pagination.page(),
                pagination.size(),
                pagination.totalElements(),
                pagination.totalPages()
        );
    }
}
