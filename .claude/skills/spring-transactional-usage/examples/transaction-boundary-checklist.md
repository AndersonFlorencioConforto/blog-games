# Checklist de Fronteira Transacional

## Delimitacao

- [ ] A transacao cobre um unico caso de uso de negocio.
- [ ] Nao ha transacao em controller.
- [ ] Domain permanece sem anotacoes Spring.

## Consistencia

- [ ] Falha no meio do fluxo faz rollback completo.
- [ ] Fluxo com eventos usa outbox ou publicacao apos commit.
- [ ] Chamadas externas longas ficam fora da transacao quando possivel.

## Configuracao

- [ ] Escrita usa `@Transactional` no boundary escolhido.
- [ ] Leitura critica usa `@Transactional(readOnly = true)` quando aplicavel.
- [ ] `rollbackFor` foi definido para checked exceptions relevantes.

## Testes

- [ ] Teste de caminho feliz valida commit.
- [ ] Teste de falha intermediaria valida rollback.
- [ ] Teste de idempotencia/correcao evita duplicidade de efeito externo.
