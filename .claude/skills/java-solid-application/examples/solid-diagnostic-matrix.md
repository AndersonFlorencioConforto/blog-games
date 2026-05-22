# Matriz de Diagnostico SOLID (Java)

| Sinal observado no codigo                                          | Principio alvo | Acao recomendada                                                      | Quando evitar                                                            | Camada tipica                     |
| ------------------------------------------------------------------ | -------------- | --------------------------------------------------------------------- | ------------------------------------------------------------------------ | --------------------------------- |
| Classe mistura regra de negocio, persistencia e serializacao       | SRP            | Extrair responsabilidades em classes/modulos separados                | Quando separacao criaria fragmentacao sem ganho de legibilidade          | Domain/Application/Infrastructure |
| Nova regra sempre exige editar classe central estavel              | OCP            | Introduzir extensao por estrategia/polimorfismo em ponto de variacao  | Quando nao existe variacao real e uma condicao simples resolve           | Domain/Application                |
| Subclasse quebra comportamento esperado do contrato base           | LSP            | Redesenhar hierarquia, fortalecer contrato e preferir composicao      | Quando heranca ainda representa "is-a" claro e contrato permanece valido | Domain                            |
| Interface grande obriga implementacao de metodos nao usados        | ISP            | Dividir em interfaces menores orientadas ao cliente/caso de uso       | Quando a interface ja e pequena, estavel e realmente compartilhada       | Application/Infrastructure        |
| Caso de uso depende de adapter concreto, repository ou client HTTP | DIP            | Dependencia via abstracao no nucleo e implementacao concreta na borda | Quando abstracao e unica e nunca mudara, sem impacto de teste/evolucao   | Application/Infrastructure        |

## Checklist rapido antes de refatorar

1. Qual principio SOLID esta sendo violado de forma observavel?
2. Qual mudanca minima reduz o risco de regressao agora?
3. A refatoracao melhora testabilidade e entendimento do fluxo?
4. O limite entre domain/application/infrastructure permaneceu claro?
5. Existe evidencia de que a nova abstracao tem consumidor real?

Se 3 ou mais respostas forem "nao", reduza o escopo e aplique uma mudanca menor antes de ampliar a refatoracao.
