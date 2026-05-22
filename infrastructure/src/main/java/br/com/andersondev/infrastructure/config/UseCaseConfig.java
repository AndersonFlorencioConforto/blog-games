package br.com.andersondev.infrastructure.config;

import br.com.andersondev.application.auth.login.DefaultLoginUseCase;
import br.com.andersondev.application.auth.login.LoginUseCase;
import br.com.andersondev.application.auth.logout.DefaultLogoutUseCase;
import br.com.andersondev.application.auth.logout.LogoutUseCase;
import br.com.andersondev.application.auth.password.DefaultRequestPasswordResetUseCase;
import br.com.andersondev.application.auth.password.DefaultResetPasswordUseCase;
import br.com.andersondev.application.auth.password.RequestPasswordResetUseCase;
import br.com.andersondev.application.auth.password.ResetPasswordUseCase;
import br.com.andersondev.application.auth.port.AccessTokenProvider;
import br.com.andersondev.application.auth.port.PasswordResetLinkBuilder;
import br.com.andersondev.application.auth.port.RefreshTokenProvider;
import br.com.andersondev.application.auth.port.ResetTokenProvider;
import br.com.andersondev.application.auth.refresh.DefaultRefreshTokenUseCase;
import br.com.andersondev.application.auth.refresh.RefreshTokenUseCase;
import br.com.andersondev.application.discussion.reply.add.AddReplyUseCase;
import br.com.andersondev.application.discussion.reply.add.DefaultAddReplyUseCase;
import br.com.andersondev.application.discussion.reply.like.DefaultLikeReplyUseCase;
import br.com.andersondev.application.discussion.reply.like.LikeReplyUseCase;
import br.com.andersondev.application.discussion.reply.list.DefaultListRepliesUseCase;
import br.com.andersondev.application.discussion.reply.list.ListRepliesUseCase;
import br.com.andersondev.application.discussion.reply.unlike.DefaultUnlikeReplyUseCase;
import br.com.andersondev.application.discussion.reply.unlike.UnlikeReplyUseCase;
import br.com.andersondev.application.discussion.thread.create.CreateThreadUseCase;
import br.com.andersondev.application.discussion.thread.create.DefaultCreateThreadUseCase;
import br.com.andersondev.application.discussion.thread.get.DefaultGetThreadByIdUseCase;
import br.com.andersondev.application.discussion.thread.get.GetThreadByIdUseCase;
import br.com.andersondev.application.discussion.thread.like.DefaultLikeThreadUseCase;
import br.com.andersondev.application.discussion.thread.like.LikeThreadUseCase;
import br.com.andersondev.application.discussion.thread.list.DefaultListThreadsByGameUseCase;
import br.com.andersondev.application.discussion.thread.list.ListThreadsByGameUseCase;
import br.com.andersondev.application.discussion.thread.unlike.DefaultUnlikeThreadUseCase;
import br.com.andersondev.application.discussion.thread.unlike.UnlikeThreadUseCase;
import br.com.andersondev.application.game.create.CreateGameUseCase;
import br.com.andersondev.application.game.create.DefaultCreateGameUseCase;
import br.com.andersondev.application.game.delete.DefaultDeleteGameUseCase;
import br.com.andersondev.application.game.delete.DeleteGameUseCase;
import br.com.andersondev.application.game.get.DefaultGetGameByIdUseCase;
import br.com.andersondev.application.game.get.GetGameByIdUseCase;
import br.com.andersondev.application.game.list.DefaultListGamesUseCase;
import br.com.andersondev.application.game.list.ListGamesUseCase;
import br.com.andersondev.application.game.update.DefaultUpdateGameUseCase;
import br.com.andersondev.application.game.update.UpdateGameUseCase;
import br.com.andersondev.application.rating.get.DefaultGetMyRatingUseCase;
import br.com.andersondev.application.rating.get.GetMyRatingUseCase;
import br.com.andersondev.application.rating.rate.DefaultRateGameUseCase;
import br.com.andersondev.application.rating.rate.RateGameUseCase;
import br.com.andersondev.application.shelf.add.AddToShelfUseCase;
import br.com.andersondev.application.shelf.add.DefaultAddToShelfUseCase;
import br.com.andersondev.application.moderation.create.CreateReportUseCase;
import br.com.andersondev.application.moderation.create.DefaultCreateReportUseCase;
import br.com.andersondev.application.moderation.list.DefaultListReportsUseCase;
import br.com.andersondev.application.moderation.list.ListReportsUseCase;
import br.com.andersondev.application.moderation.resolve.DefaultResolveReportUseCase;
import br.com.andersondev.application.moderation.resolve.ResolveReportUseCase;
import br.com.andersondev.domain.moderation.port.ReportGateway;
import br.com.andersondev.application.news.create.CreateNewsUseCase;
import br.com.andersondev.application.news.create.DefaultCreateNewsUseCase;
import br.com.andersondev.application.news.delete.DefaultDeleteNewsUseCase;
import br.com.andersondev.application.news.delete.DeleteNewsUseCase;
import br.com.andersondev.application.news.get.DefaultGetNewsByIdUseCase;
import br.com.andersondev.application.news.get.GetNewsByIdUseCase;
import br.com.andersondev.application.news.list.DefaultListNewsUseCase;
import br.com.andersondev.application.news.list.ListNewsUseCase;
import br.com.andersondev.application.news.update.DefaultUpdateNewsUseCase;
import br.com.andersondev.application.news.update.UpdateNewsUseCase;
import br.com.andersondev.application.shelf.list.DefaultListShelfUseCase;
import br.com.andersondev.application.shelf.list.ListShelfUseCase;
import br.com.andersondev.application.shelf.remove.DefaultRemoveFromShelfUseCase;
import br.com.andersondev.application.shelf.remove.RemoveFromShelfUseCase;
import br.com.andersondev.application.shelf.update.DefaultUpdateShelfStatusUseCase;
import br.com.andersondev.application.shelf.update.UpdateShelfStatusUseCase;
import br.com.andersondev.application.social.follow.DefaultFollowUserUseCase;
import br.com.andersondev.application.social.follow.DefaultListFollowersUseCase;
import br.com.andersondev.application.social.follow.DefaultListFollowingUseCase;
import br.com.andersondev.application.social.follow.DefaultUnfollowUserUseCase;
import br.com.andersondev.application.social.follow.FollowUserUseCase;
import br.com.andersondev.application.social.follow.ListFollowersUseCase;
import br.com.andersondev.application.social.follow.ListFollowingUseCase;
import br.com.andersondev.application.social.follow.UnfollowUserUseCase;
import br.com.andersondev.application.user.profile.DefaultGetMyProfileUseCase;
import br.com.andersondev.application.user.profile.DefaultGetUserProfileUseCase;
import br.com.andersondev.application.user.profile.DefaultUpdateProfileUseCase;
import br.com.andersondev.application.user.profile.GetMyProfileUseCase;
import br.com.andersondev.application.user.profile.GetUserProfileUseCase;
import br.com.andersondev.application.user.profile.UpdateProfileUseCase;
import br.com.andersondev.application.user.register.DefaultRegisterUserUseCase;
import br.com.andersondev.application.user.register.RegisterUserUseCase;
import br.com.andersondev.domain.auth.port.EmailPort;
import br.com.andersondev.domain.auth.port.PasswordResetTokenGateway;
import br.com.andersondev.domain.auth.port.RefreshTokenGateway;
import br.com.andersondev.domain.auth.port.TokenBlocklistGateway;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.news.port.NewsGateway;
import br.com.andersondev.domain.discussion.port.ReplyLikeGateway;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.discussion.port.ThreadLikeGateway;
import br.com.andersondev.domain.game.port.GameGateway;
import br.com.andersondev.domain.rating.port.RatingGateway;
import br.com.andersondev.domain.shelf.port.ShelfItemGateway;
import br.com.andersondev.domain.social.port.FollowGateway;
import br.com.andersondev.domain.user.port.PasswordHasherPort;
import br.com.andersondev.domain.user.port.UserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring explicito dos casos de uso (application e Java puro, sem anotacoes Spring).
 * As fronteiras transacionais sao aplicadas via wrappers transacionais (TransactionalUseCaseConfig).
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            final UserGateway userGateway,
            final PasswordHasherPort passwordHasher
    ) {
        return new DefaultRegisterUserUseCase(userGateway, passwordHasher);
    }

    @Bean
    public LoginUseCase loginUseCase(
            final UserGateway userGateway,
            final PasswordHasherPort passwordHasher,
            final AccessTokenProvider accessTokenProvider,
            final RefreshTokenProvider refreshTokenProvider,
            final RefreshTokenGateway refreshTokenGateway
    ) {
        return new DefaultLoginUseCase(
                userGateway, passwordHasher, accessTokenProvider, refreshTokenProvider, refreshTokenGateway);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            final RefreshTokenGateway refreshTokenGateway,
            final UserGateway userGateway,
            final AccessTokenProvider accessTokenProvider,
            final RefreshTokenProvider refreshTokenProvider
    ) {
        return new DefaultRefreshTokenUseCase(
                refreshTokenGateway, userGateway, accessTokenProvider, refreshTokenProvider);
    }

    @Bean
    public LogoutUseCase logoutUseCase(
            final TokenBlocklistGateway tokenBlocklistGateway,
            final RefreshTokenGateway refreshTokenGateway,
            final RefreshTokenProvider refreshTokenProvider
    ) {
        return new DefaultLogoutUseCase(tokenBlocklistGateway, refreshTokenGateway, refreshTokenProvider);
    }

    @Bean
    public RequestPasswordResetUseCase requestPasswordResetUseCase(
            final UserGateway userGateway,
            final PasswordResetTokenGateway passwordResetTokenGateway,
            final ResetTokenProvider resetTokenProvider,
            final PasswordResetLinkBuilder linkBuilder,
            final EmailPort emailPort
    ) {
        return new DefaultRequestPasswordResetUseCase(
                userGateway, passwordResetTokenGateway, resetTokenProvider, linkBuilder, emailPort);
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(
            final PasswordResetTokenGateway passwordResetTokenGateway,
            final UserGateway userGateway,
            final PasswordHasherPort passwordHasher,
            final RefreshTokenGateway refreshTokenGateway,
            final ResetTokenProvider resetTokenProvider
    ) {
        return new DefaultResetPasswordUseCase(
                passwordResetTokenGateway, userGateway, passwordHasher, refreshTokenGateway, resetTokenProvider);
    }

    // --- Discussao (Threads, Replies, Likes) ---

    @Bean
    public CreateThreadUseCase createThreadUseCase(
            final ThreadGateway threadGateway,
            final GameGateway gameGateway
    ) {
        return new DefaultCreateThreadUseCase(threadGateway, gameGateway);
    }

    @Bean
    public GetThreadByIdUseCase getThreadByIdUseCase(final ThreadGateway threadGateway) {
        return new DefaultGetThreadByIdUseCase(threadGateway);
    }

    @Bean
    public ListThreadsByGameUseCase listThreadsByGameUseCase(final ThreadGateway threadGateway) {
        return new DefaultListThreadsByGameUseCase(threadGateway);
    }

    @Bean
    public AddReplyUseCase addReplyUseCase(
            final ReplyGateway replyGateway,
            final ThreadGateway threadGateway
    ) {
        return new DefaultAddReplyUseCase(replyGateway, threadGateway);
    }

    @Bean
    public ListRepliesUseCase listRepliesUseCase(
            final ReplyGateway replyGateway,
            final ThreadGateway threadGateway
    ) {
        return new DefaultListRepliesUseCase(replyGateway, threadGateway);
    }

    @Bean
    public LikeThreadUseCase likeThreadUseCase(
            final ThreadGateway threadGateway,
            final ThreadLikeGateway threadLikeGateway
    ) {
        return new DefaultLikeThreadUseCase(threadGateway, threadLikeGateway);
    }

    @Bean
    public UnlikeThreadUseCase unlikeThreadUseCase(
            final ThreadGateway threadGateway,
            final ThreadLikeGateway threadLikeGateway
    ) {
        return new DefaultUnlikeThreadUseCase(threadGateway, threadLikeGateway);
    }

    @Bean
    public LikeReplyUseCase likeReplyUseCase(
            final ReplyGateway replyGateway,
            final ReplyLikeGateway replyLikeGateway
    ) {
        return new DefaultLikeReplyUseCase(replyGateway, replyLikeGateway);
    }

    @Bean
    public UnlikeReplyUseCase unlikeReplyUseCase(
            final ReplyGateway replyGateway,
            final ReplyLikeGateway replyLikeGateway
    ) {
        return new DefaultUnlikeReplyUseCase(replyGateway, replyLikeGateway);
    }

    // --- Catalogo de Jogos ---

    @Bean
    public CreateGameUseCase createGameUseCase(final GameGateway gameGateway) {
        return new DefaultCreateGameUseCase(gameGateway);
    }

    @Bean
    public UpdateGameUseCase updateGameUseCase(final GameGateway gameGateway) {
        return new DefaultUpdateGameUseCase(gameGateway);
    }

    @Bean
    public DeleteGameUseCase deleteGameUseCase(final GameGateway gameGateway) {
        return new DefaultDeleteGameUseCase(gameGateway);
    }

    @Bean
    public GetGameByIdUseCase getGameByIdUseCase(final GameGateway gameGateway) {
        return new DefaultGetGameByIdUseCase(gameGateway);
    }

    @Bean
    public ListGamesUseCase listGamesUseCase(final GameGateway gameGateway) {
        return new DefaultListGamesUseCase(gameGateway);
    }

    // --- Avaliacoes ---

    @Bean
    public RateGameUseCase rateGameUseCase(
            final GameGateway gameGateway,
            final RatingGateway ratingGateway
    ) {
        return new DefaultRateGameUseCase(gameGateway, ratingGateway);
    }

    @Bean
    public GetMyRatingUseCase getMyRatingUseCase(final RatingGateway ratingGateway) {
        return new DefaultGetMyRatingUseCase(ratingGateway);
    }

    // --- Perfil de Usuario ---

    @Bean
    public GetUserProfileUseCase getUserProfileUseCase(
            final UserGateway userGateway,
            final FollowGateway followGateway
    ) {
        return new DefaultGetUserProfileUseCase(userGateway, followGateway);
    }

    @Bean
    public GetMyProfileUseCase getMyProfileUseCase(
            final UserGateway userGateway,
            final FollowGateway followGateway
    ) {
        return new DefaultGetMyProfileUseCase(userGateway, followGateway);
    }

    @Bean
    public UpdateProfileUseCase updateProfileUseCase(
            final UserGateway userGateway,
            final FollowGateway followGateway
    ) {
        return new DefaultUpdateProfileUseCase(userGateway, followGateway);
    }

    // --- Social (Follow) ---

    @Bean
    public FollowUserUseCase followUserUseCase(
            final FollowGateway followGateway,
            final UserGateway userGateway
    ) {
        return new DefaultFollowUserUseCase(followGateway, userGateway);
    }

    @Bean
    public UnfollowUserUseCase unfollowUserUseCase(final FollowGateway followGateway) {
        return new DefaultUnfollowUserUseCase(followGateway);
    }

    @Bean
    public ListFollowersUseCase listFollowersUseCase(final FollowGateway followGateway) {
        return new DefaultListFollowersUseCase(followGateway);
    }

    @Bean
    public ListFollowingUseCase listFollowingUseCase(final FollowGateway followGateway) {
        return new DefaultListFollowingUseCase(followGateway);
    }

    // --- Estante (Shelf) ---

    @Bean
    public AddToShelfUseCase addToShelfUseCase(
            final ShelfItemGateway shelfItemGateway,
            final GameGateway gameGateway
    ) {
        return new DefaultAddToShelfUseCase(shelfItemGateway, gameGateway);
    }

    @Bean
    public UpdateShelfStatusUseCase updateShelfStatusUseCase(final ShelfItemGateway shelfItemGateway) {
        return new DefaultUpdateShelfStatusUseCase(shelfItemGateway);
    }

    @Bean
    public RemoveFromShelfUseCase removeFromShelfUseCase(final ShelfItemGateway shelfItemGateway) {
        return new DefaultRemoveFromShelfUseCase(shelfItemGateway);
    }

    @Bean
    public ListShelfUseCase listShelfUseCase(
            final ShelfItemGateway shelfItemGateway,
            final GameGateway gameGateway
    ) {
        return new DefaultListShelfUseCase(shelfItemGateway, gameGateway);
    }

    // --- Noticias (News) ---

    @Bean
    public CreateNewsUseCase createNewsUseCase(
            final NewsGateway newsGateway,
            final GameGateway gameGateway
    ) {
        return new DefaultCreateNewsUseCase(newsGateway, gameGateway);
    }

    @Bean
    public UpdateNewsUseCase updateNewsUseCase(
            final NewsGateway newsGateway,
            final GameGateway gameGateway
    ) {
        return new DefaultUpdateNewsUseCase(newsGateway, gameGateway);
    }

    @Bean
    public DeleteNewsUseCase deleteNewsUseCase(final NewsGateway newsGateway) {
        return new DefaultDeleteNewsUseCase(newsGateway);
    }

    @Bean
    public GetNewsByIdUseCase getNewsByIdUseCase(final NewsGateway newsGateway) {
        return new DefaultGetNewsByIdUseCase(newsGateway);
    }

    @Bean
    public ListNewsUseCase listNewsUseCase(final NewsGateway newsGateway) {
        return new DefaultListNewsUseCase(newsGateway);
    }

    // --- Moderacao (Reports) ---

    @Bean
    public CreateReportUseCase createReportUseCase(
            final ReportGateway reportGateway,
            final UserGateway userGateway
    ) {
        return new DefaultCreateReportUseCase(reportGateway, userGateway);
    }

    @Bean
    public ListReportsUseCase listReportsUseCase(final ReportGateway reportGateway) {
        return new DefaultListReportsUseCase(reportGateway);
    }

    @Bean
    public ResolveReportUseCase resolveReportUseCase(
            final ReportGateway reportGateway,
            final UserGateway userGateway
    ) {
        return new DefaultResolveReportUseCase(reportGateway, userGateway);
    }
}
