# Matriz de Selecao de Design Patterns (Java)

| Sinal de problema                                      | Pattern candidato               | Quando evitar                                                 | Camada tipica         |
| ------------------------------------------------------ | ------------------------------- | ------------------------------------------------------------- | --------------------- |
| Muitos if/else para trocar algoritmo                   | Strategy                        | Quando existe apenas uma variacao real                        | Domain/Application    |
| Objeto com construcao complexa e muitos opcionais      | Builder                         | Quando construtor simples ja e suficiente                     | Domain/Infrastructure |
| Integracao com contrato externo diferente do interno   | Adapter                         | Quando voce controla ambos os contratos e pode alinhar direto | Infrastructure        |
| Subsistema externo complexo exposto para varios pontos | Facade                          | Quando a abstracao esconde detalhes essenciais para o negocio | Infrastructure        |
| Notificacao para multiplos consumidores de evento      | Observer                        | Quando fluxo sincrono simples e suficiente                    | Domain/Infrastructure |
| Comportamento muda conforme estado interno             | State                           | Quando estados sao poucos e estaveis, um switch simples basta | Domain                |
| Pipeline de regras em etapas encadeadas                | Chain of Responsibility         | Quando ordem/etapas nao variam e a complexidade nao justifica | Application           |
| Criacao de objetos por tipo/familia                    | Factory Method/Abstract Factory | Quando criacao e direta e nao ha variabilidade                | Domain/Infrastructure |

## Checklist rapido antes de aplicar

1. O problema se repete em mais de um ponto do codigo?
2. O pattern reduz acoplamento e facilita testes?
3. O ganho de clareza compensa as classes extras?
4. Existe alternativa mais simples em Java/Spring para o mesmo problema?
5. O time consegue manter essa estrutura sem aumentar custo operacional?

Se 3 ou mais respostas forem "nao", prefira solucao mais simples antes de aplicar um pattern.
