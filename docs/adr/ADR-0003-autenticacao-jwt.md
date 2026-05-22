# ADR-0003 - Autenticacao com JWT + Refresh Token + Invalidacao

**Data:** 2026-05-21  
**Status:** Aceito  
**Decisores:** Anderson Florencio Conforto

---

## Contexto

A plataforma requer autenticacao stateless para a API REST. Os requisitos sao:
- Login com e-mail e senha
- JWT de curta duracao (access token)
- Refresh token de longa duracao para renovacao sem relogin
- Logout com invalidacao real do token (nao apenas no cliente)
- Recuperacao de senha por e-mail com token temporario de uso unico
- Sem login social (Google, Facebook)

O desafio principal do JWT stateless e a impossibilidade de invalidar tokens antes de sua expiracao natural sem alguma forma de estado server-side.

---

## Decisao

Adotar **JWT (HS256 ou RS256) para access token** com as seguintes complementacoes:

1. **Access Token:** JWT com expiracao de 15 minutos. Assinado com chave simetrica (HS256) armazenada em variavel de ambiente.
2. **Refresh Token:** UUID opaco armazenado em banco de dados (tabela `refresh_tokens`). Expiracao de 7 dias. Invalido apos uso (rotacao).
3. **Blocklist de tokens (Denylist):** Tabela `token_blocklist` armazena o `jti` (JWT ID) de tokens invalidados ate sua expiracao natural. Verificacao a cada requisicao autenticada.
4. **Recuperacao de senha:** Token UUID de uso unico com expiracao de 1 hora armazenado em tabela `password_reset_tokens`.

---

## Opcoes Avaliadas

### Opcao A - JWT puro stateless (sem invalidacao server-side)
**Pros:** Completamente stateless; sem consulta ao banco em cada requisicao  
**Contras:** Impossivel invalidar token no logout real; token roubado permanece valido ate expirar  
**Rejeitado:** Requisito de logout real invalida esta opcao

### Opcao B - Sessao server-side (Spring Session + Redis)
**Pros:** Invalidacao trivial; revogacao granular  
**Contras:** Stateful; requer Redis; viola principio de escalabilidade horizontal simples  
**Rejeitado:** Adiciona dependencia de infraestrutura desnecessaria para o porte atual

### Opcao C - JWT + Denylist em banco (ESCOLHIDA)
**Pros:** Mantém JWT para payloads; invalidacao real via denylist; sem Redis necessario no escopo inicial  
**Contras:** Uma consulta extra ao banco por requisicao autenticada (denylist check)  
**Aceito:** Trade-off aceitavel; denylist e pequena (apenas tokens ainda nao expirados); pode ser cacheada em memoria futuramente

### Opcao D - JWT com RS256 (chave assimetrica)
**Pros:** Permite verificacao de token por outros servicos sem compartilhar chave privada  
**Contras:** Complexidade adicional desnecessaria para servico monolitico  
**Premissa P-012:** Se o sistema evoluir para microservicos, migrar para RS256 deve ser revisitado em ADR especifico

---

## Modelo de Dados para Autenticacao

```sql
-- Refresh tokens validos
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,  -- SHA-256 do token
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    device_info VARCHAR(255)
);

-- Denylist de access tokens (somente os ainda nao expirados)
CREATE TABLE token_blocklist (
    jti VARCHAR(36) PRIMARY KEY,  -- JWT ID (UUID)
    expires_at TIMESTAMP NOT NULL,  -- para limpeza automatica
    blocked_at TIMESTAMP NOT NULL
);

-- Tokens de recuperacao de senha
CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);
```

---

## Fluxo de Autenticacao

### Login
1. POST /auth/login com { email, password }
2. Validar credenciais; verificar se usuario esta banido
3. Gerar access token JWT (payload: userId, email, role, jti, iat, exp=15min)
4. Gerar refresh token UUID; armazenar hash no banco
5. Retornar { accessToken, refreshToken, expiresIn }

### Requisicao Autenticada
1. Extrair JWT do header Authorization: Bearer <token>
2. Validar assinatura e expiracao
3. Verificar se jti esta na denylist
4. Verificar se usuario existe e nao esta banido
5. Injetar SecurityContext com userId e role

### Renovacao (Refresh)
1. POST /auth/refresh com { refreshToken }
2. Buscar refresh token pelo hash; verificar expiracao e nao-revogado
3. Revogar token atual (rotacao obrigatoria)
4. Emitir novo access token e novo refresh token
5. Retornar novo par { accessToken, refreshToken }

### Logout
1. POST /auth/logout (autenticado)
2. Inserir jti do access token atual na denylist com expires_at
3. Revogar refresh token do usuario (ou todos os dispositivos, configuravel)

### Recuperacao de Senha
1. POST /auth/forgot-password com { email }
2. Gerar token UUID; armazenar hash com expiracao de 1h
3. Enviar e-mail com link contendo token (sincrono via JavaMailSender)
4. POST /auth/reset-password com { token, newPassword }
5. Validar token; verificar expiracao e nao-uso
6. Atualizar senha com BCrypt; marcar token como usado
7. Revogar todos os refresh tokens do usuario

---

## Configuracoes de Seguranca

```properties
security.jwt.secret=${JWT_SECRET}  # minimo 256 bits (32 bytes)
security.jwt.access-token-expiration=900  # 15 minutos em segundos
security.jwt.refresh-token-expiration=604800  # 7 dias em segundos
security.password-reset.token-expiration=3600  # 1 hora em segundos
```

---

## Consequencias

- Tarefa de limpeza agendada (`@Scheduled`) para remover registros expirados de `token_blocklist` e `password_reset_tokens` (executar diariamente)
- Biblioteca: `io.jsonwebtoken:jjwt-api` (JJWT) ou `com.nimbusds:nimbus-jose-jwt`
- BCrypt com fator de custo 12 para hash de senhas
- Rate limiting no endpoint `/auth/login`: 10 tentativas por IP por minuto (via filtro customizado ou biblioteca como Bucket4j)
