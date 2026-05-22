package br.com.andersondev.domain.social;

import br.com.andersondev.domain.exception.SelfReferenceException;
import br.com.andersondev.domain.shared.AggregateRoot;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.Objects;

/**
 * Raiz do agregado Follow (domain-catalog 2.5). Sem id proprio: a identidade e
 * a chave composta (followerId, followedId), encapsulada em {@link FollowId}.
 *
 * Invariantes (business-validation-matrix F-01):
 *  - followerId != followedId (seguir a si mesmo e proibido -&gt; SelfReferenceException / 400);
 *  - par (followerId, followedId) unico (garantido pela PK composta no banco e pelo gateway).
 */
public class Follow extends AggregateRoot<FollowId> {

    private final Instant followedAt;

    private Follow(final FollowId id, final Instant followedAt) {
        super(id);
        this.followedAt = followedAt;
    }

    /**
     * Cria uma nova relacao de seguir. Rejeita auto-follow (F-01).
     */
    public static Follow newFollow(final UserId followerId, final UserId followedId) {
        Objects.requireNonNull(followerId, "'followerId' nao pode ser nulo");
        Objects.requireNonNull(followedId, "'followedId' nao pode ser nulo");
        if (followerId.equals(followedId)) {
            throw SelfReferenceException.with("Um usuario nao pode seguir a si mesmo");
        }
        return new Follow(FollowId.of(followerId, followedId), Instant.now());
    }

    /**
     * Reidrata uma relacao existente a partir da persistencia.
     */
    public static Follow with(final UserId followerId, final UserId followedId, final Instant followedAt) {
        return new Follow(FollowId.of(followerId, followedId), followedAt);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // Invariante principal (auto-follow) garantida na criacao via SelfReferenceException.
    }

    public UserId getFollowerId() {
        return getId().followerId();
    }

    public UserId getFollowedId() {
        return getId().followedId();
    }

    public Instant getFollowedAt() {
        return this.followedAt;
    }
}
