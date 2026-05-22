package br.com.andersondev.infrastructure.user;

import br.com.andersondev.application.social.follow.FollowUserCommand;
import br.com.andersondev.application.social.follow.ListSocialCommand;
import br.com.andersondev.application.user.profile.UpdateProfileCommand;
import br.com.andersondev.infrastructure.api.PageResponse;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
import br.com.andersondev.infrastructure.social.models.UserSummaryResponse;
import br.com.andersondev.infrastructure.user.models.MyProfileResponse;
import br.com.andersondev.infrastructure.user.models.UpdateProfileRequest;
import br.com.andersondev.infrastructure.user.models.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de usuarios: perfil, perfil proprio e relacoes sociais (api-catalog secao 2).
 * Enxuto: mapeia HTTP -&gt; command e delega para a fachada transacional.
 * O follower/dono e sempre o principal autenticado, nunca do path/body (security-matrix).
 * Rotas /me e follow exigem autenticacao; leitura de perfil/seguidores/seguindo e publica
 * (matchers em SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserTransactionalFacade userFacade;

    public UserController(final UserTransactionalFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> me(@AuthenticationPrincipal final AuthenticatedUser principal) {
        final var output = this.userFacade.getMyProfile(principal.userId());
        return ResponseEntity.ok(MyProfileResponse.from(output));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            @Valid @RequestBody final UpdateProfileRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.userFacade.updateProfile(UpdateProfileCommand.with(
                principal.userId(), request.name(), request.bio(), request.avatarUrl()));
        return ResponseEntity.ok(UserProfileResponse.from(output));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getById(@PathVariable("id") final String id) {
        return ResponseEntity.ok(UserProfileResponse.from(this.userFacade.getProfile(id)));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable("id") final String id,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.userFacade.follow(FollowUserCommand.with(principal.userId(), id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(
            @PathVariable("id") final String id,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.userFacade.unfollow(FollowUserCommand.with(principal.userId(), id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<PageResponse<UserSummaryResponse>> followers(
            @PathVariable("id") final String id,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.userFacade.listFollowers(ListSocialCommand.with(id, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, UserSummaryResponse::from));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<PageResponse<UserSummaryResponse>> following(
            @PathVariable("id") final String id,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.userFacade.listFollowing(ListSocialCommand.with(id, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, UserSummaryResponse::from));
    }
}
