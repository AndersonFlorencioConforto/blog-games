---
name: "Convencoes de API REST"
description: "Convencoes para controllers, contratos HTTP e mapeamento de respostas em projetos backend modulares."
applyTo: "**/*Controller.java,**/*API.java"
---

# Convencoes de API REST

## Responsabilidades do Controller

- Fazer parse da entrada da requisicao HTTP.
- Construir objeto de comando/query.
- Chamar caso de uso.
- Mapear saida para resposta HTTP.

## Padrao Contract-First

- Defina contrato de endpoint na interface `*API`.
- Mantenha path, media types de request/response e anotacoes OpenAPI no contrato da API.

## Convencao Swagger/OpenAPI

- Use `@Tag` no controller para agrupar endpoints por recurso.
- Use `@Operation` em metodos publicos para resumo da operacao.
- Quando aplicavel, documente respostas padrao (`200`, `201`, `204`, `404`, `422`) com `@ApiResponse`.
- Para endpoints protegidos, documente requisito de seguranca (`@SecurityRequirement`).

## Convencao de Pagination

- Para listagem paginada, mantenha query params: `search`, `page`, `perPage`, `sort`, `dir`.
- Defina defaults no contrato `*API` e preserve consistencia por recurso.
- Retorne `Pagination<ResponseModel>` em endpoints de listagem.
- Preserve serializacao da resposta em `snake_case` (`current_page`, `per_page`, `total`, `items`).
- Em filtros por ID (ex.: videos), converta strings para IDs de dominio no controller antes do caso de uso.

```java
@org.springframework.web.bind.annotation.RequestMapping("/customers")
public interface CustomerAPI {

    @org.springframework.web.bind.annotation.PostMapping(
        consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
        produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE
    )
    org.springframework.http.ResponseEntity<?> createCustomer(
        @org.springframework.web.bind.annotation.RequestBody CreateCustomerRequest input
    );
}
```

## Mantenha Controllers Enxutos

- Sem regras de negocio em controllers.
- Sem acesso direto a repository.
- Sem orquestracao de transacao em controllers.

## Mapeamento de Resposta

- `201 Created` para criacao bem-sucedida com URI do recurso quando aplicavel.
- `200 OK` para consulta/atualizacao bem-sucedida.
- `204 No Content` para delecao.
- `422 Unprocessable Entity` para falhas de validacao.
- `404 Not Found` para recursos ausentes.

## Padrao de Presenter e Models

- Use modelos de request/response de infrastructure (`record`) na borda HTTP.
- Converta outputs de application para resposta HTTP usando funcoes de presenter.

```java
public record CustomerResponse(String id, String name) {}

public interface CustomerApiPresenter {
    static CustomerResponse present(final CustomerOutput output) {
        return new CustomerResponse(output.id(), output.name());
    }
}
```

```java
@org.springframework.web.bind.annotation.RestController
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(final CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = java.util.Objects.requireNonNull(createOrderUseCase);
    }

    @org.springframework.web.bind.annotation.PostMapping("/orders")
    public org.springframework.http.ResponseEntity<?> create(
            @org.springframework.web.bind.annotation.RequestBody final CreateOrderRequest request) {

        final var command = CreateOrderCommand.with(request.customerId(), request.items());
        final var result = this.createOrderUseCase.execute(command);

        return org.springframework.http.ResponseEntity
                .created(java.net.URI.create("/orders/" + result.id()))
                .body(result);
    }
}
```

## Limites de Erro

- Traduza erros de domain/application apenas no limite de infrastructure.
- Nunca exponha stack traces para clientes da API.

```java
@org.springframework.web.bind.annotation.RestControllerAdvice
public class GlobalExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public org.springframework.http.ResponseEntity<?> handleNotFound(final NotFoundException ex) {
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```
