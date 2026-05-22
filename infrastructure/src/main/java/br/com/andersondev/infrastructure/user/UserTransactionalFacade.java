package br.com.andersondev.infrastructure.user;

import br.com.andersondev.application.social.UserSummaryOutput;
import br.com.andersondev.application.social.follow.FollowUserCommand;
import br.com.andersondev.application.social.follow.FollowUserUseCase;
import br.com.andersondev.application.social.follow.ListFollowersUseCase;
import br.com.andersondev.application.social.follow.ListFollowingUseCase;
import br.com.andersondev.application.social.follow.ListSocialCommand;
import br.com.andersondev.application.social.follow.UnfollowUserUseCase;
import br.com.andersondev.application.user.profile.GetMyProfileUseCase;
import br.com.andersondev.application.user.profile.GetUserProfileUseCase;
import br.com.andersondev.application.user.profile.MyProfileOutput;
import br.com.andersondev.application.user.profile.UpdateProfileCommand;
import br.com.andersondev.application.user.profile.UpdateProfileUseCase;
import br.com.andersondev.application.user.profile.UserProfileOutput;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fronteiras transacionais (transaction-boundaries) dos casos de uso de Perfil e Social,
 * que sao Java puro na camada application. Reads sao readOnly; escritas sao transacionais.
 */
@Service
public class UserTransactionalFacade {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetMyProfileUseCase getMyProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final FollowUserUseCase followUserUseCase;
    private final UnfollowUserUseCase unfollowUserUseCase;
    private final ListFollowersUseCase listFollowersUseCase;
    private final ListFollowingUseCase listFollowingUseCase;

    public UserTransactionalFacade(
            final GetUserProfileUseCase getUserProfileUseCase,
            final GetMyProfileUseCase getMyProfileUseCase,
            final UpdateProfileUseCase updateProfileUseCase,
            final FollowUserUseCase followUserUseCase,
            final UnfollowUserUseCase unfollowUserUseCase,
            final ListFollowersUseCase listFollowersUseCase,
            final ListFollowingUseCase listFollowingUseCase
    ) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.getMyProfileUseCase = getMyProfileUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.followUserUseCase = followUserUseCase;
        this.unfollowUserUseCase = unfollowUserUseCase;
        this.listFollowersUseCase = listFollowersUseCase;
        this.listFollowingUseCase = listFollowingUseCase;
    }

    @Transactional(readOnly = true)
    public UserProfileOutput getProfile(final String id) {
        return this.getUserProfileUseCase.execute(id);
    }

    @Transactional(readOnly = true)
    public MyProfileOutput getMyProfile(final String authenticatedUserId) {
        return this.getMyProfileUseCase.execute(authenticatedUserId);
    }

    @Transactional
    public UserProfileOutput updateProfile(final UpdateProfileCommand command) {
        return this.updateProfileUseCase.execute(command);
    }

    @Transactional
    public void follow(final FollowUserCommand command) {
        this.followUserUseCase.execute(command);
    }

    @Transactional
    public void unfollow(final FollowUserCommand command) {
        this.unfollowUserUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<UserSummaryOutput> listFollowers(final ListSocialCommand command) {
        return this.listFollowersUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<UserSummaryOutput> listFollowing(final ListSocialCommand command) {
        return this.listFollowingUseCase.execute(command);
    }
}
