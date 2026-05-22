---
name: angular-app-bootstrap
description: Scaffold de aplicação Angular com configurações de base, roteamento, autenticação e ambiente por profile. Use quando iniciar um frontend Angular novo, configurar o projeto base, ambientes por profile, ou a estrutura inicial de autenticação e roteamento.
argument-hint: "Nome do projeto, versão Angular, features (auth, routing, state-management)"
---

# Skill: Bootstrap de Aplicação Angular

Use esta skill para criar um scaffold inicial de uma aplicação Angular pronta para desenvolvimento modular.

## Entradas esperadas

- Nome do projeto (ex: blog-games-frontend)
- Versão Angular (recomendado: 18+)
- Features iniciais (auth, routing, state-management, http-client)
- Restrições (browsers alvo, tamanho de bundle, performance)

## Procedimento

1. **Crie estrutura de diretórios**: `src/app/{core,shared,features}`, `environments/`, `assets/`.
2. **Configure Angular (`angular.json`)**: Build configurations, assets, styles, outputHashing.
3. **Setup TypeScript (`tsconfig.json`)**: Strict mode habilitado, paths alias para imports limpos.
4. **App config (`app.config.ts`)**: Providers para HttpClient, Router, Security (guards/interceptors).
5. **Roteamento (`app.routes.ts`)**: Feature-based lazy loading, default route, wildcard route.
6. **Ambiente (`environment.ts`, `environment.prod.ts`)**: API base URLs, feature flags, logging level.
7. **Core services**: `AuthService`, `HttpService`, `StorageService`.
8. **Auth guard e interceptor**: Proteção de rotas, injeta token em requisições.
9. **Package.json**: Scripts (dev, build, preview, lint, test).
10. **README**: Setup, estrutura, próximos passos.

## Verificações obrigatórias

- [ ] `angular.json` com configurations para development e production.
- [ ] `tsconfig.json` com `strict: true` e paths alias definidos.
- [ ] `app.config.ts` fornece HttpClientModule, RouterModule, Security providers.
- [ ] `app.routes.ts` com roteamento feature-based e lazy loading.
- [ ] `environment.ts` e `environment.prod.ts` definem API base URL.
- [ ] AuthService mínimo com login, logout, getCurrentUser.
- [ ] AuthGuard e JwtInterceptor funcionam com token storage.
- [ ] `package.json` com scripts dev/build/preview.
- [ ] Node modules instalados com `npm install` ou `yarn install`.
- [ ] `npm run dev` inicia dev server em http://localhost:4200.

## Formato de saída

1. Estrutura de diretórios com arquivos.
2. Conteúdo de arquivo principal (app.routes.ts, app.config.ts).
3. Environment files (development, production).
4. Package.json com dependencies.
5. Instruções de setup e build.
6. Próximas tarefas de geração de features.

## Recursos

- [Template app.routes.ts](./templates/app-routes.template.ts)
- [Template app.config.ts](./templates/app-config.template.ts)
- [Template environment.ts](./templates/environment.template.ts)
- [Template AuthService](./templates/auth-service.template.ts)
- [Template Auth Guard](./templates/auth-guard.template.ts)
- [Template JWT Interceptor](./templates/jwt-interceptor.template.ts)
- [Template package.json](./templates/package-template.json)
