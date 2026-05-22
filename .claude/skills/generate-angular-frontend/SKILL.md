---
name: generate-angular-frontend
description: Gera o frontend Angular do Blog Games com base nos docs e nas fases solicitadas.
argument-hint: "<fase> [extras] [restricoes]"
disable-model-invocation: true
---

# Prompt: Gerar Frontend Angular para Blog Games

Você foi chamado para gerar um **frontend Angular completo e modular** para a plataforma **Blog Games** baseado em:

- **Backend**: Java 25 + Spring Boot 4.0.5, Rest APIs documentadas em `docs/architecture/api-catalog.md`
- **Domínios**: 14 domínios com especificação em `docs/architecture/domains-architecture.md`
- **PRD**: Product requirements em `docs/prd/PRD-blog-games.md`
- **Security**: Matriz de segurança em `docs/architecture/security-matrix.md`
- **Manual**: Guia de testes end-to-end em `docs/manual-test-guide.md`

## Escopo da Geração

Seu objetivo é criar um **Frontend Angular modular, escalável e pronto para desenvolvimento**.

### Fases

**Fase 1: Bootstrap (Obrigatório)**

- Scaffold inicial com Angular 18+
- Estrutura de diretórios (core, shared, features)
- App routing com lazy loading
- Environment config (dev, prod)
- package.json com scripts e dependencies
- Instruções de setup (npm install, npm run dev)

**Fase 2: Core & Auth (Obrigatório)**

- AuthService (login, register, refresh, logout, getCurrentUser)
- JwtInterceptor (injeta token, refresh automático em 401)
- AuthGuard (protege rotas)
- RoleGuard (controla acesso por role: USER, ADMIN, MODERATOR, EDITOR)
- Login Page (form com email/password, validações, error handling)
- Register Page (form com email/username/password, validações)

**Fase 3: Serviços HTTP (Obrigatório)**

- Serviços por domínio:
  - `UserService`: profile, follow, unfollow, followers, following, shelf, favorite
  - `GameService`: list, getById, create (admin), getRatingSummary, rate
  - `ThreadService`: list por game, getById, create, update, delete
  - `ReplyService`: create, list, update, delete
  - `NewsService`: list, getById, create (admin), update, delete
  - `ReportService`: create, list (admin), resolve (admin)
  - `CategoryService`: list, create (admin)
  - `PlatformService`: list, create (admin)
  - `EditorialReviewService`: list, create (admin), update
- Models/DTOs tipados correspondendo aos contratos REST
- Error handling global com interceptor
- Retry logic para requests falhadas

**Fase 4: Páginas & Componentes (Obrigatório)**

- Dashboard/Home page
- Games:
  - GamesListPage (filtros, paginação, cards)
  - GameDetailPage (detalhe, rating, threads, reviews editoriais)
  - GameRatePage (form rating 0-10)
- Users:
  - UserProfilePage (perfil público/privado)
  - UserShelfPage (Wishlist, Playing, Completed, Dropped)
  - FollowersPage, FollowingPage
- Threads:
  - ThreadsListPage (por jogo, paginação)
  - ThreadDetailPage (detalhe com replies)
  - ThreadCreatePage (form criar thread)
- News:
  - NewsListPage (feed de notícias, paginação)
  - NewsDetailPage (detalhe)
- Admin:
  - AdminGamesPage (CRUD games)
  - AdminCategoriesPage (CRUD categories)
  - AdminPlatformsPage (CRUD platforms)
  - AdminReviewsPage (CRUD editorial reviews)
  - AdminNewsPage (CRUD news)
  - AdminReportsPage (listar, resolver reports)

### Componentes Compartilhados (Obrigatório)

- Header (navbar com menu, user dropdown, logout)
- Footer (links, copyright)
- Cards (game card, user card, thread card, news card)
- Pagination (list com prev/next, jump to page)
- Modal (generic, para confirmação, forms)
- Loading Spinner e Skeleton Loaders
- Error messages e Toast notifications
- Breadcrumbs

### Validações & UX

- Validações em formulários (email format, username length, password strength)
- Mensagens de erro do backend exibidas em UI
- Loading states durante chamadas HTTP
- Empty states (nenhum resultado)
- Unsaved changes guard em forms
- Confirmação antes de delete

### Autenticação & Autorização

- JWT com access token (15 min) + refresh token (30 dias)
- Renovação automática de access token via refresh endpoint
- Logout revoga tokens locais
- Guards protegem rotas por autenticação e role
- Redirecionamento pós-login para referrer ou dashboard

### Build & Deploy

- `npm run dev`: Inicia dev server (localhost:4200)
- `npm run build`: Build otimizado para produção
- `npm run preview`: Preview do build
- `npm run lint`: ESLint check
- Environment variables por profile
- Docker support (opcional: Dockerfile para frontend)

### Testes Mínimos

- Testes unitários para serviços críticos (AuthService, GameService)
- Testes de componentes inteligentes (pages)
- Mocks de HttpTestingController
- Coverage mínimo 50%

### Documentação

- README.md com setup, estrutura, decisões arquiteturais
- Inline code comments para lógica complexa
- Exemplos de integração com backend
- Guia de adicionar nova página/feature

## Entradas Esperadas

Quando você receber um pedido de geração, o usuário deve fornecer:

```
Gere o frontend Angular para Blog Games com:
- Fase [1/2/3/4/all]: Qual fase? (default: all)
- Features extras (ngRx para state management, Tailwind CSS, etc.)
- Restrições (browsers alvo, tamanho máximo bundle, etc.)
- Preferences (CSS framework: Tailwind/Bootstrap/Material)
```

Exemplo:

```
Fase: all
Features: Tailwind CSS, ngRx, Storybook
Restrições: Chrome 120+, Safari 17+, bundle < 500KB
```

## Saída Esperada

1. **Estrutura de diretórios**: Criada com arquivo scaffold mínimos
2. **Arquivos principais**:
   - `angular.json`, `package.json`, `tsconfig.json`
   - `src/app/{core,shared,features}` estrutura
   - `src/environments/{environment,environment.prod}.ts`
   - `src/app/app.routes.ts`, `src/app/app.config.ts`
3. **Instruções de setup**:
   ```bash
   npm install
   npm run dev        # Dev server
   npm run build      # Build production
   npm run preview    # Preview do build
   ```
4. **Mapa de rotas**: Documento de rotas <-> páginas/componentes
5. **Modelos esperados**: DTOs e tipos por serviço
6. **Próximas tarefas**: Lista de features a implementar em detalhe

## Skills Disponíveis

Use as seguintes skills conforme necessário:

1. **angular-app-bootstrap**: Scaffold inicial, roteamento, ambiente
2. **angular-services-architecture**: Serviços HTTP tipados, modelos
3. **angular-pages-components**: Páginas, componentes, formulários
4. **angular-routing-lazy-loading**: Roteamento avançado, guards, lazy loading

## Agentes Disponíveis

- **frontend-angular-generator** (este agente): Orquestra toda a geração
- Use subagentes de geração de código conforme necessário

## Checklist de Completude

Antes de finalizar, verifique:

- [ ] Todas as rotas da API catalog estão representadas
- [ ] Autenticação JWT funciona (login, refresh, logout)
- [ ] Guards protegem rotas por role
- [ ] Componentes usam @Input/@Output (dumb + smart)
- [ ] Serviços retornam Observable<T> tipados
- [ ] Formulários usam ReactiveForms com validators
- [ ] Loading/Error states implementados
- [ ] Responsividade testada (mobile, tablet, desktop)
- [ ] Scripts npm funcionam (dev, build, preview)
- [ ] README completo com exemplos
- [ ] Testes mínimos para serviços críticos
- [ ] Sem errors/warnings na console
- [ ] Integração com backend testada manualmente

## Referências

- Backend API: `http://localhost:8080/api/v1`
- API Catalog: `docs/architecture/api-catalog.md`
- Security Matrix: `docs/architecture/security-matrix.md`
- Domain Architecture: `docs/architecture/domains-architecture.md`
- Manual Test Guide: `docs/manual-test-guide.md`
- PRD: `docs/prd/PRD-blog-games.md`

---

**Use este prompt como entrada para gerar o frontend Angular completo ou por fases.**

## Estilo: Paleta e layout (usar game-shelf)

O frontend gerado deve reproduzir a identidade visual do projeto `game-shelf`. Extraia e use diretamente as variáveis CSS e utilitários listados abaixo — prefira usar as variáveis em `:root` e as classes utilitárias em vez de cores hard-coded.

- Fontes:
  - `--font-display`: "Space Grotesk"
  - `--font-sans`: "DM Sans"

- Tokens principais (use `var(--token)`):
  - `--primary`: oklch(0.69 0.19 38)
  - `--primary-foreground`: oklch(0.16 0.005 30)
  - `--secondary`: oklch(0.26 0.008 30)
  - `--secondary-foreground`: oklch(0.96 0.01 60)
  - `--muted`: oklch(0.26 0.008 30)
  - `--muted-foreground`: oklch(0.7 0.02 50)
  - `--accent`: oklch(0.32 0.05 35)
  - `--accent-foreground`: oklch(0.96 0.01 60)
  - `--destructive`: oklch(0.62 0.22 25)
  - `--destructive-foreground`: oklch(0.98 0.01 60)
  - `--border`: oklch(0.3 0.008 30)
  - `--input`: oklch(0.26 0.008 30)
  - `--ring`: oklch(0.69 0.19 38)
  - `--ember`: oklch(0.69 0.19 38)
  - `--ember-glow`: oklch(0.78 0.16 50)
  - `--gradient-ember`: linear-gradient(135deg, oklch(0.69 0.19 38), oklch(0.78 0.16 50))
  - `--background`: oklch(0.16 0.005 30)
  - `--foreground`: oklch(0.96 0.01 60)
  - `--surface`: oklch(0.21 0.006 30)
  - `--surface-2`: oklch(0.26 0.008 30)

- Utilitários e classes a preservar/usar:
  - `.bg-gradient-ember` (usa `--gradient-ember`)
  - `.text-gradient-ember` (texto com background-clip)
  - `.shadow-ember`, `.shadow-card`
  - `.glass` (fundo translúcido + backdrop blur)
  - classes de status: `bg-card`, `bg-popover`, `text-muted-foreground`, `text-primary`, `border-border`, `bg-background`

- Exemplos de uso no scaffold:
  - Importar `styles.css` ou mapear tokens para `tailwind.config.js` theme.
  - Usar `var(--primary)` para botões primários e `--primary-foreground` para textos sobre eles.
  - Utilizar `.glass` em headers sticky e `.bg-gradient-ember` em badges/ctas.

Observação: NÃO gerar componentes React — o prompt deve instruir agentes a criar apenas templates Angular, componentes e estilos que repliquem visualmente `game-shelf` usando os tokens acima.
