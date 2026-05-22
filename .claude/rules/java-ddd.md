---
name: "Java DDD + Arquitetura Limpa"
description: "Convencoes para codigo Java em projetos modulares com camadas de dominio, aplicacao e infraestrutura."
paths:
  - "**/*.java"
---

# Convencoes Java DDD

## Limites de Camada

- `domain` nao deve importar Spring, JPA, HTTP ou APIs especificas de framework.
- `application` orquestra casos de uso e depende de contratos do dominio.
- `infrastructure` implementa contratos e mapeia preocupacoes externas.

## Modelagem de Dominio

- Prefira metodos de fabrica explicitos (`newX`, `with`) em vez de construtores telescopicos.
- Mantenha invariantes em metodos de entidade e validadores.
- Use value objects para evitar obsessao por tipos primitivos.

## Abstracoes Base de Dominio (padrao observado neste projeto)

- `Identifier` estende `ValueObject` e expoe `getValue()` como contrato comum de ID.
- IDs concretos seguem o padrao `XID.unique()` e `XID.from(String)`.
- `Entity<ID extends Identifier>` concentra identidade, igualdade por ID e ciclo de eventos de dominio (`registerEvent`, `publishDomainEvents`).
- `AggregateRoot<ID extends Identifier>` deve ser a base de agregados com `validate(ValidationHandler)` sobrescrito no agregado.
- `IdUtils.uuid()` gera IDs em lowercase e sem hifen; preserve consistencia ao mapear/rehidratar IDs.
- Se um `XID.from` aplicar normalizacao (ex.: lowercase), mantenha a regra consistente em todo o modulo.

```java
public class Money {
    private final java.math.BigDecimal amount;
    private final String currency;

    private Money(final java.math.BigDecimal amount, final String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(final java.math.BigDecimal amount, final String currency) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("amount deve ser nao negativo");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency e obrigatoria");
        }
        return new Money(amount, currency);
    }
}
```

## Estrutura de Caso de Uso

- Use objetos de comando como entrada e DTOs de saida como retorno.
- Mantenha construtores de caso de uso explicitos e com injecao de dependencia.
- Mantenha um unico ponto de entrada claro em `execute`.
- Em comandos com colecoes (`List`/`Set`), trate `null` como vazio antes de usar `stream()`.
- Para recuperacao por ID, converta entrada com `XID.from` e use `NotFoundException.with(X.class, xId)` com o ID do proprio agregado.

## Convencoes de Nomenclatura

- Use `CreateXCommand`, `UpdateXCommand`, `XOutput`, `DefaultXUseCase`, `XGateway`.
- Use nomes de metodo explicitos (`execute`, `create`, `update`, `deleteById`, `getById`).
- Use DTOs de entrada/saida imutaveis quando possivel (records sao preferidos).

## Padrao de Validacao (Validator + Notification)

- Validators de dominio devem estender `Validator` e anexar erros via `ValidationHandler`.
- Agregados implementam `validate(handler)` e delegam para `XValidator`.
- Casos de uso em `application` acumulam erros com `Notification.create()` e validam regras de dominio + relacionamentos externos.
- Fluxos de erro devem usar excecoes previsiveis de dominio (ex.: `NotificationException`, `NotFoundException`, `DomainException`).
- Evite misturar estrategias de erro diferentes no mesmo caso de uso.

```java
public abstract class UseCase<I, O> {
    public abstract O execute(I input);
}
```

## Direcao de Dependencia

- Gateways sao declarados em domain/application e implementados em infrastructure.
- Nunca dependa de dentro para fora de domain para infrastructure.

## Sequencia de Mudanca

- Se a mudanca atravessar camadas, implemente de dentro para fora: domain -> application -> infrastructure.

## Mapeamento de Dados

- Mapeie modelos de request/response apenas em infrastructure.
- Mantenha entidades de dominio livres de preocupacoes de serializacao.

## Padrao de Adapter de Infraestrutura

- Implemente contratos de gateway em adapters de infrastructure.
- Mantenha detalhes de query/paginacao/filtro no gateway de infrastructure.
- Converta modelos de persistencia para aggregates no limite do adapter.

```java
@org.springframework.stereotype.Component
public class CustomerMySQLGateway implements CustomerGateway {

    private final CustomerRepository repository;

    public CustomerMySQLGateway(final CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer create(final Customer customer) {
        return this.repository.save(CustomerJpaEntity.from(customer)).toAggregate();
    }
}
```

## Padrao de Modelo de Persistencia

- Use `XJpaEntity` apenas para preocupacoes de persistencia.
- Forneca metodos explicitos de mapeamento `from(aggregate)` e `toAggregate()`.

```java
@javax.persistence.Entity(name = "Customer")
@javax.persistence.Table(name = "customers")
public class CustomerJpaEntity {

    @javax.persistence.Id
    private String id;

    private String name;

    public static CustomerJpaEntity from(final Customer customer) {
        final var entity = new CustomerJpaEntity();
        entity.id = customer.getId().getValue();
        entity.name = customer.getName();
        return entity;
    }

    public Customer toAggregate() {
        return Customer.with(CustomerID.from(this.id), this.name);
    }
}
```

## Padrao de Wiring de Caso de Uso

- Faça o wiring de casos de uso padrao na configuracao de infrastructure quando wiring explicito de bean for adotado.

```java
@org.springframework.context.annotation.Configuration
public class CustomerUseCaseConfig {

    private final CustomerGateway customerGateway;

    public CustomerUseCaseConfig(final CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @org.springframework.context.annotation.Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new DefaultCreateCustomerUseCase(customerGateway);
    }
}
```

## Estilo de Codigo

- Favoreca metodos pequenos com nomes explicitos.
- Prefira variaveis locais `final` em fluxos de caso de uso.
- Mantenha classes focadas em um unico motivo de mudanca.
