# Swagger/OpenAPI para Controllers REST

Este guia complementa a skill `spring-rest-endpoint` com exemplos de uso de OpenAPI quando ha tags em controllers.

## Objetivo

- Padronizar `@Tag`, `@Operation` e respostas comuns em endpoints.
- Manter contrato HTTP documentado sem vazar regra de negocio.

## Exemplo 1: Tag no controller + operation no metodo

```java
@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Operacoes de categorias")
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = java.util.Objects.requireNonNull(createCategoryUseCase);
    }

    @Override
    @Operation(summary = "Cria uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado"),
            @ApiResponse(responseCode = "422", description = "Erro de validacao")
    })
    public ResponseEntity<?> create(final CreateCategoryRequest input) {
        final var command = CreateCategoryCommand.with(input.name(), input.description(), input.isActive());
        final var output = this.createCategoryUseCase.execute(command);
        return ResponseEntity.created(java.net.URI.create("/categories/" + output.id())).body(output);
    }
}
```

## Exemplo 2: Seguranca no endpoint

```java
@Operation(summary = "Detalha categoria")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "404", description = "Nao encontrado")
public CategoryResponse getById(final String id) {
    ...
}
```

## Exemplo 3: Agrupamento por recurso

- `@Tag(name = "Categories")` em `CategoryController`
- `@Tag(name = "Genres")` em `GenreController`
- `@Tag(name = "Videos")` em `VideoController`

Evite misturar muitos recursos na mesma tag para nao poluir a UI.

## Checklist rapido

- Controller tem `@Tag` com nome consistente.
- Metodos publicos tem `@Operation` com resumo objetivo.
- Responses basicas (`200`, `201`, `204`, `404`, `422`) estao mapeadas.
- Se endpoint e protegido, `@SecurityRequirement` foi definido.
- Regras de negocio continuam fora do controller.
