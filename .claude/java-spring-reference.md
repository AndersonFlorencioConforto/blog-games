# Guia de Referencia Java + Spring

Este guia e um baseline pratico para iniciar novos projetos Java e Spring Boot usando fontes oficiais.

## Guias internos complementares

- Swagger/OpenAPI (uso pratico em endpoints REST): `./skills/spring-rest-endpoint/examples/swagger-openapi-controller-tags.md`
- Pagination em endpoints REST (contrato, defaults e testes): `./skills/spring-rest-endpoint/examples/pagination-rest-contract-and-controller.md`
- Pagination no fluxo DDD (domain -> application -> infrastructure): `./skills/ddd-use-case-scaffold/examples/pagination-domain-application-flow.md`
- build.gradle por modulo (domain/application/infrastructure): `./skills/java-spring-bootstrap/examples/build-gradle-modules-examples.md`
- Spring Security (Boot 4.x + Java 25): `./skills/spring-security-application/SKILL.md`

Observacao de estrutura:

- Regras obrigatorias ficam em `rules/*.md`.
- Playbooks e exemplos reutilizaveis ficam em `skills/*/examples/*.md`.

## 1) Guia de Versoes Java (8, 11, 17, 21, 25)

### Matriz rapida: o que mudou, como aplicar e quando usar

| Java | Tipo                  | Novidades praticas para aplicar                                                                                                                      | Como aplicar em projetos                                                                                                                           | Quando escolher                                       |
| ---- | --------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| 8    | LTS (baseline legado) | Lambdas, Stream API, default methods, java.time                                                                                                      | Refatore loops para streams quando melhorar legibilidade; substitua `Date/Calendar` por `java.time` em modelos de dominio e API                    | Apenas manutencao de legado                           |
| 11   | LTS                   | HTTP Client (JEP 321), Flight Recorder (JEP 328), TLS 1.3 (JEP 332), single-file launch (JEP 330)                                                    | Substitua stacks HTTP antigas em integracoes simples por `java.net.http`; use JFR em diagnostico de producao; use single-file para scripts         | Etapa de transicao de 8 para LTS moderna              |
| 17   | LTS                   | Records, sealed classes, pattern matching para `instanceof`, encapsulamento mais forte da JDK                                                        | Use records para DTOs/output imutaveis; hierarquias sealed para polimorfismo controlado; revise codigo legado com reflexao pesada                  | Baseline corporativo estavel                          |
| 21   | LTS                   | Virtual threads (JEP 444), record patterns (JEP 440), switch pattern matching (JEP 441), sequenced collections (JEP 431), generational ZGC (JEP 439) | Use virtual threads em servicos I/O-bound; simplifique ramificacoes com pattern matching; avalie generational ZGC para workloads de baixa latencia | Escolha padrao para novos servicos backend            |
| 25   | LTS                   | Scoped values (JEP 506), module import declarations (JEP 511), compact source files (JEP 512), generational Shenandoah (JEP 521)                     | Adote primeiro features estaveis (scoped values, melhor comportamento de runtime); mantenha preview/incubator isolado e documentado                | Servicos com visao de futuro apos validar ecossistema |

### Guia Java 8 (baseline legado)

Use recursos do Java 8 para manter codigo legado manutenivel enquanto prepara migracao.

```java
public BigDecimal totalPaid(final List<Order> orders) {
    return orders.stream()
            .filter(Order::isPaid)
            .map(Order::total)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

public LocalDate dueDatePlusDays(final LocalDate dueDate, final int days) {
    return dueDate.plusDays(days);
}
```

Quando usar:

- Plataforma/runtime existente esta travada em Java 8.
- Stack de dependencias ainda nao pode migrar com seguranca para 17/21.

Quando evitar:

- Servicos greenfield.
- Novos sistemas com alta concorrencia ou alto throughput.

### Guia Java 11 (baseline de transicao)

Use Java 11 para etapas de modernizacao antes de migrar para 17/21.

```java
final var client = java.net.http.HttpClient.newHttpClient();
final var request = java.net.http.HttpRequest.newBuilder(java.net.URI.create(url)).GET().build();
final var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
```

Quando usar:

- A migracao de Java 8 ja esta em andamento.
- O time quer upgrades incrementais com risco menor.

Quando evitar:

- Novos servicos onde Java 21 e viavel desde o primeiro dia.

### Guia Java 17 (baseline corporativo estavel)

Use Java 17 para modernizar a estrutura do codigo sem pular direto para 21.

```java
public record CustomerOutput(String id, String name) {}

public sealed interface VideoEvent permits VideoStarted, VideoCompleted, VideoFailed {}
```

Quando usar:

- Portfolios corporativos grandes com janelas conservadoras de upgrade.
- Dependencias ainda nao validadas totalmente no 21.

Quando evitar:

- Times que podem adotar 21 com seguranca e ja se beneficiar de virtual threads.

### Guia Java 21 (baseline moderno padrao)

Use Java 21 como alvo principal para novos servicos backend.

```java
try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
    final var futures = java.util.stream.IntStream.range(0, 100)
            .mapToObj(i -> executor.submit(() -> remoteClient.fetch(i)))
            .toList();
    for (final var future : futures) {
        process(future.get());
    }
}
```

Quando usar:

- Novas APIs e microservices.
- Workloads I/O-bound com alta concorrencia.

Quando evitar:

- Restricoes de tooling ou runtime que bloqueiem Java 21 em producao.

### Guia Java 25 (LTS com visao de futuro)

Use Java 25 de forma seletiva apos checagens de compatibilidade.

```java
private static final java.lang.ScopedValue<String> REQUEST_ID = java.lang.ScopedValue.newInstance();

void executeWithRequestId(final String requestId, final Runnable action) {
    java.lang.ScopedValue.where(REQUEST_ID, requestId).run(action);
}
```

Quando usar:

- Plataforma ja validada em Java 25.
- Necessidade de melhorias recentes de runtime e linguagem.

Quando evitar:

- Sistemas criticos onde integracoes de dependencias/frameworks ainda nao estao totalmente validadas.

### Caminho pratico de adocao (recomendado)

1. Java 8 -> 11: remova APIs obsoletas e modernize uso de HTTP/data-hora.
2. Java 11 -> 17: introduza records/sealed classes e remova acesso inseguro a internos da JDK.
3. Java 17 -> 21: adote virtual threads para fluxos de I/O com alta concorrencia.
4. Java 21 -> 25: adote recursos estaveis do 25, com features preview sob politica explicita.

## 2) Matriz de Compatibilidade Spring Boot

| Linha Spring Boot | Requisito e compatibilidade de Java    | Spring Framework relacionado | Ferramentas de build (suporte oficial) |
| ----------------- | -------------------------------------- | ---------------------------- | -------------------------------------- |
| 2.7.18            | Requer Java 8, compativel ate Java 21  | 5.3.31+                      | Maven 3.5+, Gradle 6.8/6.9/7/8         |
| 3.2.12            | Requer Java 17, compativel ate Java 23 | 6.1.x+                       | Maven 3.6.3+, Gradle 7.5+ e 8.x        |
| 4.0.5             | Requer Java 17, compativel ate Java 26 | 7.0.6+                       | Maven 3.6.3+, Gradle 8.14+ e 9.x       |

## 3) Recomendacao de Stack Padrao

- Novo servico (geral): Java 21 + Spring Boot 3.2+
- Novo servico com estrategia de longo prazo muito rigorosa: Java 21 agora, avaliar Java 25 + Spring Boot 4 apos checagem de maturidade de bibliotecas
- Plataforma existente Java 8/11: permanecer em Spring Boot 2.7 no curto prazo e criar backlog de migracao para Java 17/21 + Boot 3+
- Use features por versao Java de forma intencional:
  - Java 17: records/sealed classes para modelagem.
  - Java 21: virtual threads para concorrencia de I/O.
  - Java 25: somente novas features estaveis apos validacao de compatibilidade.

## 4) Deep Dive do Ecossistema Spring (o que escolher e quando)

### Spring Boot

- Foco: bootstrap da aplicacao, auto-configuracao, starters, Actuator, empacotamento e convencoes de runtime.
- Use quando: basicamente todo servico Spring moderno comeca aqui.
- Evite quando: quase nunca; apenas pule se voce quiser intencionalmente wiring manual do framework.
- Dependencias tipicas: `spring-boot-starter-web`, `spring-boot-starter-actuator`, `spring-boot-starter-security`.
- Nota de arquitetura: mantenha Boot no limite de infraestrutura; domain/application permanecem agnosticos de framework.

### Spring Framework

- Foco: container de DI, transacoes, stacks web (MVC/WebFlux), abstracoes de dados/integracao.
- Use quando: voce precisa de controle fino alem dos defaults do Boot ou recursos core avancados.
- Evite quando: servico simples onde auto-configuracao do Boot ja atende.
- Dependencias tipicas: transitivamente via starters Boot; uso direto em modulos avancados.
- Nota de arquitetura: trate como plumbing e evite vazamento de framework para o modelo de dominio.

### Spring Data

- Foco: abstracao de repository, derivacao de query, auditoria, modulos por storage (JPA/JDBC/R2DBC/NoSQL).
- Use quando: voce quer estilo consistente de acesso a dados em stores relacionais e nao relacionais.
- Evite quando: SQL e altamente especializado e repositories escondem demais (nesses casos SQL explicito pode ser melhor).
- Dependencias tipicas: `spring-boot-starter-data-jpa`, `spring-boot-starter-data-r2dbc`, starters especificos por modulo.
- Nota de arquitetura: mantenha abstracoes de repository em infraestrutura e mapeie para aggregates de dominio.

### Spring Cloud

- Foco: padroes de sistemas distribuidos (config, discovery, roteamento, resiliencia, comunicacao entre servicos).
- Use quando: ambiente multi-servico com topologia dinamica e necessidade de configuracao centralizada.
- Evite quando: monolito modular ou malha pequena onde complexidade operacional extra nao se justifica.
- Dependencias tipicas: BOM do Spring Cloud + starters especificos (`config`, `gateway`, `openfeign`, etc.).
- Nota de arquitetura: sempre alinhe release train do Cloud com linha do Boot via BOM.

### Spring Cloud Data Flow

- Foco: orquestracao de pipelines de stream e batch com Spring Cloud Stream/Task.
- Use quando: precisa de muitos pipelines de dados componiveis com orquestracao visual e por API.
- Evite quando: voce tem poucos jobs e pode orquestrar diretamente com ferramentas nativas da plataforma.
- Dependencias tipicas: servidor Data Flow + apps de stream/task.
- Status importante: o projeto nao e mais mantido como OSS pela Broadcom; avalie suporte enterprise ou alternativas antes de adotar.

### Spring gRPC

- Foco: abstracoes e starter no estilo Spring para desenvolvimento de servidor/cliente gRPC.
- Use quando: comunicacao interna service-to-service exige contratos fortes, baixa latencia e protocolo binario.
- Evite quando: consumidores de API publica sao majoritariamente browser/mobile e interoperabilidade REST/JSON e prioridade.
- Dependencias tipicas: starter Spring gRPC + stubs protobuf gerados.
- Nota de arquitetura: mantenha contratos de transporte em infraestrutura; mapeie DTOs protobuf para comandos/outputs de application.

### Spring Security

- Foco: autenticacao, autorizacao e protecao padrao contra ataques web comuns.
- Use quando: sempre em APIs e aplicacoes web de producao.
- Evite quando: nunca em producao; relaxe apenas em profiles isolados de desenvolvimento local, se necessario.
- Dependencias tipicas: `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`.
- Nota de arquitetura: mantenha politica de autorizacao declarativa e evite espalhar decisoes de seguranca em controllers.

### Spring Authorization Server

- Foco: construir seu proprio Authorization Server OAuth2/OIDC.
- Use quando: sua plataforma precisa emitir/validar tokens e controlar comportamento de provedor de identidade.
- Evite quando: IdP externo (Keycloak, Auth0, Entra ID etc.) ja atende aos requisitos.
- Dependencias tipicas: Spring Authorization Server (ou capacidades do Spring Security 7+).
- Status importante: Spring Authorization Server foi incorporado ao Spring Security 7; `1.5.x` e o ultimo branch standalone.

### Spring for GraphQL

- Foco: integracao Spring para GraphQL Java com arquitetura de servidor flexivel.
- Use quando: clientes precisam de consultas em grafo agregadas e selecao de campos.
- Evite quando: API e majoritariamente CRUD simples, onde REST e mais facil de operar e cachear.
- Dependencias tipicas: `spring-boot-starter-graphql`.
- Nota de arquitetura: mantenha schema/resolvers GraphQL em infraestrutura e delegue logica de negocio para casos de uso.

### Spring Integration

- Foco: Enterprise Integration Patterns (canais, transformers, routers, filters, gateways).
- Use quando: precisa de fluxos de integracao orquestrados entre protocolos e sistemas.
- Evite quando: consumidor/produtor de broker simples e suficiente e a camada EIP completa vira excesso.
- Dependencias tipicas: `spring-boot-starter-integration` + adapters de protocolo.
- Nota de arquitetura: modele fluxos de integracao como orquestracao de infraestrutura, nao como comportamento de dominio.

### Spring HATEOAS

- Foco: representacoes REST dirigidas por hipermidia (links, relation models, suporte HAL).
- Use quando: API e orientada a fluxo e clientes devem descobrir acoes por links.
- Evite quando: clientes sao controlados de forma estrita e contratos JSON simples sao suficientes.
- Dependencias tipicas: `spring-boot-starter-hateoas`.
- Nota de arquitetura: mantenha montagem de links na camada de presenter/representacao.

### Spring REST Docs

- Foco: documentacao de API orientada por testes gerada de testes de integracao reais.
- Use quando: precisa de documentacao confiavel sincronizada com comportamento real.
- Evite quando: time espera apenas docs design-first e nao mantem disciplina de cobertura de testes.
- Dependencias tipicas: `spring-restdocs-mockmvc` ou `spring-restdocs-webtestclient`, mais plugin Asciidoctor.
- Nota de arquitetura: use como quality gate em CI para evitar documentacao de API desatualizada.

### Spring AI

- Foco: APIs de IA portaveis entre provedores, abstracao de vector store, padroes RAG, tool/function calling.
- Use quando: integracao enterprise de IA exige portabilidade, observabilidade e saidas estruturadas.
- Evite quando: caso de uso e muito pequeno e preso a SDK de um unico provedor sem necessidade de portabilidade.
- Dependencias tipicas: starter de modelo (por exemplo OpenAI/Anthropic) + integracao com vector store.
- Nota de arquitetura: mantenha prompts/advisors em servicos de infraestrutura; proteja o dominio de tipos especificos de provedor.

### Spring Batch

- Foco: processamento batch robusto (chunking, controle transacional, restartability, retry/skip).
- Use quando: jobs agendados de alto volume, ETL, reconciliacao periodica.
- Evite quando: workloads request/response de baixa latencia.
- Dependencias tipicas: `spring-boot-starter-batch`.
- Nota de arquitetura: isole etapas de job e mantenha regras de negocio reutilizaveis em servicos de application/domain.

### Spring AMQP

- Foco: abstracao de mensageria orientada a RabbitMQ (`RabbitTemplate`, listener containers, suporte administrativo).
- Use quando: semantica de fila, routing keys, estrategias de dead-letter e roteamento no broker sao centrais.
- Evite quando: semantica de event-log/replay e streams particionados de alto throughput sao obrigatorios.
- Dependencias tipicas: `spring-boot-starter-amqp`.
- Nota de arquitetura: mantenha contratos de mensagem versionados e mapeie-os na borda de infraestrutura.

### Spring for Apache Kafka

- Foco: abstracoes Kafka (`KafkaTemplate`, `@KafkaListener`, transacoes, retryable topics).
- Use quando: event streaming, alto throughput, ordenacao por particao, replay e retention sao necessarios.
- Evite quando: semantica simples de work queue com modelo operacional mais leve e suficiente.
- Dependencias tipicas: `spring-kafka` (muitas vezes via auto-configuracao do Boot).
- Nota de arquitetura: trate evolucao de schema de topicos e idempotencia de consumidores como preocupacoes de primeira classe.

### Exemplos minimos de uso por tecnologia

#### Spring Boot

```java
@SpringBootApplication
@RestController
public class CatalogApplication {

    @GetMapping("/healthz")
    String health() {
        return "ok";
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }
}
```

#### Spring Framework

```java
@Configuration
class BillingConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}

@Service
class BillingService {
    private final Clock clock;

    BillingService(final Clock clock) {
        this.clock = clock;
    }
}
```

#### Spring Data

```java
@Entity
@Table(name = "categories")
class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}

interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByNameContainingIgnoreCase(String name);
}
```

#### Spring Cloud

```java
@EnableFeignClients
@SpringBootApplication
class OrderApplication {
}

@FeignClient(name = "catalog-service")
interface CatalogClient {

    @GetMapping("/products/{id}")
    ProductResponse getById(@PathVariable("id") String id);
}
```

#### Spring Cloud Data Flow

```text
dataflow:> app import --uri https://dataflow.spring.io/rabbitmq-maven-latest
dataflow:> stream create --name upperLog --definition "http | transform --expression=payload.toUpperCase() | log"
dataflow:> stream deploy --name upperLog
```

#### Spring gRPC

```java
@GrpcService
class GreetingGrpcService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        final var reply = HelloReply.newBuilder().setMessage("Ola " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
```

#### Spring Security

```java
@Configuration
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}

@Service
class InvoiceService {

    @PreAuthorize("hasRole('ADMIN')")
    void closeInvoice(String id) {
    }
}
```

#### Spring Authorization Server

```java
@Bean
RegisteredClientRepository registeredClientRepository() {
    final var client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("catalog-client")
            .clientSecret("{noop}secret")
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("catalog.read")
            .build();
    return new InMemoryRegisteredClientRepository(client);
}
```

#### Spring for GraphQL

```java
@Controller
class ProductGraphqlController {

    @QueryMapping
    ProductOutput productById(@Argument String id) {
        return new ProductOutput(id, "Keyboard");
    }
}
```

#### Spring Integration

```java
@Bean
IntegrationFlow upperCaseFlow() {
    return IntegrationFlow
            .from("inputChannel")
            .transform(String.class, String::toUpperCase)
            .handle(message -> System.out.println(message.getPayload()))
            .get();
}
```

#### Spring HATEOAS

```java
@GetMapping("/orders/{id}")
EntityModel<OrderResponse> getById(@PathVariable String id) {
    final var response = new OrderResponse(id, "CREATED");
    return EntityModel.of(response)
            .add(linkTo(methodOn(OrderController.class).getById(id)).withSelfRel());
}
```

#### Spring REST Docs

```java
@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs
class OrderApiDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDocumentGetOrder() throws Exception {
        mockMvc.perform(get("/orders/{id}", "123"))
                .andExpect(status().isOk())
                .andDo(document("orders-get"));
    }
}
```

#### Spring AI

```java
@Bean
CommandLineRunner aiDemo(ChatClient.Builder builder) {
    return args -> {
        final var chatClient = builder.build();
        final var response = chatClient.prompt("Resuma Java 21 em 3 bullets").call().content();
        System.out.println(response);
    };
}
```

#### Spring Batch

```java
@Bean
Job importJob(JobRepository jobRepository, Step importStep) {
    return new JobBuilder("importJob", jobRepository)
            .start(importStep)
            .build();
}

@Bean
Step importStep(JobRepository jobRepository,
                PlatformTransactionManager transactionManager,
                ItemReader<String> reader,
                ItemWriter<String> writer) {
    return new StepBuilder("importStep", jobRepository)
            .<String, String>chunk(100, transactionManager)
            .reader(reader)
            .writer(writer)
            .build();
}
```

#### Spring AMQP

```java
@Service
class InvoicePublisher {

    private final RabbitTemplate rabbitTemplate;

    InvoicePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    void publish(String payload) {
        rabbitTemplate.convertAndSend("billing.exchange", "invoice.created", payload);
    }
}

@Component
class InvoiceConsumer {

    @RabbitListener(queues = "invoice.created.q")
    void consume(String payload) {
        System.out.println(payload);
    }
}
```

#### Spring for Apache Kafka

```java
@Service
class OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    OrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void publish(String orderId, String eventJson) {
        kafkaTemplate.send("orders.events", orderId, eventJson);
    }
}

@Component
class OrderEventConsumer {

    @KafkaListener(topics = "orders.events", groupId = "billing")
    void onMessage(String payload) {
        System.out.println(payload);
    }
}
```

## 5) Exemplos de Codigo

### 5.1 Toolchain Gradle com baseline Java explicito

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.12'
    id 'io.spring.dependency-management' version '1.1.7'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 5.2 Baseline de Spring Security para APIs (JWT resource server)

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
```

### 5.3 Executor de virtual threads para Java 21+

```java
@Configuration
public class AsyncConfig {

    @Bean(destroyMethod = "close")
    ExecutorService applicationExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

### 5.4 Import de BOM do Spring Cloud (casar com sua linha de Boot)

```gradle
ext {
    set('springCloudVersion', '2025.0.0')
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

### 5.5 Padrao de arquivos `application.yml` por ambiente

- Use `application.yml` (ou `application.yaml`) como base comum e mantenha nele apenas configuracoes compartilhadas.
- Defina o profile ativo via variavel de ambiente para facilitar execucao local e deploy:

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:development}
```

- Organize sobrescritas por profile em arquivos especificos:
  - `application-development.yml`: defaults para desenvolvimento.
  - `application-local.yml`: ajustes exclusivos de maquina local (stubs, timeouts, endpoints locais).
  - `application-production.yml`: comportamento de producao sem segredos hardcoded.
  - `application-test-integration.yml`: configuracao de testes de integracao.
  - `application-test-e2e.yml`: configuracao de testes end-to-end.

- Exemplo minimo de override local:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
```

- Boas praticas:
  - segredo sempre em variavel de ambiente/secret manager.
  - evite duplicar blocos grandes entre profiles; mantenha no arquivo base e sobrescreva so diferencas.
  - padronize nomes de profile para evitar ambiguidade (`development`, `local`, `production`, `test-integration`, `test-e2e`).

## 6) Checklist de Decisao Antes de Iniciar Novo Projeto

1. Qual baseline Java e exigido pela plataforma e pelas dependencias?
2. Qual geracao do Spring Boot e compativel com esse baseline Java?
3. O projeto e MVC sincrono, reativo, orientado a eventos ou misto?
4. Qual modelo de dados (JPA/JDBC/R2DBC/NoSQL) e realmente necessario agora?
5. Spring Cloud e realmente necessario ou Boot puro e suficiente?
6. Qual baseline de observabilidade e obrigatorio (Actuator, metricas, tracing)?
7. Qual modo de seguranca e necessario desde o dia 1 (JWT, OAuth2 login, mTLS)?

## 7) Design Patterns em Java (conceito, quando usar e como usar)

### 7.1 Conceito

- Design pattern e um modelo de solucao recorrente para problemas de design, nao um codigo pronto para copiar.
- Patterns ajudam a capturar decisoes de arquitetura e design de forma reutilizavel.
- Diferenca pratica:
  - algoritmo: passos definidos para chegar a um resultado.
  - pattern: estrutura de colaboracao entre classes/objetos que pode variar conforme o contexto.

### 7.2 Classificacao pratica

- Criacionais: focam em como criar objetos com flexibilidade e baixo acoplamento.
  - exemplos: Factory Method, Abstract Factory, Builder, Prototype.
- Estruturais: focam em composicao e adaptacao entre classes/objetos.
  - exemplos: Adapter, Facade, Decorator, Proxy.
- Comportamentais: focam em comunicacao e distribuicao de responsabilidades.
  - exemplos: Strategy, Observer, State, Template Method, Chain of Responsibility.

### 7.3 Quando usar

- Quando existe variacao real de comportamento e if/else cresce sem controle.
  - candidato tipico: Strategy ou State.
- Quando a criacao de objeto tem muitos campos opcionais/combinacoes e construtores ficam confusos.
  - candidato tipico: Builder.
- Quando precisa integrar API externa com contrato diferente do seu dominio.
  - candidato tipico: Adapter.
- Quando um subsistema e complexo e voce precisa de uma interface de entrada mais simples.
  - candidato tipico: Facade.
- Quando ha eventos relevantes para multiplos consumidores.
  - candidato tipico: Observer.

### 7.4 Quando evitar

- Quando o problema ainda e simples e sem variacao recorrente.
- Quando o pattern entra apenas por "boa pratica" sem dor concreta.
- Quando o pattern aumenta classes/indirecao sem ganho de legibilidade, testabilidade ou evolucao.
- Quando a linguagem/framework ja resolve o problema de forma mais simples.

### 7.5 Como aplicar em Java neste estilo DDD + camadas

- Domain:
  - prefira Strategy, Specification, State e fabricas explicitas para proteger invariantes.
  - evite singleton mutavel global no dominio.
- Application:
  - preserve caso de uso como orquestrador; use composicao de servicos em vez de heranca desnecessaria.
  - use Chain of Responsibility somente quando houver pipeline real de regras.
- Infrastructure:
  - Adapter para integrar persistencia, mensageria e clientes externos.
  - Facade para esconder detalhes de provedores externos e SDKs.
  - Proxy quando precisar de controle de acesso, cache ou telemetria em volta de um cliente.

### 7.6 Exemplo minimo de Strategy (dominio)

```java
public interface PricePolicy {
        java.math.BigDecimal apply(java.math.BigDecimal basePrice);
}

public final class DefaultPricePolicy implements PricePolicy {
        @Override
        public java.math.BigDecimal apply(final java.math.BigDecimal basePrice) {
                return basePrice;
        }
}

public final class PromotionalPricePolicy implements PricePolicy {
        private final java.math.BigDecimal discountRate;

        public PromotionalPricePolicy(final java.math.BigDecimal discountRate) {
                this.discountRate = discountRate;
        }

        @Override
        public java.math.BigDecimal apply(final java.math.BigDecimal basePrice) {
                return basePrice.subtract(basePrice.multiply(discountRate));
        }
}
```

### 7.7 Exemplo minimo de Adapter (infraestrutura)

```java
public interface PaymentGateway {
        PaymentResult charge(String customerId, java.math.BigDecimal amount);
}

@org.springframework.stereotype.Component
public class AcmePaymentGatewayAdapter implements PaymentGateway {

        private final AcmePaymentClient client;

        public AcmePaymentGatewayAdapter(final AcmePaymentClient client) {
                this.client = client;
        }

        @Override
        public PaymentResult charge(final String customerId, final java.math.BigDecimal amount) {
                final var response = this.client.createCharge(customerId, amount);
                return new PaymentResult(response.transactionId(), response.approved());
        }
}
```

## 8) SOLID em Java (conceito, quando usar e como usar)

### 8.1 Conceito

- SOLID e um conjunto de cinco principios para manter codigo OO mais coeso, extensivel e de baixo acoplamento.
- O foco nao e criar mais camadas por padrao, e sim reduzir custo de mudanca com fronteiras claras.
- Os cinco principios:
  - S: Single Responsibility Principle (SRP)
  - O: Open Closed Principle (OCP)
  - L: Liskov Substitution Principle (LSP)
  - I: Interface Segregation Principle (ISP)
  - D: Dependency Inversion Principle (DIP)

### 8.2 Quando usar

- Quando o modulo muda por muitos motivos diferentes e regressao vira frequente.
- Quando novas variacoes de comportamento exigem mexer em classes estaveis.
- Quando testes sao caros por alto acoplamento entre politica de negocio e detalhes tecnicos.
- Quando interfaces ficaram "gordas" e implementacoes ignoram ou quebram metodos.

### 8.3 Quando evitar aplicacao mecanica

- Quando o problema ainda e pequeno e sem sinal real de variacao.
- Quando a abstracao e criada apenas para "cumprir SOLID" sem consumidor real.
- Quando DIP vira excesso de interfaces sem ganho de testabilidade ou substituicao.
- Quando a complexidade adicional supera o beneficio para o contexto atual.

### 8.4 Como aplicar cada principio em Java

- SRP:
  - regra: uma classe/modulo deve ter um motivo claro de mudanca.
  - em Java: separe regras de negocio de formatacao, persistencia, transporte e seguranca.
  - sinal de violacao: classe com metodos de dominio + SQL + serializacao no mesmo arquivo.
- OCP:
  - regra: estender comportamento sem editar codigo estavel a cada nova variacao.
  - em Java: use polimorfismo (Strategy, Template Method, plugins por interface) para pontos de variacao.
  - sinal de violacao: cadeia crescente de if/else por tipo/canal/status.
- LSP:
  - regra: subtipo deve poder substituir tipo base sem quebrar contrato.
  - em Java: mantenha pre-condicoes iguais ou mais fracas e pos-condicoes iguais ou mais fortes.
  - sinal de violacao: implementacao que lanca UnsupportedOperationException em metodo herdado esperado.
- ISP:
  - regra: clientes nao devem depender de metodos que nao usam.
  - em Java: prefira interfaces pequenas por caso de uso, em vez de um "mega gateway".
  - sinal de violacao: classe implementa interface grande e deixa metodos vazios.
- DIP:
  - regra: politica de alto nivel depende de abstracoes; detalhes dependem das mesmas abstracoes.
  - em Java/Spring: caso de uso depende de gateway (interface), adapter concreto vive em infrastructure.
  - sinal de violacao: use case importando repository/JPA/client HTTP concreto.

### 8.5 Aplicacao por camada (DDD + Clean)

- Domain:
  - aplique SRP em entidades/value objects e mantenha invariantes perto do dominio.
  - use interfaces apenas quando houver colaboracao real com variacao de implementacao.
- Application:
  - aplique DIP para depender de gateways/contratos, nao de detalhes de framework.
  - mantenha caso de uso como orquestrador, sem logica de transporte HTTP.
- Infrastructure:
  - implemente detalhes (JPA, mensageria, HTTP clients) por adapters conectados aos contratos.
  - aplique ISP em contratos externos para nao vazar API extensa para o nucleo.

### 8.6 Exemplo minimo de DIP (application + infrastructure)

```java
public interface NotificationGateway {
                void notifyUser(String userId, String message);
}

public final class DefaultSendNotificationUseCase {
                private final NotificationGateway notificationGateway;

                public DefaultSendNotificationUseCase(final NotificationGateway notificationGateway) {
                                this.notificationGateway = java.util.Objects.requireNonNull(notificationGateway);
                }

                public void execute(final String userId, final String message) {
                                this.notificationGateway.notifyUser(userId, message);
                }
}

@org.springframework.stereotype.Component
public class EmailNotificationGatewayAdapter implements NotificationGateway {

                private final EmailClient emailClient;

                public EmailNotificationGatewayAdapter(final EmailClient emailClient) {
                                this.emailClient = emailClient;
                }

                @Override
                public void notifyUser(final String userId, final String message) {
                                this.emailClient.send(userId, message);
                }
}
```

### 8.7 Padroes DDD implementados neste projeto (IDs, Validation, Value Object)

- Base de dominio:
  - `Identifier` estende `ValueObject` e expoe `getValue()`.
  - `Entity<ID extends Identifier>` concentra identidade e eventos de dominio (`registerEvent`, `publishDomainEvents`).
  - `AggregateRoot<ID extends Identifier>` e a base de agregados.
- Padrao de IDs (`CategoryID`, `GenreID`, `CastMemberID`, `VideoID`):
  - `unique()` para gerar novo ID via `IdUtils.uuid()`.
  - `from(String)` para reidratar ID vindo de camada externa/persistencia.
  - `equals/hashCode` por valor.
- Validacao:
  - `Validator` + `ValidationHandler` + `Notification` para acumular erros de dominio.
  - Cada agregado implementa `validate(handler)` e delega para `XValidator`.
  - Em `application`, o use case agrega erros de validacao local e de relacionamentos externos (ex.: `existsById`).
- Diretriz de erro para o kit (novos fluxos):
  - usar excecoes previsiveis de dominio (`NotificationException`, `NotFoundException`, `DomainException`).
  - manter consistencia dentro do mesmo modulo/caso de uso.
- Cuidados praticos:
  - em comandos com colecoes de IDs, trate `null` como vazio antes de `stream()`.
  - em erros de not found, use o ID do proprio agregado (`NotFoundException.with(X.class, xId)`).

## 9) Fontes Oficiais Usadas

- Novidades do Java 8: https://www.oracle.com/java/technologies/javase/8-whats-new.html
- OpenJDK JDK 11: https://openjdk.org/projects/jdk/11/
- OpenJDK JDK 17: https://openjdk.org/projects/jdk/17/
- OpenJDK JDK 21: https://openjdk.org/projects/jdk/21/
- OpenJDK JDK 25: https://openjdk.org/projects/jdk/25/
- Catalogo de projetos Spring: https://spring.io/projects
- Pagina do projeto Spring Boot: https://spring.io/projects/spring-boot
- Pagina do Spring Framework: https://spring.io/projects/spring-framework
- Pagina do Spring Data: https://spring.io/projects/spring-data
- Pagina do Spring Security: https://spring.io/projects/spring-security
- Pagina do Spring Cloud: https://spring.io/projects/spring-cloud
- Pagina do Spring Cloud Data Flow: https://spring.io/projects/spring-cloud-dataflow
- Pagina do Spring gRPC: https://spring.io/projects/spring-grpc
- Pagina do Spring Authorization Server: https://spring.io/projects/spring-authorization-server
- Pagina do Spring for GraphQL: https://spring.io/projects/spring-graphql
- Pagina do Spring HATEOAS: https://spring.io/projects/spring-hateoas
- Pagina do Spring REST Docs: https://spring.io/projects/spring-restdocs
- Pagina do Spring AI: https://spring.io/projects/spring-ai
- Pagina do Spring Batch: https://spring.io/projects/spring-batch
- Pagina do Spring Integration: https://spring.io/projects/spring-integration
- Pagina do Spring AMQP: https://spring.io/projects/spring-amqp
- Pagina do Spring for Apache Kafka: https://spring.io/projects/spring-kafka
- Requisitos de sistema Spring Boot 4: https://docs.spring.io/spring-boot/system-requirements.html
- Requisitos Spring Boot 3.2.12: https://docs.spring.io/spring-boot/docs/3.2.12/reference/html/getting-started.html#getting-started.system-requirements
- Requisitos Spring Boot 2.7.18: https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/getting-started.html#getting-started.system-requirements
- Spring Boot Externalized Configuration: https://docs.spring.io/spring-boot/reference/features/external-config.html
- Spring Boot Profiles: https://docs.spring.io/spring-boot/reference/features/profiles.html
- What is a design pattern: https://refactoring.guru/design-patterns/what-is-pattern
- Why learn patterns: https://refactoring.guru/design-patterns/why-learn-patterns
- Classification of patterns: https://refactoring.guru/design-patterns/classification
- Java catalog of patterns: https://refactoring.guru/design-patterns/java
- Java design patterns catalog: https://java-design-patterns.com/patterns/
- SourceMaking design patterns: https://sourcemaking.com/design_patterns
- SOLID (visao geral e historico): https://en.wikipedia.org/wiki/SOLID
- Single Responsibility Principle: https://en.wikipedia.org/wiki/Single-responsibility_principle
- Dependency Inversion Principle: https://en.wikipedia.org/wiki/Dependency_inversion_principle
- The Single Responsibility Principle (Uncle Bob): https://blog.cleancoder.com/uncle-bob/2014/05/08/SingleReponsibilityPrinciple.html
- The Open Closed Principle (Uncle Bob): https://blog.cleancoder.com/uncle-bob/2014/05/12/TheOpenClosedPrinciple.html
- Java principles catalog: https://java-design-patterns.com/principles/
