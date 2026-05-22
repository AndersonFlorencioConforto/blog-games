# Checklist de Arquitetura

## Verificacoes de Domain

- Sem imports de Spring/JPA/HTTP.
- Invariantes de negocio sao reforcadas em entidades/value objects.
- Contratos de gateway sao apenas interfaces.
- IDs concretos seguem `XID.unique()` e `XID.from(String)` de forma consistente.
- Agregados delegam validacao para `XValidator` em `validate(handler)`.
- `Entity`/`AggregateRoot` preservam identidade por ID e regras de evento de dominio.

## Verificacoes de Application

- Cada caso de uso tem entrada/saida clara.
- O caso de uso orquestra apenas domain e gateways.
- O caminho de erro e explicito e usa excecoes previsiveis de dominio (`NotificationException`, `NotFoundException`, `DomainException`).
- Conversoes de colecoes de IDs em command sao null-safe (sem `stream()` em colecao nula).
- Erros de not found usam o ID do agregado correto (`NotFoundException.with(X.class, xId)`).

## Verificacoes de Infrastructure

- Controllers sao enxutos e delegam para casos de uso.
- O contrato da API e definido na interface `*API` (path/params/status).
- Adapters de persistencia implementam contratos de gateway.
- O adapter converte por meio de `from(aggregate)` e `toAggregate()` explicitos.
- Repository permanece apenas como abstracao de persistencia (sem regra de negocio).
- O wiring de caso de uso na configuracao referencia interfaces/contratos, nao classes de controller.
- Excecoes de domain/application sao mapeadas no global exception handler.
- Modelos de API sao mapeados na borda, sem vazamento para o dominio.

## Verificacoes de Teste

- Testes unitarios de caso de uso cobrem sucesso + validacao + falhas de gateway.
- Testes de integracao cobrem adapters com dependencias reais de infraestrutura quando necessario.

## Smells para sinalizar

- Controller contendo logica de decisao de negocio.
- Entidade de dominio anotada com anotacao de framework.
- Caso de uso retornando tipos de resposta de framework.
- Tratamento silencioso de excecao generica sem significado de dominio.
