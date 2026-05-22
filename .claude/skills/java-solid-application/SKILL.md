---
name: java-solid-application
description: Diagnostica violacoes SOLID e aplica refatoracoes incrementais em Java com impacto controlado por camada. Use quando uma classe tiver multiplas responsabilidades, estiver dificil de testar, com alto acoplamento, ou ao refatorar aplicando principios SOLID.
argument-hint: "<sintoma/arquivos> [restricoes]"
---

# Skill de Aplicacao de SOLID em Java

Use esta skill quando houver sinais de alto acoplamento, classes com multiplos motivos de mudanca, interfaces inchadas ou dependencia direta de detalhes tecnicos no nucleo da regra de negocio.

## Entradas esperadas

- Sintoma observado no codigo (ex.: if/else crescendo, classe gigante, contrato quebradico).
- Escopo de modulo/arquivos impactados.
- Restricoes (prazo, compatibilidade, performance, risco de regressao).

## Procedimento

1. Mapeie sinais de problema para os principios SRP, OCP, LSP, ISP e DIP.
2. Priorize violacoes por risco de regressao e custo de mudanca.
3. Defina o menor recorte de refatoracao que gera ganho real de coesao/testabilidade.
4. Selecione tecnica de refatoracao adequada:
   - extracao de classe/modulo para SRP
   - pontos de extensao por polimorfismo para OCP
   - ajuste de contrato e hierarquia para LSP
   - quebra de interfaces grandes para ISP
   - introducao/ajuste de abstracoes para DIP
5. Aplique mudancas de forma incremental, preservando comportamento externo.
6. Verifique limites de camada domain/application/infrastructure antes de concluir.
7. Adicione/atualize testes para caminho feliz, falhas de contrato e cenarios de substituicao.
8. Registre riscos residuais e gatilhos para revisitar o desenho.

## Regras obrigatorias

- Nao criar abstracao sem variacao real prevista ou existente.
- Nao criar "interface para tudo" apenas para mock.
- Nao mover regra de negocio para infrastructure.
- Nao quebrar contrato publico sem plano de migracao explicito.
- Preferir composicao a heranca quando reduzir acoplamento e risco de LSP.

## Formato de saida

1. Problema observado e impacto.
2. Violacoes SOLID identificadas por principio.
3. Refatoracao escolhida e justificativa de menor complexidade.
4. Plano de mudanca por arquivo/camada.
5. Plano de testes.
6. Riscos residuais e mitigacoes.

## Recursos

- [Matriz de diagnostico SOLID](./examples/solid-diagnostic-matrix.md)
