# Playbook de Customizacoes de IA (Generico)

Este playbook explica quando usar `CLAUDE.md`, `rules`, `skills` e `agents` em projetos de software com arquitetura em camadas.

Nota (Claude Code): use `CLAUDE.md` e `.claude/rules/` para regras; use `.claude/skills/` para comandos reutilizaveis. `commands/` permanece suportado, mas `skills/` e recomendado.

## 1) Para que serve cada artefato

- `CLAUDE.md` e `rules`: regras sempre ativas ou direcionadas por arquivo. Use para padroes de codigo, restricoes de arquitetura e convencoes inegociaveis.
- `skills`: comandos reutilizaveis para tarefas focadas (scaffold, review, refatoracao), com recursos/scripts/templates opcionais.
- `agents`: personas persistentes com papel e fluxo de trabalho especificos (planner, implementer, java-spring-architect, project-discovery-architect).

## 2) Guia de decisao

- Precisa de padroes globais de codigo? Use `CLAUDE.md` ou `rules`.
- Precisa de uma tarefa repetivel? Use uma `skill`.
- Precisa de uma capacidade reutilizavel com exemplos/templates? Use uma `skill`.
- Precisa de um papel com fluxo e handoffs? Use um `agent`.

## 3) Estrategia de engenharia de prompts

### Zero-shot

Use quando:

- A tarefa e simples e direta.
- O formato de saida e obvio.
  Evite quando:
- O raciocinio envolve varias etapas ou forte dependencia de dominio.

Template:

```text
Tarefa: <objetivo unico e claro>
Restricoes: <regras obrigatorias>
Saida: <formato exato esperado>
```

### Few-shot

Use quando:

- A estrutura da saida precisa ser consistente.
- Voce precisa reproduzir estilo ou formatacao.
  Evite quando:
- Os exemplos sao ruidosos ou contraditorios.

Template:

```text
Instrucao: Classifique a entrada como A/B/C.
Exemplo 1: <entrada> -> <saida>
Exemplo 2: <entrada> -> <saida>
Agora classifique: <nova entrada>
```

### Chain-of-thought (CoT)

Use quando:

- O problema exige raciocinio em varias etapas.
- E necessario usar aritmetica, logica ou decomposicao mais profunda.
  Evite quando:
- A tarefa e trivial.
- Latencia/custo e muito restrito.

Boa pratica:

- Peça um resumo conciso do raciocinio, nao rastros internos ocultos.

Template:

```text
Resolva internamente passo a passo e depois retorne:
1) resumo conciso do raciocinio
2) resposta final
3) auto-verificacao rapida
```

### ReAct

Use quando:

- A tarefa precisa de raciocinio + uso de ferramentas.
- E necessario buscar evidencias externas.
  Evite quando:
- Todos os dados necessarios ja estao no contexto local.

Template:

```text
Objetivo: <objetivo>
Processo: alternar raciocinio e acoes de ferramenta.
Para cada acao, cite a fonte da evidencia.
Finalize com resposta final e confianca.
```

### Tree of Thoughts (ToT)

Use quando:

- Existem varios caminhos validos e trade-offs.
- E necessaria busca estrategica com retrocesso.
  Evite quando:
- O problema tem um caminho deterministico obvio.

Template:

```text
Gere 3 abordagens candidatas.
Pontue cada uma por: risco de corretude, esforco, manutenibilidade.
Escolha o melhor caminho e justifique em 5 linhas.
```

## 4) Antipadroes rapidos

- Usar few-shot em excesso com exemplos fracos.
- Pedir todas as tecnicas no mesmo prompt sem necessidade.
- Forcar explicacoes longas para tarefas simples.
- Misturar regras de arquitetura com instrucoes temporarias de tarefa.

## 5) Referencias usadas

- Documentacao de customizacao do VS Code (instructions, prompts, skills, agents).
- Documentacao de instrucoes customizadas do GitHub Copilot.
- Guia de engenharia de prompts da OpenAI.
- Boas praticas de prompts da Anthropic.
- Artigos de pesquisa: CoT (Wei et al. 2022), ReAct (Yao et al. 2022), ToT (Yao et al. 2023).
