# Exemplos de build.gradle por modulo

Este guia mostra exemplos de `build.gradle` para arquitetura modular com `domain`, `application` e `infrastructure`.

## Root (orquestracao)

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.12' apply false
    id 'io.spring.dependency-management' version '1.1.6' apply false
}

allprojects {
    group = 'br.com.andersondev.admin.catalog'
    version = '1.0.0'
    repositories { mavenCentral() }
}

subprojects {
    apply plugin: 'java'
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    tasks.withType(Test).configureEach {
        useJUnitPlatform()
    }
}
```

## domain/build.gradle

```groovy
plugins {
    id 'java-library'
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
}
```

Regra: sem dependencias Spring no `domain`.

## application/build.gradle

```groovy
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':domain')

    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.11.0'
}
```

Regra: `application` depende de `domain`, mas nao de web framework.

## infrastructure/build.gradle

```groovy
plugins {
    id 'org.springframework.boot'
    id 'io.spring.dependency-management'
    id 'java'
}

dependencies {
    implementation project(':domain')
    implementation project(':application')

    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

Regra: `infrastructure` pode depender de Spring e integra com os casos de uso da `application`.

## Comandos uteis de validacao

```bash
./gradlew :domain:check
./gradlew :application:check
./gradlew :infrastructure:check
./gradlew build
```
