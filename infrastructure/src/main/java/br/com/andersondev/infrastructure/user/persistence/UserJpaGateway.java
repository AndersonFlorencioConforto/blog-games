package br.com.andersondev.infrastructure.user.persistence;

import br.com.andersondev.domain.user.Email;
import br.com.andersondev.domain.user.User;
import br.com.andersondev.domain.user.UserId;
import br.com.andersondev.domain.user.port.UserGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserJpaGateway implements UserGateway {

    private final UserRepository repository;

    public UserJpaGateway(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(final User user) {
        return this.repository.save(UserJpaEntity.from(user)).toAggregate();
    }

    @Override
    public Optional<User> findById(final UserId id) {
        return this.repository.findById(id.getValue()).map(UserJpaEntity::toAggregate);
    }

    @Override
    public Optional<User> findByEmail(final Email email) {
        return this.repository.findByEmail(email.getValue()).map(UserJpaEntity::toAggregate);
    }

    @Override
    public boolean existsByEmail(final Email email) {
        return this.repository.existsByEmail(email.getValue());
    }
}
