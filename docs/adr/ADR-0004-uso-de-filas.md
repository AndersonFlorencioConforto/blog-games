# ADR-0004 - Decisao sobre Uso de Filas (AMQP/Kafka)

**Data:** 2026-05-21  
**Status:** Aceito - Sem filas no escopo atual  
**Decisores:** Anderson Florencio Conforto

---

## Contexto

O usuario questionou explicitamente se ha necessidade de usar sistemas de mensageria (RabbitMQ/AMQP, Kafka, etc.) na plataforma. Esta decisao impacta diretamente a complexidade de infraestrutura, operacao e desenvolvimento.

Foi realizada analise criteriosamente de todos os casos de uso da plataforma para identificar se ha necessidade real de desacoplamento via mensageria.

---

## Analise dos Casos de Uso

### Envio de E-mail (recuperacao de senha)
- **Volume esperado:** Baixo (recuperacao de senha e evento esporadico)
- **Tolerancia a latencia:** Alta (usuario aguarda o e-mail; alguns segundos sao aceitaveis)
- **Consequencia de falha:** Detectavel imediatamente; usuario recebe erro
- **Conclusao:** JavaMailSender sincrono e suficiente. Fila nao justificada.

### Calculo de media de estrelas ao avaliar jogo
- **Volume esperado:** Medio (avaliacoes ocorrem com frequencia moderada)
- **Tolerancia a latencia:** Alta (media pode ser eventual; usuario nao percebe delay de 1-2s)
- **Conclusao:** UPDATE em campo `averageRating` na mesma transacao ou via consulta AVG. Sem necessidade de fila.

### Notificacao de resposta em thread (futuro)
- **Volume esperado:** Medio-alto em jogos populares
- **Tolerancia a latencia:** Alta (notificacao pode chegar com delay)
- **Conclusao:** Nao esta no escopo atual. Se implementado, avaliar neste ADR.

### Processamento de upload de imagem (futuro)
- **Volume esperado:** Baixo-medio
- **Conclusao:** Fora do escopo do Release 1. Upload aceita URL externa.

### Moderacao de denuncias
- **Volume esperado:** Baixo
- **Conclusao:** Fluxo sincrono simples; sem necessidade de fila.

---

## Decisao

**NAO adotar nenhum sistema de mensageria (RabbitMQ, Kafka, ActiveMQ, AWS SQS) no escopo atual.**

Todos os casos de uso identificados podem ser atendidos com chamadas sincronas. A adicao de mensageria neste momento introduziria:
- Complexidade operacional desproporcional ao porte do sistema
- Dependencia adicional de infraestrutura (broker) para rodar localmente
- Necessidade de lidar com idempotencia, retries, DLQ e monitoramento do broker
- Overhead cognitivo sem beneficio claro

---

## Opcoes Avaliadas

### Opcao A - RabbitMQ (AMQP)
**Pros:** Amplamente adotado; facil de usar com Spring AMQP; bom para task queues  
**Contras:** Requer broker externo; complexidade operacional; sem justificativa de volume  
**Rejeitado:** Sem caso de uso que justifique

### Opcao B - Apache Kafka
**Pros:** Alta throughput; event sourcing; replay de eventos  
**Contras:** Infraestrutura pesada; curva de aprendizado; absolutamente desproporcional para uma plataforma de catalogo de jogos no estagio atual  
**Rejeitado:** Overkill claro para o contexto

### Opcao C - Spring Events (ApplicationEvent) para desacoplamento interno
**Pros:** Zero infraestrutura adicional; desacoplamento de modulos internos; suporte a `@TransactionalEventListener`  
**Contras:** Nao persiste eventos; sem retry automatico em falhas de handlers  
**Aceito parcialmente:** Pode ser usado para desacoplamento interno (ex: apos salvar avaliacao, publicar evento interno para atualizar media). Nao e fila externa.

### Opcao D - Sem mensageria (ESCOLHIDA)
**Pros:** Simplicidade maxima; zero dependencias externas adicionais; facil de entender e manter  
**Contras:** Acoplamento sincrono em todos os fluxos; se JavaMailSender travar, a requisicao trava  
**Aceito:** Para o volume esperado, os contras sao gerenciaveis

---

## Criterios para Revisitar esta Decisao

Esta decisao deve ser revisada (novo ADR) se:

1. O tempo de envio de e-mail passar a impactar perceptivelmente a UX (ex: timeout frequente do provedor SMTP)
2. Funcionalidade de notificacoes em tempo real for adicionada ao roadmap
3. Volume de avaliacoes causar contenao no banco ao atualizar `averageRating`
4. Processamento de imagens (resize, CDN) for internalizado

---

## Consequencias

- **Event Strategy** interna usa `ApplicationEvent` do Spring para desacoplamento opcional entre casos de uso na camada `application`
- Envio de e-mail e chamado diretamente do caso de uso `RequestPasswordResetUseCase` via porta `EmailPort` (interface em `domain` ou `application`), implementada em `infrastructure`
- Timeout do cliente SMTP deve ser configurado para nao bloquear threads por mais de 5 segundos
- Nenhuma dependencia de RabbitMQ, Kafka ou similar deve ser adicionada ao `build.gradle`
