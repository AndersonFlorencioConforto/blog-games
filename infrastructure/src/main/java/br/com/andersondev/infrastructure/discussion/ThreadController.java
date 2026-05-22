package br.com.andersondev.infrastructure.discussion;

import br.com.andersondev.application.discussion.reply.add.AddReplyCommand;
import br.com.andersondev.application.discussion.reply.like.LikeReplyCommand;
import br.com.andersondev.application.discussion.reply.list.ListRepliesCommand;
import br.com.andersondev.application.discussion.reply.unlike.UnlikeReplyCommand;
import br.com.andersondev.application.discussion.thread.create.CreateThreadCommand;
import br.com.andersondev.application.discussion.thread.get.GetThreadByIdCommand;
import br.com.andersondev.application.discussion.thread.like.LikeThreadCommand;
import br.com.andersondev.application.discussion.thread.list.ListThreadsByGameCommand;
import br.com.andersondev.application.discussion.thread.unlike.UnlikeThreadCommand;
import br.com.andersondev.infrastructure.api.PageResponse;
import br.com.andersondev.infrastructure.discussion.models.AddReplyRequest;
import br.com.andersondev.infrastructure.discussion.models.CreateThreadRequest;
import br.com.andersondev.infrastructure.discussion.models.ReplyResponse;
import br.com.andersondev.infrastructure.discussion.models.ThreadDetailResponse;
import br.com.andersondev.infrastructure.discussion.models.ThreadResponse;
import br.com.andersondev.infrastructure.discussion.models.ThreadSummaryResponse;
import br.com.andersondev.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller de threads de discussao (api-catalog secao 6).
 * Enxuto: mapeia HTTP -> command e delega para a fachada transacional.
 * Leituras de thread e respostas sao publicas; escrita exige autenticacao.
 */
@RestController
@RequestMapping("/api/v1/games/{gameId}/threads")
public class ThreadController {

    private final DiscussionTransactionalFacade discussionFacade;

    public ThreadController(final DiscussionTransactionalFacade discussionFacade) {
        this.discussionFacade = discussionFacade;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ThreadSummaryResponse>> listThreads(
            @PathVariable("gameId") final String gameId,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.discussionFacade.listThreadsByGame(
                ListThreadsByGameCommand.with(gameId, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, ThreadSummaryResponse::from));
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ThreadResponse> createThread(
            @PathVariable("gameId") final String gameId,
            @Valid @RequestBody final CreateThreadRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.discussionFacade.createThread(
                CreateThreadCommand.with(gameId, principal.userId(), request.title(), request.content()));
        return ResponseEntity
                .created(URI.create("/api/v1/games/" + gameId + "/threads/" + output.id()))
                .body(ThreadResponse.from(output));
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<ThreadDetailResponse> getThread(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId
    ) {
        final var output = this.discussionFacade.getThreadById(GetThreadByIdCommand.with(threadId));
        return ResponseEntity.ok(ThreadDetailResponse.from(output));
    }

    @PostMapping(
            value = "/{threadId}/replies",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<ReplyResponse> addReply(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId,
            @Valid @RequestBody final AddReplyRequest request,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        final var output = this.discussionFacade.addReply(
                AddReplyCommand.with(threadId, principal.userId(), request.content()));
        return ResponseEntity
                .created(URI.create("/api/v1/games/" + gameId + "/threads/" + threadId + "/replies/" + output.id()))
                .body(ReplyResponse.from(output));
    }

    @GetMapping("/{threadId}/replies")
    public ResponseEntity<PageResponse<ReplyResponse>> listReplies(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size
    ) {
        final var pagination = this.discussionFacade.listReplies(
                ListRepliesCommand.with(threadId, page, size));
        return ResponseEntity.ok(PageResponse.from(pagination, ReplyResponse::from));
    }

    @PostMapping("/{threadId}/likes")
    public ResponseEntity<Void> likeThread(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.discussionFacade.likeThread(LikeThreadCommand.with(threadId, principal.userId()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{threadId}/likes")
    public ResponseEntity<Void> unlikeThread(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.discussionFacade.unlikeThread(UnlikeThreadCommand.with(threadId, principal.userId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{threadId}/replies/{replyId}/likes")
    public ResponseEntity<Void> likeReply(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId,
            @PathVariable("replyId") final String replyId,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.discussionFacade.likeReply(LikeReplyCommand.with(replyId, principal.userId()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{threadId}/replies/{replyId}/likes")
    public ResponseEntity<Void> unlikeReply(
            @PathVariable("gameId") final String gameId,
            @PathVariable("threadId") final String threadId,
            @PathVariable("replyId") final String replyId,
            @AuthenticationPrincipal final AuthenticatedUser principal
    ) {
        this.discussionFacade.unlikeReply(UnlikeReplyCommand.with(replyId, principal.userId()));
        return ResponseEntity.noContent().build();
    }
}
