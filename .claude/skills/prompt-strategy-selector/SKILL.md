---
name: prompt-strategy-selector
description: Seleciona a estrategia certa de engenharia de prompt (zero-shot, few-shot, CoT, ReAct, ToT) e gera um template adequado ao objetivo. Use quando precisar projetar um prompt, escolher entre zero-shot, few-shot, CoT, ReAct ou ToT, ou gerar um template de prompt para uma tarefa especifica.
argument-hint: "Descreva tarefa, restricoes e saida esperada"
---

# Skill de Selecao de Estrategia de Prompt

Use esta skill para escolher a estrategia minima viavel de prompting que atenda a restricoes de qualidade, latencia e custo.

## Procedimento

1. Classifique a complexidade da tarefa:
   - simples e deterministica
   - sensivel a formatacao
   - intensiva em raciocinio
   - dependente de ferramenta
   - intensa em busca/ramificacao
2. Mapeie para estrategia:
   - simples e deterministica -> zero-shot
   - sensivel a formatacao -> few-shot
   - intensiva em raciocinio -> resumo CoT
   - dependente de ferramenta -> ReAct
   - intensa em busca/ramificacao -> ToT
3. Gere um template de prompt pronto para executar.
4. Forneca um risco e uma mitigacao.

## Notas de seguranca e qualidade

- Nunca peça exposicao de chain-of-thought privado.
- Peça justificativa concisa e resposta final.
- Prefira schemas explicitos de saida para respostas consumidas por maquina.

## Recurso

- [Matriz de decisao](./examples/decision-matrix.md)
