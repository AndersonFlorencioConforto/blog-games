package br.com.andersondev.application.social.follow;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.social.UserSummaryOutput;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListFollowersUseCase
        extends UseCase<ListSocialCommand, Pagination<UserSummaryOutput>> {
}
