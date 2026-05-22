package br.com.andersondev.domain.user.port;

import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;

import java.util.Optional;

/**
 * Porta de saida para persistencia de usuarios.
 * Implementada na infrastructure (adapter JPA).
 */
public interface UserGateway {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
