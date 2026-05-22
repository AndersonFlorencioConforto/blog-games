---
name: "Versionamento da Plataforma Java"
description: "Regras para selecionar e usar Java 8/11/17/21/25 com seguranca no codigo e nos arquivos de build."
applyTo: "**/*.java,**/build.gradle,**/build.gradle.kts,**/pom.xml"
---

# Regras de Versionamento da Plataforma Java

## Selecao de baseline

- Baseline padrao para novos servicos backend: Java 21.
- Use Java 17 quando dependencias chave ainda nao estiverem estaveis no 21.
- Use Java 25 apenas apos validar suporte de dependencias e runtime.
- Mantenha Java 8/11 apenas para manutencao ou etapas de migracao.

## A configuracao de build deve ser explicita

- Sempre declare toolchain/release de Java nos arquivos de build.
- Nunca confie em defaults implicitos de JDK local.

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.release>21</maven.compiler.release>
</properties>
```

## Disciplina de features de linguagem

- Nao use features de linguagem acima do baseline de runtime configurado.
- Se o baseline do projeto for Java 17, evite sintaxe e APIs de Java 21+.
- Features preview devem ser opt-in e documentadas; nao as habilite por padrao em producao.

## Guia de features por geracao

- Caminhos Java 8/11: mantenha sintaxe conservadora, evite sintaxe de record/sealed/pattern.
- Java 17+: prefira `record` para DTOs imutaveis e hierarquias `sealed` quando util.
- Java 21+: avalie virtual threads para cargas com I/O intenso.
- Java 25+: sinalize claramente features somente preview e forneca padroes de fallback.

## Adocao pratica por versao

- Java 8:
  - Aplicar: Streams/lambdas para transformacoes de colecao, `java.time` para data/hora.
  - Usar quando: existem restricoes de runtime legado.
  - Evitar para: novos servicos greenfield.
- Java 11:
  - Aplicar: `java.net.http.HttpClient` para integracoes HTTP diretas, JFR para diagnostico em producao.
  - Usar quando: etapa de migracao de 8 para LTS moderna.
  - Evitar para: sistemas novos onde Java 21 e viavel.
- Java 17:
  - Aplicar: DTOs `record`, hierarquias sealed, pattern matching mais seguro para `instanceof`.
  - Usar quando: baseline corporativo com politica conservadora de upgrade.
  - Atencao: acesso reflexivo a internos da JDK devido encapsulamento mais forte.
- Java 21:
  - Aplicar: virtual threads para concorrencia bound a I/O, padroes de switch/record para reduzir complexidade de ramificacao.
  - Usar quando: novos servicos backend e APIs de alta concorrencia.
  - Atencao: faça benchmark de fluxos CPU-bound antes de adocao ampla de virtual threads.
- Java 25:
  - Aplicar: primeiro features novas estaveis (por exemplo scoped values), avaliar melhorias de runtime apos testes de compatibilidade.
  - Usar quando: ecossistema de dependencias e ferramentas de plataforma estiver validado para 25.
  - Atencao: features preview/incubator exigem governanca explicita e plano de fallback.

## Comportamento de runtime e seguranca de migracao

- Evite acesso reflexivo a internos da JDK; Java 17+ encapsula internos de forma forte.
- Durante migracao, teste serializacao/deserializacao e comportamento de data/hora explicitamente.
- Ao substituir APIs antigas, prefira APIs padrao de Java em vez de wrappers de terceiros, quando possivel.

## Requisito de documentacao

- Todo novo modulo deve documentar:
  - baseline Java selecionado
  - motivo da escolha da versao
  - caminho de upgrade planejado para a proxima LTS
- Se uma feature especifica de versao Java for adotada, documente:
  - por que essa feature e necessaria
  - impacto operacional esperado
  - abordagem de rollback/fallback
