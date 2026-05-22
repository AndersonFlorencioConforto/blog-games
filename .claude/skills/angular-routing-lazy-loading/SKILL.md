---
name: angular-routing-lazy-loading
description: Configuração de roteamento feature-based com lazy loading, guards e resolvers. Use quando configurar rotas Angular, lazy loading por feature, guards de autenticação ou role, ou resolvers de dados.
argument-hint: "Features list, roles/guards requirements, lazy load strategy"
---

# Skill: Roteamento e Lazy Loading Angular

Use esta skill para configurar roteamento feature-based com lazy loading, guards e resolvers.

## Entradas esperadas

- Lista de features/módulos (auth, games, users, admin, etc.).
- Rotas públicas vs protegidas.
- Roles que acessam cada rota (USER, ADMIN, MODERATOR, EDITOR).
- Estratégia de lazy load (por feature, por role, eager para críticas).
- Redirects esperados (login redirect, 404, unauthorized).

## Procedimento

1. **App routes**: Define estrutura principal com children para features.
2. **Feature routes**: Cada feature exporta suas rotas em `.routes.ts`.
3. **Lazy loading**: Usar `loadChildren: () => import(...).then(m => m.ROUTES)`.
4. **Guards**: `canActivate`, `canActivateChild`, `canDeactivate` (unsaved changes).
5. **Resolvers**: Carregar dados antes de ativar rota (opcional para pesado).
6. **Route metadata**: Title, roles required, breadcrumbs.
7. **Error routes**: 404, 403 (unauthorized), 500.
8. **Redirects**: Login -> referrer ou dashboard, old routes -> new.
9. **Preloading**: Estratégia de pre-loading para melhor UX (opcional).
10. **Analytics**: Logging de navegação para insights (opcional).

## Mapa de rotas esperado (Blog Games)

```
/                               -> Dashboard (se logado) ou login
├── auth/
│   ├── login                   -> LoginPage (público)
│   ├── register                -> RegisterPage (público)
│   └── forgot-password         -> ForgotPasswordPage (público)
├── games
│   ├── (list)                  -> GamesListPage (público, lazy)
│   ├── :id                     -> GameDetailPage (público, lazy)
│   └── :id/rate               -> GameRatePage (logado, lazy)
├── users
│   ├── profile/:id             -> UserProfilePage (público, lazy)
│   ├── :id/followers          -> FollowersPage (público, lazy)
│   ├── :id/following          -> FollowingPage (público, lazy)
│   └── me/shelf               -> ShelfPage (logado, lazy)
├── threads
│   ├── game/:gameId            -> ThreadsListPage (público, lazy)
│   ├── :id                     -> ThreadDetailPage (público, lazy)
│   └── create                  -> ThreadCreatePage (logado, lazy)
├── news
│   ├── (list)                  -> NewsListPage (público, lazy)
│   └── :id                     -> NewsDetailPage (público, lazy)
├── admin (role: ADMIN|EDITOR|MODERATOR, lazy)
│   ├── games                   -> AdminGamesPage
│   ├── categories              -> AdminCategoriesPage
│   ├── platforms               -> AdminPlatformsPage
│   ├── reviews                 -> AdminReviewsPage
│   ├── news                    -> AdminNewsPage
│   ├── reports                 -> AdminReportsPage
│   └── users                   -> AdminUsersPage (ADMIN only)
├── 404                         -> NotFoundPage
├── 403                         -> UnauthorizedPage
└── **                          -> redirect 404
```

## Verificações obrigatórias

- [ ] `app.routes.ts` define estrutura principal com lazy loading.
- [ ] Cada feature exporta `ROUTES` em seu `.routes.ts`.
- [ ] Guards `AuthGuard`, `RoleGuard` protegem rotas.
- [ ] Redirecionamento após login para rota anterior (referrer).
- [ ] Lazy loading reduz bundle inicial.
- [ ] Route metadata (title, roles) preenchido.
- [ ] 404 e 403 pages existem.
- [ ] Rota default (/) redireciona logicamente.
- [ ] Sem erros de rota na console.
- [ ] Navegação funciona com browser back/forward.

## Formato de saída

1. Estrutura de `app.routes.ts` com lazy loading.
2. Exemplo de feature `.routes.ts`.
3. Implementação de guards (AuthGuard, RoleGuard).
4. Mapa visual de rotas e acesso.
5. Instruções para adicionar nova rota.
6. Próximas tarefas (resolvers, preloading, analytics).

## Recursos

- [Template app.routes.ts](./templates/app-routes-lazy.template.ts)
- [Template feature.routes.ts](./templates/feature-routes.template.ts)
- [Template AuthGuard](./templates/auth-guard.template.ts)
- [Template RoleGuard](./templates/role-guard.template.ts)
- [Template PageTitleStrategy](./templates/page-title-strategy.template.ts)
