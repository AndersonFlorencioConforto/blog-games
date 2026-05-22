# Matriz de Decisao de Estrategia de Prompt

| Estrategia   | Melhor para                              | Evite quando                               | Custo/Latencia | Seed de template                                           |
| ------------ | ---------------------------------------- | ------------------------------------------ | -------------- | ---------------------------------------------------------- |
| Zero-shot    | Tarefas simples e diretas                | Raciocinio complexo em varias etapas       | Mais baixo     | `Tarefa + restricoes + formato de saida`                   |
| Few-shot     | Formato/estilo de saida estavel          | Exemplos fracos ou conflitantes            | Baixo-Medio    | `Instrucao + 2-5 exemplos + nova entrada`                  |
| CoT (resumo) | Logica/matematica/decomposicao           | Tarefa trivial                             | Medio          | `Pense internamente e retorne racional conciso + resposta` |
| ReAct        | Uso de ferramentas + coleta de evidencia | Nao existe dependencia de ferramenta/dados | Medio-Alto     | `Raciocinar -> Agir(ferramenta) -> Observar -> Decidir`    |
| ToT          | Varios caminhos de solucao em disputa    | Existe um caminho deterministico obvio     | Mais alto      | `Gerar opcoes, pontuar e selecionar melhor caminho`        |

## Seletor rapido

1. Precisa de ferramentas externas? escolha ReAct.
2. Precisa explorar ramificacoes? escolha ToT.
3. Precisa de formatacao estrita? escolha Few-shot.
4. Precisa de raciocinio mais profundo, mas sem ferramentas? escolha resumo CoT.
5. Caso contrario, comece com Zero-shot.

## Exemplos de saida

### Zero-shot

```text
Tarefa: Gere uma query SQL para listar usuarios ativos criados nos ultimos 30 dias.
Restricoes: Use apenas ANSI SQL. Nao use funcoes especificas de vendor.
Saida: somente SQL.
```

### Few-shot

```text
Instrucao: Normalize nomes de produto para kebab-case.
Entrada: "Blue Chair" -> "blue-chair"
Entrada: "USB C Cable" -> "usb-c-cable"
Agora normalize: "Smart Phone Case"
```

### CoT summary

```text
Resolva o problema com raciocinio interno passo a passo.
Retorne apenas:
1) resumo conciso da justificativa
2) proposta final de patch
3) checklist de validacao
```

### ReAct

```text
Objetivo: identificar a causa raiz da suite de testes com falha.
Use chamadas de ferramenta para inspecionar testes com falha, codigo relacionado e git diff.
Apos cada resultado de ferramenta, resuma o achado e escolha a proxima acao.
Finalize com plano de correcao e confianca.
```

### ToT

```text
Gere 3 abordagens de implementacao para esta migracao.
Pontue cada abordagem por risco, velocidade de entrega e manutenibilidade.
Selecione a melhor abordagem e explique em 5 linhas.
```
