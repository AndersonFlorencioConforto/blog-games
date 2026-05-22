---
name: spring-transactional-usage
description: Define fronteiras transacionais no Spring com @Transactional sem violar limites de arquitetura em camadas. Use quando decidir onde colocar @Transactional, definir a fronteira transacional de um caso de uso, tratar readOnly, rollback, ou consistencia entre multiplas escritas.
argument-hint: "<caso-de-uso> <fluxo> [consistencia]"
---

# Skill de Uso de Transactional no Spring

Use esta skill para desenhar e aplicar transacoes com seguranca em fluxos de escrita e leitura.

## Quando usar

- Caso de uso grava dados em mais de um repository/gateway.
- Fluxo precisa garantir atomicidade para nao persistir parcialmente.
- Existe publicacao de evento acoplada a persistencia.
- Ha duvida sobre onde colocar `@Transactional` na arquitetura em camadas.

## Procedimento

1. Mapeie a unidade de trabalho do caso de uso:
   - quais writes no banco
   - quais leituras de suporte
   - quais efeitos externos (eventos, HTTP, fila, storage)
2. Defina a fronteira transacional na camada de infrastructure que orquestra o caso de uso (wrapper/service), mantendo domain/application sem framework.
3. Para operacoes de escrita, use `@Transactional` com propagacao padrao (`REQUIRED`), salvo justificativa tecnica.
4. Para consultas puras, use `@Transactional(readOnly = true)` quando houver ganho de consistencia/performance.
5. Nao abra transacao em controller e evite manter transacao envolvendo chamadas remotas lentas.
6. Publique efeitos externos de forma segura:
   - preferir outbox ou publicacao apos commit
   - evitar envio de evento antes da confirmacao da transacao
7. Defina rollback esperado:
   - runtime exceptions fazem rollback por padrao
   - para checked exceptions, explicite `rollbackFor`
8. Cubra testes de commit e rollback (incluindo falha intermediaria).
9. Documente fronteiras no arquivo tecnico de transacoes do projeto.

## Verificacoes obrigatorias

- Nenhuma anotacao de transacao em classes de domain.
- Controller nao orquestra transacao.
- Fronteira transacional coincide com um caso de uso de negocio.
- Regra de rollback esta explicita para falhas esperadas.
- Fluxo com eventos tem garantia de consistencia apos commit.
- Testes provam que nao existe persistencia parcial.

## Antipadroes comuns

- `@Transactional` em metodos privados (nao passa por proxy).
- Self-invocation de metodo transacional na mesma classe.
- Transacao longa segurando lock enquanto chama servico externo.
- Publicar evento antes de commit sem estrategia de recuperacao.
- Misturar regra de negocio com detalhes tecnicos de transacao.

## Recursos

- [Template de wrapper transacional](./templates/transactional-usecase-wrapper-template.java.template)
- [Checklist de fronteira transacional](./examples/transaction-boundary-checklist.md)
- [Guia de referencia Java Spring](../../java-spring-reference.md)
