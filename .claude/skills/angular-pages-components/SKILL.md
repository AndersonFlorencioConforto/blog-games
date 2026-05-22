---
name: angular-pages-components
description: Geração de páginas e componentes por feature (auth, games, users, threads, admin). Use quando criar páginas ou componentes Angular de uma feature (list, detail, form, dashboard) e suas integrações com serviços.
argument-hint: "Feature name, página type (list, detail, form, dashboard), integrações de serviço"
---

# Skill: Páginas e Componentes Angular

Use esta skill para gerar páginas completas e componentes reutilizáveis por feature do Blog Games.

## Entradas esperadas

- Feature (auth, games, users, threads, news, admin, etc.).
- Tipo de página (list com paginação, detail, form com validação, dashboard).
- Serviços a integrar e ações esperadas.
- Estado (local component state vs global via store).
- Validações e regras de negócio.

## Procedimento

1. **Página container** (smart component): Gerencia lógica, chamadas a serviço, estado local.
2. **Componentes UI** (dumb components): Recebem @Input, emitem @Output, sem lógica de negócio.
3. **Formulários**: ReactiveForms com FormBuilder, validadores customizados, feedback de erros.
4. **Paginação**: Componente reutilizável ou integrado, comunicação com serviço.
5. **Loading/Error states**: Skeletons, spinners, mensagens de erro.
6. **Guards de acesso**: CanActivate por role (USER, ADMIN, MODERATOR).
7. **Testes**: Testes unitários para lógica e interações críticas.
8. **Acessibilidade**: aria-labels, semantic HTML, contrast.
9. **Responsividade**: Tailwind CSS ou CSS Grid/Flexbox.
10. **Documentação**: Exemplos de uso, story storybook.

## Páginas esperadas por feature

### Auth

- `LoginPage`: Form com email/password, error handling, redirect pós-sucesso.
- `RegisterPage`: Form com email/username/password, validações, agree terms.
- `ForgotPasswordPage` (opcional): Request reset via email.

### Games

- `GamesListPage`: Filtros, paginação, cards de jogo, navegação para detail.
- `GameDetailPage`: Detalhe completo, rating summary, review editorial, threads.
- `GameRatingComponent`: Form de rating 0-10, submit.
- `GameRateCompletePage` (opcional): Review após avaliação.

### Users

- `UserProfilePage`: Perfil do usuário logado/outro, bio, followers, following, shelf.
- `UserShelfPage`: Prateleiras (Wishlist, Playing, Completed, Dropped) com filtros.
- `UserFollowersPage`: Lista de seguidores com ação unfollow.
- `UserFollowingPage`: Lista de seguindo com ação unfollow.

### Threads

- `ThreadsListPage`: Por jogo, com paginação, sort por data/relevância.
- `ThreadDetailPage`: Conteúdo, replies, form de nova resposta, like button.
- `ThreadCreatePage`: Form para criar nova thread em jogo.

### News

- `NewsListPage`: Feed de notícias com paginação, cards.
- `NewsDetailPage`: Conteúdo full, metadata (autor, data).

### Admin

- `AdminGamesPage`: CRUD de games (list, create, edit).
- `AdminCategoriesPage`: CRUD de categorias.
- `AdminPlatformsPage`: CRUD de plataformas.
- `AdminReviewsPage`: Criar/editar revisões editoriais.
- `AdminNewsPage`: Criar/editar notícias.
- `AdminReportsPage`: Listar reports, resolver com ação.
- `AdminUsersPage`: Gerenciar usuários, roles, bans (opcional).

## Estrutura de componentes por página

```
features/games/
├── services/
│   └── game.service.ts
├── models/
│   └── game.dto.ts
├── components/
│   ├── game-card.component.ts      # Dumb, mostra game resumido
│   ├── game-rating.component.ts    # Dumb, form de rating
│   ├── game-detail.component.ts    # Dumb, detalhe expansível
│   └── ...
└── pages/
    ├── games-list/
    │   └── games-list.page.ts      # Smart, integra service + componentes
    ├── game-detail/
    │   └── game-detail.page.ts
    └── ...
```

## Verificações obrigatórias

- [ ] Cada página tem uma rota em `app.routes.ts`.
- [ ] Páginas container (smart) chamam serviços via `ngOnInit`.
- [ ] Componentes UI (dumb) usam @Input/@Output, sem serviço direto.
- [ ] Formulários usam ReactiveForms com validators.
- [ ] Loading/Error states mostram skeleton ou spinner.
- [ ] Guards protegem rotas por autenticação/role.
- [ ] Integração com serviços retorna Observable, usam async pipe.
- [ ] Erros do backend exibem mensagens amigáveis.
- [ ] Componentes possuem testes unitários (mínimo 1 por página crítica).
- [ ] Resposta responsiva (mobile-first com Tailwind ou media queries).

## Formato de saída

1. Estrutura de diretórios `src/app/features/[feature]/{services,models,components,pages}`.
2. Páginas exemplo com todas as features (form, loading, error, paginação).
3. Componentes compartilhados reutilizáveis (card, pagination, modal, skeleton).
4. Guards por role.
5. Testes unitários (1-2 páginas críticas).
6. Screenshots ou descrição visual do layout esperado.
7. Próximas tarefas (detalhes, animações, performance).

## Recursos

- [Template Page Component](./templates/page-component.template.ts)
- [Template Dumb Component](./templates/dumb-component.template.ts)
- [Template Reactive Form](./templates/reactive-form.template.ts)
- [Template Pagination](./templates/pagination.template.ts)
- [Template Role Guard](./templates/role-guard.template.ts)
- [Template Shared Components](./templates/shared-components.template.ts)
