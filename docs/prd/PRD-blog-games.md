# PRD - Blog Games Platform

**Versao:** 1.0  
**Data:** 2026-05-21  
**Autor:** Anderson Florencio Conforto  
**Status:** Aprovado para discovery

---

## 1. Problema

Jogadores e entusiastas de games carecem de uma plataforma centralizada em portugues que combine:
- Catalogo curado de jogos com reviews editoriais e notas da plataforma
- Notas e avaliacoes da comunidade de usuarios
- Estante pessoal para organizacao da biblioteca
- Discussoes e threads por jogo
- Feed de noticias do mundo dos games

Plataformas como Metacritic e Backloggery atendem partes dessa demanda em ingles. O Baixaki Jogos (descontinuado) deixou lacuna no mercado brasileiro.

---

## 2. Objetivos e Criterios de Sucesso

| Objetivo | Metrica de sucesso | Prazo |
|---|---|---|
| Lançar catalogo com pelo menos 50 jogos curados | 50 jogos cadastrados com review editorial | Release 1 |
| Usuarios podem avaliar jogos | Media de notas exibida por jogo | Release 1 |
| Usuarios podem criar estante pessoal | Funcionalidade de estante operacional | Release 1 |
| Threads de discussao ativas | Pelo menos 1 thread por jogo popular | Release 2 |
| Sistema de noticias editorial | ADMIN publica noticias vinculadas ou nao a jogos | Release 2 |
| Sistema de moderacao funcional | ADMINs podem resolver denuncias em menos de 48h | Release 2 |

---

## 3. Personas

### P1 - Jogador Casual
- Busca informacoes sobre jogos antes de comprar
- Avalia jogos que ja jogou
- Nao participa ativamente de forums

### P2 - Gamer Entusiasta
- Mantem estante pessoal detalhada
- Participa de threads de discussao
- Segue outros usuarios e ve suas estantes

### P3 - Editor / Admin
- Cadastra e mantem catalogo de jogos
- Escreve reviews editoriais e noticias
- Modera denuncias e comportamento abusivo

---

## 4. Escopo por Release

### Release 1 - MVP Core (base do produto)

**Autenticacao e Usuarios**
- Cadastro de usuario com e-mail e senha
- Login com JWT + refresh token
- Logout com invalidacao de token
- Recuperacao de senha por e-mail (token temporario)
- Edicao de perfil (nome, bio, foto de perfil)
- Ver perfil de outro usuario

**Catalogo de Jogos**
- CRUD completo de jogos (somente ADMIN)
- Campos: titulo, descricao, review editorial, nota da plataforma, pros, contras, imagem de capa, plataformas, categorias
- Busca por categoria e por plataforma
- Avaliacao de 0 a 5 estrelas por usuarios autenticados
- Media de notas dos usuarios exibida por jogo

**Estante do Usuario**
- Adicionar jogo a estante com status: JA_TENHO, PRETENDO_PEGAR, FAVORITO
- Remover jogo da estante
- Alterar status de jogo na estante
- Ver propria estante
- Ver estante de outro usuario

**Social basico**
- Seguir / deixar de seguir outro usuario
- Ver lista de seguidores e seguindo

### Release 2 - Threads e Noticias

**Threads e Comentarios**
- Abrir thread em um jogo
- Responder a uma thread
- Dar like em thread e em resposta
- Denunciar usuario por comportamento abusivo em thread

**Noticias**
- ADMIN cria, edita e remove noticias
- Noticias podem ser vinculadas a um jogo especifico ou ser gerais
- Listagem de noticias paginada
- Detalhe de noticia

**Moderacao**
- ADMIN ve lista de denuncias abertas
- ADMIN resolve denuncia (aprovar ou rejeitar)
- ADMIN pode banir usuario temporariamente (campo `bannedUntil`)

---

## 5. Requisitos Nao Funcionais (NFRs)

| Categoria | Requisito |
|---|---|
| Performance | Listagem de jogos com paginacao de ate 20 itens deve responder em menos de 500ms |
| Seguranca | Tokens JWT com expiracao de 15 minutos; refresh token com expiracao de 7 dias |
| Seguranca | Passwords armazenados com BCrypt (fator de custo 12) |
| Seguranca | Rate limiting em endpoints de autenticacao (10 req/min por IP) |
| Disponibilidade | 99% uptime em ambiente de producao |
| Escalabilidade | Arquitetura stateless; sessao nao armazenada em servidor |
| Observabilidade | Logs estruturados em JSON; rastreamento por correlation ID |
| Portabilidade | Profile `local` com H2; profile `dev/staging/production` com PostgreSQL |
| Testabilidade | Cobertura minima de 80% nas camadas domain e application |

---

## 6. Premissas Registradas

- **P-001:** Nao ha login social (Google, Facebook). Autenticacao exclusivamente por e-mail e senha com JWT.
- **P-002:** Jogos sao gerenciados exclusivamente por ADMINs. Usuarios comuns nao cadastram jogos.
- **P-003:** A nota da plataforma e inserida manualmente pelo ADMIN e nao e calculada automaticamente.
- **P-004:** A media de estrelas dos usuarios e calculada em tempo de leitura (query AVG) ou materializada em campo desnormalizado. Decisao pendente de benchmark (ver Data Strategy).
- **P-005:** Upload de imagem (capa do jogo, foto de perfil) sera armazenado em objeto storage externo (ex: S3-compatible). URL da imagem e armazenada no banco. Integracao com storage nao e escopo do Release 1; campo aceita URL externa.
- **P-006:** Envio de e-mail de recuperacao de senha sera sincrono via JavaMailSender no Release 1. Avaliacao de fila adiada para quando volume justificar (ver ADR-0004).
- **P-007:** Noticias nao tem sistema de comentarios proprios; comentarios existem apenas em jogos via threads.
- **P-008:** Nao ha sistema de notificacao push ou em tempo real no escopo atual (sem WebSocket/SSE).
- **P-009:** Banco de dados para producao sera PostgreSQL (ver ADR-0002).
- **P-010:** Ban de usuario implementa campo `bannedUntil` (timestamp). Verificacao no momento de autenticacao e em cada requisicao autenticada.

---

## 7. Riscos

| Risco | Probabilidade | Impacto | Mitigacao |
|---|---|---|---|
| Volume de avaliacoes causar lentidao no calculo de media | Media | Alto | Desnormalizar media em campo calculado e atualizar via trigger ou em background |
| Abuso de denuncias (spam de denuncias) | Media | Medio | Rate limit por usuario em endpoint de denuncia |
| Tokens de recuperacao de senha nao expirados sendo reutilizados | Baixa | Alto | Token de uso unico com expiracao de 1 hora; invalidar apos uso |
| Crescimento de threads em jogos populares afetando leitura | Media | Medio | Paginacao obrigatoria; indices em thread.gameId e reply.threadId |
