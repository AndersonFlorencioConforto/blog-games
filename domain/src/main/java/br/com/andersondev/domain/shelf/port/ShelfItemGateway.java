package br.com.andersondev.domain.shelf.port;

import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.shared.Pagination;
import br.com.andersondev.domain.shelf.ShelfItem;
import br.com.andersondev.domain.shelf.ShelfStatus;
import br.com.andersondev.domain.user.UserId;

import java.util.Optional;

/**
 * Porta de saida para persistencia de itens de estante (domain-catalog 4 / ShelfItemRepository).
 * Implementada na infrastructure (adapter JPA).
 */
public interface ShelfItemGateway {

    ShelfItem save(ShelfItem shelfItem);

    Optional<ShelfItem> findByUserIdAndGameId(UserId userId, GameId gameId);

    boolean existsByUserIdAndGameId(UserId userId, GameId gameId);

    void deleteByUserIdAndGameId(UserId userId, GameId gameId);

    /**
     * Lista paginada da estante de um usuario, com filtro opcional por status
     * (null = todos os status). Ordenada por addedAt desc.
     */
    Pagination<ShelfItem> findByUserId(UserId userId, ShelfStatus status, int page, int size);
}
