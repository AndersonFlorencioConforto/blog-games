package br.com.andersondev.application.discussion.reply.list;

import br.com.andersondev.application.UseCase;
import br.com.andersondev.application.discussion.reply.ReplyOutput;
import br.com.andersondev.domain.shared.Pagination;

public abstract class ListRepliesUseCase extends UseCase<ListRepliesCommand, Pagination<ReplyOutput>> {
}
