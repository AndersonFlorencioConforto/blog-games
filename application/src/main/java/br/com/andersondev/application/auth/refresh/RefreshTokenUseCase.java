package br.com.andersondev.application.auth.refresh;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.auth.AuthTokensOutput;

public abstract class RefreshTokenUseCase extends UseCase<RefreshTokenCommand, AuthTokensOutput> {
}
