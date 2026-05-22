---
paths:
  - "**/*.ts"
  - "**/*.html"
  - "**/*.scss"
  - "**/*.css"
---

# Convencoes para Codigo Angular em Projetos Modulares

Este arquivo define convencoes para desenvolvimento Angular no projeto Blog Games.

## Princípios Gerais

1. **Arquitetura em camadas**: `core`, `shared`, `features`
2. **Smart + Dumb components**: Separação clara de responsabilidades
3. **Serviços HTTP tipados**: Observable<T>, sem `any`
4. **RxJS patterns**: Pipe operators, takeUntilDestroyed
5. **Lazy loading por feature**: Cada feature é roteada com lazy loading
6. **Segurança**: JWT interceptor, auth guard, role guard
7. **Validação**: ReactiveForms com validators customizados
8. **Testes**: Mínimo happy path + error cases críticos

## Estrutura de Diretórios

```
src/app/
├── core/                          # Singleton services, guards, interceptors
│   ├── guards/
│   │   ├── auth.guard.ts          # Protege rotas por autenticação
│   │   └── role.guard.ts          # Protege rotas por role
│   ├── interceptors/
│   │   ├── jwt.interceptor.ts     # Injeta token, refresh automático
│   │   └── error.interceptor.ts   # Trata erros globais
│   ├── services/
│   │   ├── auth.service.ts        # Autenticação (login, register, refresh)
│   │   ├── http.service.ts        # Wrapper genérico de HttpClient
│   │   └── storage.service.ts     # localStorage/sessionStorage helpers
│   └── models/
│       └── auth.models.ts         # Tipos de autenticação
├── shared/                         # Componentes, pipes, directivas reutilizáveis
│   ├── components/
│   │   ├── header/
│   │   ├── footer/
│   │   ├── cards/
│   │   ├── pagination/
│   │   ├── modal/
│   │   ├── loading/
│   │   └── error-message/
│   └── pipes/
│       ├── safe-html.pipe.ts
│       └── date-format.pipe.ts
├── features/                       # Domínios de negócio
│   ├── auth/
│   │   ├── pages/
│   │   │   ├── login/
│   │   │   └── register/
│   │   ├── services/
│   │   │   └── auth.service.ts    # Customizações locais se necessário
│   │   ├── models/
│   │   │   └── auth.dto.ts
│   │   └── auth.routes.ts
│   ├── games/
│   │   ├── pages/
│   │   │   ├── games-list/
│   │   │   ├── game-detail/
│   │   │   └── game-rate/
│   │   ├── components/
│   │   │   ├── game-card.component.ts
│   │   │   └── rating-form.component.ts
│   │   ├── services/
│   │   │   └── game.service.ts
│   │   ├── models/
│   │   │   └── game.dto.ts
│   │   └── games.routes.ts
│   ├── users/
│   ├── threads/
│   ├── news/
│   ├── admin/
│   └── [...outras features]
├── app.component.ts               # Root component
├── app.routes.ts                  # Roteamento principal com lazy loading
└── app.config.ts                  # Providers (HttpClient, Guards, Interceptors)
```

## Convencoes de Nomenclatura

### Arquivos e Diretórios

- **Component**: `*.component.ts` (ex: `login.component.ts`)
- **Service**: `*.service.ts` (ex: `game.service.ts`)
- **Guard**: `*.guard.ts` (ex: `auth.guard.ts`)
- **Interceptor**: `*.interceptor.ts` (ex: `jwt.interceptor.ts`)
- **Model/DTO**: `*.dto.ts` ou `*.model.ts` (ex: `game.dto.ts`)
- **Pipe**: `*.pipe.ts` (ex: `date-format.pipe.ts`)
- **Rotas**: `*.routes.ts` (ex: `games.routes.ts`)

### Classes e Exports

```typescript
// Component
export class LoginComponent {}

// Service
export class GameService {}

// Guard
export class AuthGuard {}

// Interceptor
export class JwtInterceptor {}

// Modelo
export interface GameDTO { ... }
export interface CreateGameDTO { ... }
```

## Componentes

### Smart Component (Page/Container)

```typescript
// games/pages/games-list/games-list.page.ts
import { Component, OnInit, OnDestroy } from "@angular/core";
import { Observable } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
  selector: "app-games-list-page",
  template: `
    <div class="games-container">
      <h1>Jogos</h1>
      @if (loading$ | async) {
        <app-loading-spinner />
      } @else {
        <app-game-list [games]="games$ | async" />
      }
    </div>
  `,
})
export class GamesListPage implements OnInit {
  games$: Observable<GameDTO[]>;
  loading$: Observable<boolean>;

  constructor(private gameService: GameService) {}

  ngOnInit() {
    this.games$ = this.gameService.list();
    this.loading$ = this.gameService.loading$;
  }
}
```

**Características**:

- Chama serviços diretamente
- Gerencia estado local com Observable
- Não valida, não formata
- Passa dados para componentes dumb via @Input

### Dumb Component (UI/Presentational)

```typescript
// games/components/game-card.component.ts
import { Component, Input, Output, EventEmitter } from "@angular/core";

@Component({
  selector: "app-game-card",
  template: `
    <div class="card">
      <img [src]="game.coverUrl" alt="{{ game.title }}" />
      <h3>{{ game.title }}</h3>
      <p>{{ game.description }}</p>
      <button (click)="onSelect.emit(game.id)">Ver Detalhes</button>
    </div>
  `,
  styleUrl: "./game-card.component.css",
})
export class GameCardComponent {
  @Input() game!: GameDTO;
  @Output() onSelect = new EventEmitter<string>();
}
```

**Características**:

- Recebe dados via @Input
- Emite eventos via @Output
- Sem lógica de negócio
- Sem chamadas a serviços
- Reutilizável em múltiplos contextos

## Serviços HTTP

### Padrão Genérico

```typescript
// core/services/http.service.ts
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: "root" })
export class HttpService {
  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  get<T>(path: string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${path}`);
  }

  post<T>(path: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${path}`, body);
  }

  put<T>(path: string, body: any): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${path}`, body);
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${path}`);
  }
}
```

### Serviço por Domínio

```typescript
// features/games/services/game.service.ts
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { HttpService } from "../../../core/services/http.service";
import { GameDTO, CreateGameDTO, RatingSummaryDTO } from "../models/game.dto";

@Injectable({ providedIn: "root" })
export class GameService {
  loading$ = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpService) {}

  list(page: number = 1, limit: number = 20): Observable<GameDTO[]> {
    this.loading$.next(true);
    return this.http
      .get<GameDTO[]>(`/games?page=${page}&limit=${limit}`)
      .pipe(tap(() => this.loading$.next(false)));
  }

  getById(id: string): Observable<GameDTO> {
    return this.http.get<GameDTO>(`/games/${id}`);
  }

  create(dto: CreateGameDTO): Observable<GameDTO> {
    return this.http.post<GameDTO>("/games", dto);
  }

  getRatingSummary(id: string): Observable<RatingSummaryDTO> {
    return this.http.get<RatingSummaryDTO>(`/games/${id}/rating-summary`);
  }

  rate(gameId: string, score: number): Observable<void> {
    return this.http.post<void>(`/games/${gameId}/rate`, { score });
  }
}
```

**Características**:

- Observable<T> sempre tipado
- BehaviorSubject para estado local
- Métodos CRUD padrão
- Retry/error handling via interceptor

## Autenticação

### AuthService

```typescript
// core/services/auth.service.ts
import { Injectable, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { tap, catchError } from "rxjs/operators";
import { TokenResponse, UserDTO } from "../models/auth.models";

@Injectable({ providedIn: "root" })
export class AuthService {
  currentUser = signal<UserDTO | null>(null);
  isAuthenticated = signal(false);

  constructor(
    private http: HttpClient,
    private storage: StorageService,
  ) {}

  login(username: string, password: string): Observable<TokenResponse> {
    return this.http
      .post<TokenResponse>("/auth/login", { username, password })
      .pipe(tap((response) => this.setTokens(response)));
  }

  register(
    email: string,
    username: string,
    password: string,
  ): Observable<TokenResponse> {
    return this.http
      .post<TokenResponse>("/auth/register", {
        email,
        username,
        password,
      })
      .pipe(tap((response) => this.setTokens(response)));
  }

  refreshToken(): Observable<{ accessToken: string }> {
    const refreshToken = this.storage.getRefreshToken();
    return this.http
      .post<{ accessToken: string }>("/auth/refresh", {
        refreshToken,
      })
      .pipe(
        tap((response) => this.storage.setAccessToken(response.accessToken)),
      );
  }

  logout(): void {
    this.storage.clearTokens();
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
  }

  getCurrentUser(): Observable<UserDTO> {
    return this.http.get<UserDTO>("/users/me").pipe(
      tap((user) => {
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
      }),
    );
  }

  private setTokens(response: TokenResponse): void {
    this.storage.setAccessToken(response.accessToken);
    this.storage.setRefreshToken(response.refreshToken);
    this.isAuthenticated.set(true);
  }

  getAccessToken(): string | null {
    return this.storage.getAccessToken();
  }
}
```

### JWT Interceptor

```typescript
// core/interceptors/jwt.interceptor.ts
import { Injectable } from "@angular/core";
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError, switchMap } from "rxjs/operators";
import { AuthService } from "../services/auth.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    const token = this.authService.getAccessToken();

    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      });
    }

    return next.handle(req).pipe(
      catchError((error) => {
        if (error.status === 401) {
          return this.authService.refreshToken().pipe(
            switchMap(() => {
              const newToken = this.authService.getAccessToken();
              const cloned = req.clone({
                setHeaders: { Authorization: `Bearer ${newToken}` },
              });
              return next.handle(cloned);
            }),
            catchError(() => {
              this.authService.logout();
              return throwError(() => error);
            }),
          );
        }
        return throwError(() => error);
      }),
    );
  }
}
```

### Auth Guard

```typescript
// core/guards/auth.guard.ts
import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({ providedIn: "root" })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    this.router.navigate(["/auth/login"]);
    return false;
  }
}
```

### Role Guard

```typescript
// core/guards/role.guard.ts
import { Injectable } from "@angular/core";
import { CanActivate, ActivatedRouteSnapshot, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({ providedIn: "root" })
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const user = this.authService.currentUser();
    const requiredRoles = route.data["roles"] as string[];

    if (!user || !requiredRoles.some((role) => user.roles?.includes(role))) {
      this.router.navigate(["/403"]);
      return false;
    }
    return true;
  }
}
```

## Roteamento

### App Routes

```typescript
// app.routes.ts
import { Routes } from "@angular/router";
import { AuthGuard } from "./core/guards/auth.guard";
import { RoleGuard } from "./core/guards/role.guard";

export const APP_ROUTES: Routes = [
  { path: "", redirectTo: "/games", pathMatch: "full" },

  {
    path: "auth",
    loadChildren: () =>
      import("./features/auth/auth.routes").then((m) => m.AUTH_ROUTES),
  },

  {
    path: "games",
    loadChildren: () =>
      import("./features/games/games.routes").then((m) => m.GAMES_ROUTES),
  },

  {
    path: "users",
    loadChildren: () =>
      import("./features/users/users.routes").then((m) => m.USERS_ROUTES),
  },

  {
    path: "threads",
    loadChildren: () =>
      import("./features/threads/threads.routes").then((m) => m.THREADS_ROUTES),
  },

  {
    path: "admin",
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ["ADMIN", "MODERATOR", "EDITOR"] },
    loadChildren: () =>
      import("./features/admin/admin.routes").then((m) => m.ADMIN_ROUTES),
  },

  { path: "404", component: NotFoundComponent },
  { path: "403", component: UnauthorizedComponent },
  { path: "**", redirectTo: "/404" },
];
```

### Feature Routes

```typescript
// features/games/games.routes.ts
import { Routes } from "@angular/router";
import { GamesListPage } from "./pages/games-list/games-list.page";
import { GameDetailPage } from "./pages/game-detail/game-detail.page";
import { GameRatePage } from "./pages/game-rate/game-rate.page";
import { AuthGuard } from "../../core/guards/auth.guard";

export const GAMES_ROUTES: Routes = [
  { path: "", component: GamesListPage },
  { path: ":id", component: GameDetailPage },
  {
    path: ":id/rate",
    component: GameRatePage,
    canActivate: [AuthGuard],
  },
];
```

## Validação em Formulários

```typescript
// Exemplo: form validação com ReactiveForms
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-game-rate-form',
  template: `
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <label>Score (0-10)
        <input formControlName="score" type="number" min="0" max="10" />
        @if (form.get('score')?.hasError('required')) {
          <span class="error">Campo obrigatório</span>
        }
      </label>
      <button [disabled]="form.invalid">Avaliar</button>
    </form>
  `
})
export class GameRateFormComponent {
  form: FormGroup;

  constructor(private fb: FormBuilder, private gameService: GameService) {
    this.form = this.fb.group({
      score: [null, [Validators.required, Validators.min(0), Validators.max(10)]]
    });
  }

  onSubmit() {
    if (this.form.valid) {
      const { score } = this.form.value;
      this.gameService.rate(this.gameId, score).subscribe(...);
    }
  }
}
```

## Testes

### Teste de Serviço

```typescript
// features/games/services/game.service.spec.ts
import { TestBed } from "@angular/core/testing";
import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { GameService } from "./game.service";
import { GameDTO } from "../models/game.dto";

describe("GameService", () => {
  let service: GameService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GameService],
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it("should fetch games list", () => {
    const mockGames: GameDTO[] = [
      { id: "1", title: "Game 1", description: "Desc 1" },
    ];

    service.list().subscribe((games) => {
      expect(games.length).toBe(1);
      expect(games[0].title).toBe("Game 1");
    });

    const req = httpMock.expectOne("/games?page=1&limit=20");
    expect(req.request.method).toBe("GET");
    req.flush(mockGames);
  });
});
```

## Estilos

- Use **Tailwind CSS** para utilidade (recomendado) ou **component CSS**
- Coloque styles em arquivos `.component.css` ou inline com `styleUrl`
- Use CSS Grid/Flexbox para layouts responsivos

## Ambiente

```typescript
// environments/environment.ts
export const environment = {
  production: false,
  apiBaseUrl: "http://localhost:8080/api/v1",
};

// environments/environment.prod.ts
export const environment = {
  production: true,
  apiBaseUrl: "https://api.bloggames.com/api/v1",
};
```

## Segurança

1. Nunca armazene dados sensíveis em localStorage (use httpOnly cookies se possível)
2. Sempre use HTTPS em produção
3. Valide entrada em formulários
4. Sanitize HTML com `DomSanitizer` ou `[innerHTML]`
5. Use `Content Security Policy` headers
