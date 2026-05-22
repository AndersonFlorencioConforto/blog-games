---
name: "Padroes de Teste"
description: "Diretrizes para testes unitarios e de integracao com foco em comportamento e seguranca arquitetural."
applyTo: "**/*Test.java,**/*Tests.java"
---

# Padroes de Teste

## Escopo e Prioridade

- Teste primeiro casos de uso e entidades.
- Isole gateways com mocks em testes unitarios.
- Adicione testes de integracao para persistencia e adapters externos.

## Nomenclatura e Legibilidade

- Use nomenclatura `given_when_then`.
- Mantenha cada teste focado em um comportamento.
- Organize em Arrange -> Act -> Assert.

## Casos Minimos por Caso de Uso

- Caminho feliz.
- Falha de validacao.
- Falha de gateway/adapter.
- Caso de borda (null, vazio, valores limite).

## Cobertura Minima de Dominio (Identifier, Value Object e Validator)

- Para cada `XID`, teste ao menos: - `unique()` gera valor nao nulo e reutilizavel como chave de igualdade. - `from(String)` reidrata o mesmo valor esperado para comparacao. - `equals/hashCode` por valor (nao por referencia).
- Para cada `XValidator`, cubra mensagens de erro de regras obrigatorias (null, blank, limites).
- Para agregados com `selfValidate` ou validacao explicita, cubra: - criacao valida, - criacao invalida, - update invalido.
- Para casos de uso que convertem colecoes de IDs de command, cubra `null` e colecao vazia para evitar NPE.
- Para fluxos de `NotFoundException`, valide que o ID usado no erro corresponde ao agregado correto do caso de uso.

```java
@org.junit.jupiter.api.Test
void givenValidCommand_whenExecute_thenReturnOutput() {
    // Arrange
    final var gateway = org.mockito.Mockito.mock(OrderGateway.class);
    org.mockito.Mockito.when(gateway.create(org.mockito.Mockito.any()))
            .thenAnswer(org.mockito.AdditionalAnswers.returnsFirstArg());

    final var useCase = new DefaultCreateOrderUseCase(gateway);
    final var command = CreateOrderCommand.with("customer-1", java.util.List.of("item-1"));

    // Act
    final var output = useCase.execute(command);

    // Assert
    org.junit.jupiter.api.Assertions.assertNotNull(output);
    org.mockito.Mockito.verify(gateway, org.mockito.Mockito.times(1))
            .create(org.mockito.Mockito.any());
}
```

## Antipadroes

- Nao valide detalhes internos de implementacao.
- Nao abuse de mocks na logica de dominio.
- Nao escreva testes que so verificam getters/setters sem comportamento.
