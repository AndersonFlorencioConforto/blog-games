package br.com.andersondev.application.discussion.reply.add;

import br.com.andersondev.domain.discussion.Reply;
import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ReplyGateway;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.exception.EntityNotFoundException;
import br.com.andersondev.domain.game.GameId;
import br.com.andersondev.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultAddReplyUseCaseTest {

    @Mock
    private ReplyGateway replyGateway;

    @Mock
    private ThreadGateway threadGateway;

    @InjectMocks
    private DefaultAddReplyUseCase useCase;

    @Test
    void givenValidCommandAndThreadExists_whenExecute_thenCreatesReplyAndIncrementsReplyCount() {
        // Arrange
        final var thread = stubThread();
        final var threadId = thread.getId().getValue();
        final var command = AddReplyCommand.with(threadId, UserId.unique().getValue(), "Resposta valida.");

        when(threadGateway.findById(any(ThreadId.class))).thenReturn(Optional.of(thread));
        when(replyGateway.save(any(Reply.class))).thenAnswer(returnsFirstArg());
        when(threadGateway.save(any(Thread.class))).thenAnswer(returnsFirstArg());

        // Act
        final var output = useCase.execute(command);

        // Assert
        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals(1, thread.getReplyCount());
        verify(replyGateway).save(any(Reply.class));
        verify(threadGateway).save(thread);
    }

    @Test
    void givenThreadNotFound_whenExecute_thenThrowsEntityNotFoundException() {
        // Arrange
        final var command = AddReplyCommand.with(ThreadId.unique().getValue(), UserId.unique().getValue(), "Resposta.");

        when(threadGateway.findById(any(ThreadId.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(replyGateway, never()).save(any());
    }

    @Test
    void givenGatewayFailure_whenExecute_thenPropagates() {
        // Arrange
        final var thread = stubThread();
        final var command = AddReplyCommand.with(thread.getId().getValue(), UserId.unique().getValue(), "Resposta.");

        when(threadGateway.findById(any(ThreadId.class))).thenReturn(Optional.of(thread));
        when(replyGateway.save(any(Reply.class))).thenThrow(new RuntimeException("db down"));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(command));
    }

    private static Thread stubThread() {
        return Thread.newThread(
                GameId.unique(),
                UserId.unique(),
                "Titulo valido para teste",
                "Conteudo valido com mais de 10 caracteres para a thread."
        );
    }
}
