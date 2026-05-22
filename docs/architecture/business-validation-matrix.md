# Matriz de Validacoes de Negocio - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21

---

## Convencao

- **Camada de Validacao:**
  - `INPUT` - Validacao de formato/obrigatoriedade (Bean Validation / anotacoes Jakarta no DTO de entrada, na camada `infrastructure/controller`)
  - `DOMAIN` - Regra de negocio verificada na entidade ou value object (camada `domain`)
  - `APP` - Regra verificada no caso de uso antes de chamar o repositorio (camada `application`)
  - `DB` - Constraint de banco como ultima linha de defesa
- **Erro esperado:** Excecao de dominio + HTTP status correspondente
- **Teste:** Nome do teste unitario/integracao esperado

---

## 1. Registro de Usuario

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| U-01 | Email e obrigatorio | INPUT | `422` - campo obrigatorio | `RegisterUserUseCaseTest.shouldFailWhenEmailIsBlank` |
| U-02 | Email deve ter formato valido (RFC 5322) | INPUT + DOMAIN (Email VO) | `422` | `EmailValueObjectTest.shouldRejectInvalidEmailFormat` |
| U-03 | Email deve ser unico no sistema | APP (consulta repo) | `DuplicateEntityException` / `409` | `RegisterUserUseCaseTest.shouldFailWhenEmailAlreadyExists` |
| U-04 | Senha minimo 8 caracteres | INPUT + DOMAIN | `422` | `RegisterUserUseCaseTest.shouldFailWhenPasswordTooShort` |
| U-05 | Senha requer: 1 maiuscula, 1 minuscula, 1 numero, 1 especial | INPUT + DOMAIN | `422` | `RegisterUserUseCaseTest.shouldFailWhenPasswordDoesNotMeetPolicy` |
| U-06 | Nome obrigatorio, 2-100 chars | INPUT | `422` | `RegisterUserUseCaseTest.shouldFailWhenNameIsBlank` |

---

## 2. Login e Autenticacao

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| A-01 | Email e senha sao obrigatorios | INPUT | `422` | `LoginUseCaseTest.shouldFailWhenCredentialsAreBlank` |
| A-02 | Credenciais invalidas nao revelam qual campo esta errado | APP | `InvalidCredentialsException` / `401` | `LoginUseCaseTest.shouldReturn401ForWrongPassword` |
| A-03 | Usuario banido nao pode autenticar | APP | `UserBannedException` / `403` | `LoginUseCaseTest.shouldFailWhenUserIsBanned` |
| A-04 | Token JWT invalido (assinatura) rejeita requisicao | INFRA (filtro) | `401` | `JwtFilterTest.shouldRejectTokenWithInvalidSignature` |
| A-05 | Token JWT expirado rejeita requisicao | INFRA (filtro) | `401` | `JwtFilterTest.shouldRejectExpiredToken` |
| A-06 | JTI presente na denylist rejeita requisicao | INFRA (filtro) | `401` | `JwtFilterTest.shouldRejectBlocklistedToken` |
| A-07 | Refresh token expirado rejeita renovacao | APP | `TokenExpiredException` / `401` | `RefreshTokenUseCaseTest.shouldFailWhenRefreshTokenExpired` |
| A-08 | Refresh token ja revogado rejeita renovacao | APP | `TokenExpiredException` / `401` | `RefreshTokenUseCaseTest.shouldFailWhenRefreshTokenRevoked` |

---

## 3. Recuperacao de Senha

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| P-01 | Endpoint forgot-password retorna 202 independente de email existir | APP | `202 ACCEPTED` (sempre) | `RequestPasswordResetUseCaseTest.shouldReturn202EvenWhenEmailNotFound` |
| P-02 | Token de reset expira em 1 hora | APP (verifica expires_at) | `TokenExpiredException` / `400` | `ResetPasswordUseCaseTest.shouldFailWhenResetTokenExpired` |
| P-03 | Token de reset e de uso unico | APP (verifica used_at) | `TokenAlreadyUsedException` / `400` | `ResetPasswordUseCaseTest.shouldFailWhenTokenAlreadyUsed` |
| P-04 | Nova senha deve atender politica de senha | INPUT + DOMAIN | `422` | `ResetPasswordUseCaseTest.shouldFailWhenNewPasswordIsWeak` |
| P-05 | Apos reset, todos os refresh tokens do usuario sao revogados | APP | - (comportamento esperado) | `ResetPasswordUseCaseTest.shouldRevokeAllRefreshTokensAfterReset` |

---

## 4. Gestao de Jogos

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| G-01 | Apenas ADMIN pode criar/editar/deletar jogo | INFRA (Spring Security) | `403` | `GameControllerSecurityTest.shouldReturn403WhenUserTriesToCreateGame` |
| G-02 | Titulo e obrigatorio, 1-200 chars | INPUT | `422` | `CreateGameUseCaseTest.shouldFailWhenTitleIsBlank` |
| G-03 | Titulo deve ser unico | APP | `DuplicateEntityException` / `409` | `CreateGameUseCaseTest.shouldFailWhenTitleAlreadyExists` |
| G-04 | platformScore deve estar entre 0.0 e 10.0 | INPUT + DOMAIN (PlatformScore VO) | `422` | `PlatformScoreValueObjectTest.shouldRejectScoreOutOfRange` |
| G-05 | Ao menos uma categoria obrigatoria | INPUT + DOMAIN | `422` | `CreateGameUseCaseTest.shouldFailWhenNoCategoryProvided` |
| G-06 | Ao menos uma plataforma obrigatoria | INPUT + DOMAIN | `422` | `CreateGameUseCaseTest.shouldFailWhenNoPlatformProvided` |
| G-07 | Categoria deve ser valor valido do enum `Category` | INPUT | `422` | `CreateGameUseCaseTest.shouldFailWhenCategoryIsInvalid` |
| G-08 | Plataforma deve ser valor valido do enum `Platform` | INPUT | `422` | `CreateGameUseCaseTest.shouldFailWhenPlatformIsInvalid` |
| G-09 | Jogo deletado (soft delete) nao aparece em listagens | APP (filtro no repositorio) | - (comportamento de leitura) | `ListGamesUseCaseTest.shouldNotReturnDeletedGames` |

---

## 5. Avaliacoes de Jogo

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| R-01 | Stars deve ser inteiro entre 0 e 5 inclusive | INPUT + DOMAIN (Stars VO) | `422` | `StarsValueObjectTest.shouldRejectStarsOutOfRange` |
| R-02 | Jogo avaliado deve existir | APP | `EntityNotFoundException` / `404` | `RateGameUseCaseTest.shouldFailWhenGameNotFound` |
| R-03 | Segunda avaliacao do mesmo usuario atualiza; nao duplica | APP (upsert) | - | `RateGameUseCaseTest.shouldUpdateExistingRating` |
| R-04 | Media e atualizada atomicamente apos avaliacao | APP + DOMAIN | - (consistencia) | `RateGameUseCaseTest.shouldUpdateAverageAfterRating` |

---

## 6. Estante do Usuario

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| S-01 | Status deve ser JA_TENHO, PRETENDO_PEGAR ou FAVORITO | INPUT + DOMAIN (ShelfStatus VO) | `422` | `AddToShelfUseCaseTest.shouldFailWhenStatusIsInvalid` |
| S-02 | Nao pode adicionar jogo ja presente na estante (use PUT) | APP | `DuplicateEntityException` / `409` | `AddToShelfUseCaseTest.shouldFailWhenGameAlreadyInShelf` |
| S-03 | Jogo deve existir para ser adicionado | APP | `EntityNotFoundException` / `404` | `AddToShelfUseCaseTest.shouldFailWhenGameNotFound` |
| S-04 | Usuario so pode modificar propria estante | APP (verifica ownership) | `ForbiddenOperationException` / `403` | `UpdateShelfItemUseCaseTest.shouldFailWhenUserDoesNotOwnShelf` |
| S-05 | Remover jogo nao presente na estante e idempotente (nao gera erro) | APP | `204` sem erro | `RemoveFromShelfUseCaseTest.shouldSucceedEvenWhenGameNotInShelf` |

---

## 7. Social (Seguir/Deixar de Seguir)

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| F-01 | Usuario nao pode seguir a si mesmo | DOMAIN (Follow invariante) | `SelfReferenceException` / `400` | `FollowUserUseCaseTest.shouldFailWhenFollowingSelf` |
| F-02 | Seguir usuario ja seguido e idempotente | APP | `204` sem erro | `FollowUserUseCaseTest.shouldBeIdempotentWhenAlreadyFollowing` |
| F-03 | Usuario seguido deve existir | APP | `EntityNotFoundException` / `404` | `FollowUserUseCaseTest.shouldFailWhenUserNotFound` |

---

## 8. Threads e Respostas

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| T-01 | Titulo da thread obrigatorio, 5-200 chars | INPUT | `422` | `CreateThreadUseCaseTest.shouldFailWhenTitleIsTooShort` |
| T-02 | Conteudo da thread obrigatorio, 10-5000 chars | INPUT | `422` | `CreateThreadUseCaseTest.shouldFailWhenContentIsBlank` |
| T-03 | Jogo deve existir para abrir thread | APP | `EntityNotFoundException` / `404` | `CreateThreadUseCaseTest.shouldFailWhenGameNotFound` |
| T-04 | Conteudo de reply obrigatorio, 1-2000 chars | INPUT | `422` | `ReplyToThreadUseCaseTest.shouldFailWhenContentIsBlank` |
| T-05 | Thread deve existir para receber reply | APP | `EntityNotFoundException` / `404` | `ReplyToThreadUseCaseTest.shouldFailWhenThreadNotFound` |
| T-06 | Like em thread e idempotente (segundo like ignorado) | APP | `204` sem duplicar | `LikeThreadUseCaseTest.shouldBeIdempotentWhenAlreadyLiked` |
| T-07 | Like em reply e idempotente | APP | `204` sem duplicar | `LikeReplyUseCaseTest.shouldBeIdempotentWhenAlreadyLiked` |
| T-08 | Usuario banido nao pode criar thread ou reply | INFRA (filtro pre-requisicao) | `403` | `JwtFilterTest.shouldReturn403ForBannedUser` |

---

## 9. Noticias

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| N-01 | Apenas ADMIN pode criar/editar/deletar noticias | INFRA (Spring Security) | `403` | `NewsControllerSecurityTest.shouldReturn403WhenUserTriesToCreateNews` |
| N-02 | Titulo obrigatorio, 5-300 chars | INPUT | `422` | `CreateNewsUseCaseTest.shouldFailWhenTitleIsBlank` |
| N-03 | Summary obrigatorio, 10-500 chars | INPUT | `422` | `CreateNewsUseCaseTest.shouldFailWhenSummaryIsBlank` |
| N-04 | Content obrigatorio, minimo 50 chars | INPUT | `422` | `CreateNewsUseCaseTest.shouldFailWhenContentIsTooShort` |
| N-05 | relatedGameId deve referenciar jogo existente (se fornecido) | APP | `EntityNotFoundException` / `404` | `CreateNewsUseCaseTest.shouldFailWhenRelatedGameNotFound` |

---

## 10. Denuncias e Moderacao

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| M-01 | Usuario nao pode denunciar a si mesmo | DOMAIN (Report invariante) | `SelfReferenceException` / `400` | `ReportUserUseCaseTest.shouldFailWhenReportingSelf` |
| M-02 | Reason deve ser valor valido do enum `ReportReason` | INPUT | `422` | `ReportUserUseCaseTest.shouldFailWhenReasonIsInvalid` |
| M-03 | Usuario denunciado deve existir | APP | `EntityNotFoundException` / `404` | `ReportUserUseCaseTest.shouldFailWhenReportedUserNotFound` |
| M-04 | Denuncia ja resolvida nao pode ser resolvida novamente | APP | `BusinessRuleViolationException` / `409` | `ResolveReportUseCaseTest.shouldFailWhenReportAlreadyResolved` |
| M-05 | Apenas ADMIN pode ver e resolver denuncias | INFRA (Spring Security) | `403` | `ReportControllerSecurityTest.shouldReturn403ForNonAdmin` |
| M-06 | banDurationDays deve ser >= 1 quando decision = APPROVED | INPUT + APP | `422` | `ResolveReportUseCaseTest.shouldFailWhenBanDurationIsInvalidForApproval` |
| M-07 | Decision deve ser APPROVED ou REJECTED | INPUT | `422` | `ResolveReportUseCaseTest.shouldFailWhenDecisionIsInvalid` |

---

## 11. Perfil do Usuario

| # | Regra | Camada | Excecao / HTTP | Teste |
|---|---|---|---|---|
| PR-01 | Nome obrigatorio no update, 2-100 chars | INPUT | `422` | `UpdateUserProfileUseCaseTest.shouldFailWhenNameIsBlank` |
| PR-02 | Bio opcional, max 500 chars | INPUT | `422` | `UpdateUserProfileUseCaseTest.shouldFailWhenBioExceedsLimit` |
| PR-03 | avatarUrl deve ser URL valida (se fornecida) | INPUT | `422` | `UpdateUserProfileUseCaseTest.shouldFailWhenAvatarUrlIsInvalid` |
| PR-04 | Usuario so pode editar o proprio perfil | APP (verifica ownership via SecurityContext) | `ForbiddenOperationException` / `403` | `UpdateUserProfileUseCaseTest.shouldFailWhenEditingAnotherUserProfile` |
