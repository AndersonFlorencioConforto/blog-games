---
name: java-design-pattern-application
description: Seleciona e aplica design patterns em Java com foco em problema real, trade-offs e impacto por camada. Use quando houver duplicacao, condicional complexa, acoplamento alto, ou necessidade de escolher e aplicar um padrao (Strategy, Factory, Adapter, etc.) com trade-offs claros.
argument-hint: "<problema> <modulo/arquivos> [restricoes]"
---

# Skill de Aplicacao de Design Patterns em Java

Use esta skill quando houver duvida sobre qual pattern aplicar, ou quando for necessario refatorar codigo Java para reduzir acoplamento e melhorar evolucao.

## Entradas esperadas

- Problema observado (sintoma concreto no codigo).
- Escopo do modulo/arquivos afetados.
- Restricoes (performance, legibilidade, prazo, compatibilidade).

## Procedimento

1. Identifique o problema recorrente no codigo (variacao de comportamento, criacao complexa, contrato incompativel, acoplamento excessivo).
2. Classifique o problema por intencao:
   - criacao de objetos -> padroes criacionais
   - composicao/integracao -> padroes estruturais
   - distribuicao de responsabilidade/comunicacao -> padroes comportamentais
3. Proponha 1-2 patterns candidatos e explicite trade-offs.
4. Escolha a opcao de menor complexidade que resolva o problema atual sem overengineering.
5. Desenhe interfaces e fronteiras por camada (domain/application/infrastructure).
6. Aplique mudanca incremental por arquivo, preservando comportamento externo.
7. Adicione testes para caminho feliz e falhas relevantes do fluxo impactado.
8. Documente o criterio de escolha e quando revisar essa decisao.

## Regras obrigatorias

- Nao introduza pattern sem um problema recorrente concreto.
- Prefira composicao a heranca quando possivel.
- Preserve os limites de arquitetura entre domain, application e infrastructure.
- Evite Singleton mutavel global.
- Em Spring, prefira recursos nativos do framework quando eles ja resolverem o problema de forma simples.

## Formato de saida

1. Problema observado.
2. Pattern escolhido e justificativa.
3. Alternativas rejeitadas e motivo.
4. Plano de mudanca por arquivo/camada.
5. Plano de testes.
6. Riscos residuais e mitigacoes.

## Recursos

- [Matriz de selecao de patterns](./examples/pattern-selection-matrix.md)
