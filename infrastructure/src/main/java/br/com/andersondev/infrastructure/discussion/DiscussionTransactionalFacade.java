package br.com.andersondev.infrastructure.discussion;

import br.com.andersondev.application.discussion.reply.ReplyOutput;
import br.com.andersondev.application.discussion.reply.add.AddReplyCommand;
import br.com.andersondev.application.discussion.reply.add.AddReplyUseCase;
import br.com.andersondev.application.discussion.reply.like.LikeReplyCommand;
import br.com.andersondev.application.discussion.reply.like.LikeReplyUseCase;
import br.com.andersondev.application.discussion.reply.list.ListRepliesCommand;
import br.com.andersondev.application.discussion.reply.list.ListRepliesUseCase;
import br.com.andersondev.application.discussion.reply.unlike.UnlikeReplyCommand;
import br.com.andersondev.application.discussion.reply.unlike.UnlikeReplyUseCase;
import br.com.andersondev.application.discussion.thread.ThreadOutput;
import br.com.andersondev.application.discussion.thread.create.CreateThreadCommand;
import br.com.andersondev.application.discussion.thread.create.CreateThreadUseCase;
import br.com.andersondev.application.discussion.thread.get.GetThreadByIdCommand;
import br.com.andersondev.application.discussion.thread.get.GetThreadByIdUseCase;
import br.com.andersondev.application.discussion.thread.get.ThreadDetailOutput;
import br.com.andersondev.application.discussion.thread.like.LikeThreadCommand;
import br.com.andersondev.application.discussion.thread.like.LikeThreadUseCase;
import br.com.andersondev.application.discussion.thread.list.ListThreadsByGameCommand;
import br.com.andersondev.application.discussion.thread.list.ListThreadsByGameUseCase;
import br.com.andersondev.application.discussion.thread.list.ThreadSummaryOutput;
import br.com.andersondev.application.discussion.thread.unlike.UnlikeThreadCommand;
import br.com.andersondev.application.discussion.thread.unlike.UnlikeThreadUseCase;
import br.com.andersondev.domain.shared.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fronteiras transacionais dos casos de uso de Discussao.
 * Os casos de uso sao Java puro (application layer), sem Spring.
 */
@Service
public class DiscussionTransactionalFacade {

    private final CreateThreadUseCase createThreadUseCase;
    private final GetThreadByIdUseCase getThreadByIdUseCase;
    private final ListThreadsByGameUseCase listThreadsByGameUseCase;
    private final AddReplyUseCase addReplyUseCase;
    private final ListRepliesUseCase listRepliesUseCase;
    private final LikeThreadUseCase likeThreadUseCase;
    private final UnlikeThreadUseCase unlikeThreadUseCase;
    private final LikeReplyUseCase likeReplyUseCase;
    private final UnlikeReplyUseCase unlikeReplyUseCase;

    public DiscussionTransactionalFacade(
            final CreateThreadUseCase createThreadUseCase,
            final GetThreadByIdUseCase getThreadByIdUseCase,
            final ListThreadsByGameUseCase listThreadsByGameUseCase,
            final AddReplyUseCase addReplyUseCase,
            final ListRepliesUseCase listRepliesUseCase,
            final LikeThreadUseCase likeThreadUseCase,
            final UnlikeThreadUseCase unlikeThreadUseCase,
            final LikeReplyUseCase likeReplyUseCase,
            final UnlikeReplyUseCase unlikeReplyUseCase
    ) {
        this.createThreadUseCase = createThreadUseCase;
        this.getThreadByIdUseCase = getThreadByIdUseCase;
        this.listThreadsByGameUseCase = listThreadsByGameUseCase;
        this.addReplyUseCase = addReplyUseCase;
        this.listRepliesUseCase = listRepliesUseCase;
        this.likeThreadUseCase = likeThreadUseCase;
        this.unlikeThreadUseCase = unlikeThreadUseCase;
        this.likeReplyUseCase = likeReplyUseCase;
        this.unlikeReplyUseCase = unlikeReplyUseCase;
    }

    @Transactional
    public ThreadOutput createThread(final CreateThreadCommand command) {
        return this.createThreadUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public ThreadDetailOutput getThreadById(final GetThreadByIdCommand command) {
        return this.getThreadByIdUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<ThreadSummaryOutput> listThreadsByGame(final ListThreadsByGameCommand command) {
        return this.listThreadsByGameUseCase.execute(command);
    }

    @Transactional
    public ReplyOutput addReply(final AddReplyCommand command) {
        return this.addReplyUseCase.execute(command);
    }

    @Transactional(readOnly = true)
    public Pagination<ReplyOutput> listReplies(final ListRepliesCommand command) {
        return this.listRepliesUseCase.execute(command);
    }

    @Transactional
    public void likeThread(final LikeThreadCommand command) {
        this.likeThreadUseCase.execute(command);
    }

    @Transactional
    public void unlikeThread(final UnlikeThreadCommand command) {
        this.unlikeThreadUseCase.execute(command);
    }

    @Transactional
    public void likeReply(final LikeReplyCommand command) {
        this.likeReplyUseCase.execute(command);
    }

    @Transactional
    public void unlikeReply(final UnlikeReplyCommand command) {
        this.unlikeReplyUseCase.execute(command);
    }
}
