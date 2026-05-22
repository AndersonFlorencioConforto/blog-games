---
name: "create-aggregate"
description: "Gera um aggregate root com invariantes, hooks de validacao e value objects."
argument-hint: "<nome-do-aggregate> <regras-chave>"
disable-model-invocation: true
---

Crie um aggregate root para `$0`.

Use estas referencias:

- [Regras globais](../../CLAUDE.md)
- [Regras Java + DDD](../../rules/java-ddd.md)

## Quando usar este prompt

- Use este prompt para modelar apenas o aggregate root e suas invariantes.
- Para um fluxo ponta a ponta (domain/application/infrastructure/tests), prefira a [Skill de scaffold de caso de uso](../ddd-use-case-scaffold/SKILL.md).

## Etapas

1. Capture invariantes a partir de `$1`.
2. Modele campos e value objects.
3. Adicione metodos de fabrica (`newX`, `with`).
4. Adicione metodos de comportamento que reforcem invariantes.
5. Adicione integracao de validadores.
6. Adicione testes unitarios para validacao de invariantes.

## Restricoes

- Sem anotacoes de framework em modelos de dominio.
- Mantenha metodos de mutacao explicitos e conscientes de efeitos colaterais.
- Evite modelos anemicos.

## Formato de exemplo

```java
public class Customer extends AggregateRoot<CustomerId> {
    private String name;
    private Email email;
    private boolean active;

    private Customer(final CustomerId id, final String name, final Email email, final boolean active) {
        super(id);
        this.name = name;
        this.email = email;
        this.active = active;
    }

    public static Customer newCustomer(final String name, final String email) {
        return new Customer(CustomerId.unique(), name, Email.of(email), true);
    }

    public Customer rename(final String newName) {
        this.name = newName;
        return this;
    }
}
```
