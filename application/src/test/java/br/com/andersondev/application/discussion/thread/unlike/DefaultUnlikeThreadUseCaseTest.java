package br.com.andersondev.application.discussion.thread.unlike;

import br.com.andersondev.domain.discussion.Thread;
import br.com.andersondev.domain.discussion.ThreadId;
import br.com.andersondev.domain.discussion.port.ThreadGateway;
import br.com.andersondev.domain.discussion.port.ThreadLikeGateway;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUnlikeThreadUseCaseTest {

    @Mock
    private ThreadGateway threadGateway;

    @Mock
    private ThreadLikeGateway threadLikeGateway;

    @InjectMocks
    private DefaultUnlikeThreadUseCase useCase;

    @Test
    void givenLikedThread_whenExecute_thenDeletesLikeAndDecrementsCount() {
        // Arrange
        final var thread = stubThreadWithOneLike();
        final var userId = UserId.unique().getValue();
        final var command = UnlikeThreadCommand.with(thread.getId().getValue(), userId);

        when(threadGateway.findById(any(ThreadId.class))).thenReturn(Optional.of(thread));
        when(threadLikeGateway.existsByThreadIdAndUserId(any(ThreadId.class), any(UserId.class)))
                .thenReturn(true);
        when(threadGateway.save(any(Thread.class))).thenAnswer(returnsFirstArg());

        // Act
        useCase.execute(command);

        // Assert
        assertEquals(0, thread.getLikeCount());
        verify(threadLikeGateway).deleteByThreadIdAndUserId(any(ThreadId.class), any(UserId.class));
        verify(threadGateway).save(thread);
    }

    @Test
    void givenNotLiked_whenExecute_thenIdempotentNoDelete() {
        // Arrange
        final var thread = stubThread();
        final var userId = UserId.unique().getValue();
        final var command = UnlikeThreadCommand.with(thread.getId().getValue(), userId);

        when(threadGateway.findById(any(ThreadId.class))).thenReturn(Optional.of(thread));
        when(threadLikeGateway.existsByThreadIdAndUserId(any(ThreadId.class), any(UserId.class)))
                .thenReturn(false);

        // Act
        useCase.execute(command);

        // Assert
        verify(threadLikeGateway, never()).deleteByThreadIdAndUserId(any(), any());
        verify(threadGateway, never()).save(any());
    }

    @Test
    void givenThreadNotFound_whenExecute_thenThrowsEntityNotFoundException() {
        // Arrange
        final var command = UnlikeThreadCommand.with(ThreadId.unique().getValue(), UserId.unique().getValue());

        when(threadGateway.findById(any(ThreadId.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(command));
        verify(threadLikeGateway, never()).deleteByThreadIdAndUserId(any(), any());
    }

    @Test
    void givenLikeCountAtZero_whenDecrementCalled_thenStaysAtZero() {
        // Arrange: thread starts at 0
        final var thread = stubThread();
        thread.decrementLikeCount();

        assertEquals(0, thread.getLikeCount());
    }

    private static Thread stubThread() {
        return Thread.newThread(
                GameId.unique(),
                UserId.unique(),
                "Titulo valido para teste",
                "Conteudo valido com mais de 10 caracteres para a thread."
        );
    }

    private static Thread stubThreadWithOneLike() {
        final var thread = stubThread();
        thread.incrementLikeCount();
        return thread;
    }
}
