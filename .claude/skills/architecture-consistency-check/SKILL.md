---
name: architecture-consistency-check
description: Audita consistencia arquitetural e executa code review orientado a risco entre as camadas domain, application e infrastructure. Use quando revisar uma fatia implementada, auditar violacoes de camada (framework no domain, regra em controller), ou fazer code review antes de fechar uma feature.
argument-hint: "<arquivos ou escopo do modulo>"
---

# Skill de Verificacao de Consistencia Arquitetural

Use esta skill ao revisar ou implementar mudancas para garantir corretude, integridade da arquitetura e boa cobertura de testes.

## Prioridades de review (nesta ordem)

1. Bugs comportamentais e regressoes.
2. Violacoes de limite de camadas.
3. Riscos de tratamento de erro e seguranca.
4. Testes ausentes para fluxos criticos de negocio.
5. Problemas de manutenibilidade que podem causar defeitos futuros.

## Procedimento

1. Identifique os arquivos tocados e mapeie cada arquivo para uma camada.
2. Valide a direcao de dependencias e as regras de limite.
3. Detecte antipadroes (controller gordo, vazamento de framework no dominio, chamadas diretas de adapter/repository fora da camada correta).
4. Avalie impacto em tratamento de erro, seguranca e contratos de API.
5. Verifique cobertura minima de testes para caminho feliz, validacao e falha de gateway/adapter.
6. Sugira correcoes minimas e testes impactados.
7. Produza um relatorio de checklist acionavel.

## Formato de saida

- Achados primeiro, ordenados por severidade.
- Para cada achado, inclua:
  - caminho do arquivo
  - por que isso e um risco
  - direcao concreta de correcao
- Depois liste perguntas em aberto/premissas.
- Finalize com um resumo curto.
- Se nao houver achados, declare explicitamente: "Nenhum achado critico identificado" e liste riscos residuais.

## Recurso

- [Checklist de arquitetura](./checklist.md)
