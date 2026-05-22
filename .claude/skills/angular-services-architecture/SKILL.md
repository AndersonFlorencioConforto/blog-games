---
name: angular-services-architecture
description: Arquitetura de serviços HTTP tipados por domínio com models, interceptors e error handling. Use quando criar serviços HTTP Angular por domínio, definir models ou DTOs tipados, interceptors (auth, erro), ou padronizar o consumo de API.
argument-hint: "Domínios (User, Game, Thread, etc), tipos de operações (CRUD, queries)"
---

# Skill: Arquitetura de Serviços Angular

Use esta skill para desenhar serviços HTTP modulares, tipados e com padrão consistente de error handling.

## Entradas esperadas

- Lista de domínios do backend (User, Game, Auth, Thread, Reply, Rating, News, Report, etc.).
- Tipos de operações por domínio (list, getById, create, update, delete, custom actions).
- Padrão de paginação/sorting esperado.
- Estratégia de error handling (custom HttpErrorResponse, global interceptor, local handlers).

## Procedimento

1. **Crie models/DTOs**: Interfaces TypeScript espelhando contratos REST do backend.
2. **Serviço por domínio**: `UserService`, `GameService`, `AuthService`, etc.
3. **Métodos CRUD padrão**: `list()`, `getById(id)`, `create(dto)`, `update(id, dto)`, `delete(id)`.
4. **RxJS patterns**: Use `Observable`, `BehaviorSubject` para state local simples.
5. **HttpClient wrapper**: Métodos genéricos `get<T>`, `post<T>`, `put<T>`, `delete<T>` com base URL.
6. **Error handling**: Interceptor global ou try-catch em serviços, retry logic para requests.
7. **Autenticação**: Injete token via interceptor, refresh token automaticamente.
8. **Caching**: Implemente caching simples com ReplaySubject ou map para respostas.
9. **Tipos customizados**: `ApiResponse<T>`, `PaginatedResponse<T>`, `ApiError`.
10. **Tests mínimos**: Mock de HttpTestingController para serviços críticos.

## Mapa de serviços esperado (Blog Games)

```
AuthService
  - register(email, username, password): Observable<TokenResponse>
  - login(username, password): Observable<TokenResponse>
  - refreshToken(refreshToken): Observable<AccessTokenResponse>
  - logout(): void
  - getCurrentUser(): Observable<UserDTO>

UserService
  - list(page, limit): Observable<PaginatedResponse<UserDTO>>
  - getById(userId): Observable<UserDTO>
  - updateProfile(userId, dto): Observable<UserDTO>
  - follow(userId): Observable<void>
  - unfollow(userId): Observable<void>
  - getFollowers(userId): Observable<UserDTO[]>
  - getFollowing(userId): Observable<UserDTO[]>

GameService
  - list(filters): Observable<PaginatedResponse<GameDTO>>
  - getById(gameId): Observable<GameDTO>
  - create(dto): Observable<GameDTO>
  - getRatingSummary(gameId): Observable<RatingSummaryDTO>
  - rate(gameId, score): Observable<void>

ThreadService
  - list(gameId, page): Observable<PaginatedResponse<ThreadDTO>>
  - getById(threadId): Observable<ThreadDTO>
  - create(gameId, title, content): Observable<ThreadDTO>

ReplyService
  - listByThread(threadId): Observable<ReplyDTO[]>
  - create(threadId, content): Observable<ReplyDTO>

NewsService
  - list(page): Observable<PaginatedResponse<NewsDTO>>
  - getById(newsId): Observable<NewsDTO>
  - create(dto): Observable<NewsDTO>

ReportService
  - create(reportDto): Observable<void>
  - list(page): Observable<PaginatedResponse<ReportDTO>>
  - resolve(reportId, action): Observable<void>
```

## Verificações obrigatórias

- [ ] Models/DTOs coincidem com especificação REST do backend.
- [ ] Todos os serviços extende uma classe base ou segue padrão consistente.
- [ ] HttpClient é injetado em cada serviço.
- [ ] URL base é configurável via environment.
- [ ] Métodos retornam tipados `Observable<T>`.
- [ ] Error handling é consistente (throw HttpErrorResponse ou similar).
- [ ] Interceptor injeta Authorization header.
- [ ] Refresh token é tratado automaticamente em 401.
- [ ] Métodos genéricos `get<T>`, `post<T>`, etc. existem ou cada método é tipado.
- [ ] Testes mock HttpTestingController para pelo menos 1 serviço crítico.

## Formato de saída

1. Mapa de DTOs/models por serviço.
2. Conteúdo de 2-3 serviços exemplo (AuthService, GameService).
3. HttpClient wrapper genérico ou padrão per-método.
4. Error handling strategy (interceptor, retry, logging).
5. Estrutura de diretórios `src/app/core/services/`, `src/app/*/models/`.
6. Exemplos de uso em componentes.
7. Testes mínimos (1 serviço com HttpTestingController).

## Recursos

- [Template GenericHttpService](./templates/generic-http-service.template.ts)
- [Template AuthService](./templates/auth-service-full.template.ts)
- [Template UserService](./templates/user-service.template.ts)
- [Template Models/DTOs](./templates/models.template.ts)
- [Template HttpInterceptor](./templates/http-error-interceptor.template.ts)
