# Estrategia de Eventos - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21

---

## 1. Decisao Arquitetural

Conforme documentado em **ADR-0004**, a plataforma **nao utiliza broker de mensageria externo** (RabbitMQ, Kafka, etc.) no escopo atual. O volume de dados e a complexidade dos casos de uso nao justificam a adicao de infraestrutura de fila.

A estrategia de eventos e puramente interna, utilizando o mecanismo de **Spring Application Events** (`ApplicationEvent` / `ApplicationEventPublisher`) para desacoplamento opcional entre casos de uso dentro do mesmo processo.

---

## 2. Mecanismo de Eventos Internos

**Tecnologia:** `ApplicationEventPublisher` do Spring (sincrono por padrao)  
**Transacionalidade:** Handlers de evento criticos usam `@TransactionalEventListener(phase = AFTER_COMMIT)` para garantir que o evento so e processado apos a transacao principal ser confirmada.

### Como funciona

```
UseCase (application layer)
  -> publica ApplicationEvent via EventPublisher port
     -> Spring despacha para handlers registrados
        -> Handler (infrastructure ou application) executa acao secundaria
```

**Porta de publicacao (application layer):**
```java
// br.com.andersondev.application.shared.port.EventPublisher
public interface EventPublisher {
    void publish(Object event);
}

// br.com.andersondev.infrastructure.event.SpringEventPublisher
// implementa EventPublisher usando ApplicationEventPublisher
```

---

## 3. Catalogo de Eventos Internos

### 3.1 UserRegisteredEvent

**Publicado por:** `RegisterUserUseCase`  
**Quando:** Apos persistencia bem-sucedida de novo usuario  
**Fase:** `AFTER_COMMIT`

**Payload:**
```java
public record UserRegisteredEvent(
    UUID userId,
    String name,
    String email,
    Instant registeredAt
) {}
```

**Handlers atuais:**
- (Nenhum no Release 1. Reservado para futura notificacao de boas-vindas por e-mail)

---

### 3.2 GameRatedEvent

**Publicado por:** `RateGameUseCase`  
**Quando:** Apos upsert de avaliacao no banco  
**Fase:** `AFTER_COMMIT`

**Payload:**
```java
public record GameRatedEvent(
    UUID gameId,
    UUID userId,
    int stars,
    boolean isNewRating,
    int previousStars  // 0 se isNewRating = true
) {}
```

**Handlers atuais:**
- `UpdateGameAverageRatingHandler`: recalcula e persiste `average_user_rating` e `total_ratings` em `games`

**Observacao:** O handler pode ser executado na mesma transacao (sincrono) ou em `AFTER_COMMIT`. Para o volume atual, execucao sincrona na transacao principal e preferivel para garantir consistencia imediata. Handler aplica formula:

```
Se nova avaliacao:
  new_count = old_count + 1
  new_average = ((old_average * old_count) + stars) / new_count

Se atualizacao:
  new_average = ((old_average * old_count) - previous_stars + stars) / old_count
```

---

### 3.3 PasswordResetRequestedEvent

**Publicado por:** `RequestPasswordResetUseCase`  
**Quando:** Apos geracao e persistencia do token de reset  
**Fase:** `AFTER_COMMIT`

**Payload:**
```java
public record PasswordResetRequestedEvent(
    UUID userId,
    String email,
    String resetToken,
    Instant expiresAt
) {}
```

**Handlers atuais:**
- `SendPasswordResetEmailHandler`: chama `EmailPort.sendPasswordResetEmail(email, resetLink)` de forma sincrona

**Observacao sobre falha:** Se o envio de e-mail falhar, o token ja esta persistido no banco. O usuario pode solicitar novamente. Nao ha retry automatico no escopo atual.

---

### 3.4 UserBannedEvent

**Publicado por:** `ResolveReportUseCase` (quando decision = APPROVED com banDurationDays > 0)  
**Quando:** Apos ban persistido no banco  
**Fase:** `AFTER_COMMIT`

**Payload:**
```java
public record UserBannedEvent(
    UUID bannedUserId,
    UUID adminId,
    Instant bannedUntil,
    String reason
) {}
```

**Handlers atuais:**
- `RevokeUserTokensHandler`: revoga todos os refresh tokens do usuario banido e registra todos os JTIs de access tokens ativos na denylist

---

### 3.5 ThreadCreatedEvent

**Publicado por:** `CreateThreadUseCase`  
**Quando:** Apos thread persistida  
**Fase:** `AFTER_COMMIT`

**Payload:**
```java
public record ThreadCreatedEvent(
    UUID threadId,
    UUID gameId,
    UUID authorId,
    String title
) {}
```

**Handlers atuais:**
- (Nenhum no Release 2. Reservado para futuras notificacoes de seguidores do autor)

---

### 3.6 ReplyAddedEvent

**Publicado por:** `ReplyToThreadUseCase`  
**Quando:** Apos reply persistida  
**Fase:** `AFTER_COMMIT`

**Payload:**
```java
public record ReplyAddedEvent(
    UUID replyId,
    UUID threadId,
    UUID authorId
) {}
```

**Handlers atuais:**
- `IncrementThreadReplyCountHandler`: incrementa `reply_count` na thread pai

---

## 4. Eventos NAO Implementados (Escopo Futuro)

| Evento | Trigger | Motivo do Adiamento |
|---|---|---|
| `UserFollowedEvent` | Seguir usuario | Nenhum handler necessario no Release 1-2 |
| `NewsPublishedEvent` | Publicar noticia | Sem canal de notificacao no escopo |
| `LikeAddedEvent` | Like em thread/reply | Atualizacao de contador e sincrona na transacao |

---

## 5. Criterios para Adotar Mensageria Externa

Revisitar ADR-0004 e criar novo ADR quando:

1. Tempo de resposta do endpoint `/auth/forgot-password` ultrapassar 2 segundos por falha/lentidao do SMTP
2. Pico de avaliacoes (`POST /games/{id}/ratings`) causar contenao de lock na linha do jogo ao atualizar `average_user_rating`
3. Necessidade de notificacoes para multiplos consumidores (ex: app mobile + push notification + e-mail ao mesmo tempo)
4. Necessidade de replay de eventos para auditoria ou event sourcing

---

## 6. Tabela de Idempotencia

Eventos internos do Spring nao tem garantia de entrega exatamente-uma-vez se o processo for reiniciado antes do `AFTER_COMMIT`. Para handlers criticos:

| Handler | Idempotente? | Estrategia |
|---|---|---|
| `UpdateGameAverageRatingHandler` | Sim | Recalculo baseado em dados atuais; pode rodar N vezes |
| `SendPasswordResetEmailHandler` | Nao (envia e-mail duplicado) | Aceito; frequencia baixa; usuario pode ignorar duplicata |
| `RevokeUserTokensHandler` | Sim | Revogar token ja revogado e no-op |
| `IncrementThreadReplyCountHandler` | Nao | Risco de double-count; aceito para Release 2 |

**Premissa P-016:** Para `IncrementThreadReplyCountHandler`, se dupla execucao for problema em producao, substituir contador desnormalizado por consulta `SELECT COUNT(*) FROM replies WHERE thread_id = ?` no momento de leitura. Avaliar impacto de performance antes de decidir.
