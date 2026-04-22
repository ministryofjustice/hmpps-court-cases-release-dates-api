plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.2.1"
  id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
  kotlin("plugin.spring") version "2.3.20"
  id("org.openapi.generator") version "7.4.0"
}

configurations {
  testImplementation {
    exclude(group = "org.junit.vintage")
  }
  testImplementation {
    exclude(module = "slf4j-simple")
  }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.0.2") {
    exclude(group = "com.fasterxml.jackson.core")
  }
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:7.0.1")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  testImplementation("org.springframework.boot:spring-boot-webtestclient")
  testImplementation("org.wiremock:wiremock-standalone:3.13.2")
  testImplementation("io.swagger.parser.v3:swagger-parser-v2-converter:2.1.39")
  testImplementation("org.testcontainers:testcontainers:2.0.3")
  testImplementation("org.testcontainers:localstack:1.21.4")
  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.0.2")
  testImplementation("com.github.codemonstur:embedded-redis:1.4.3")
  testImplementation("org.awaitility:awaitility-kotlin")
}

kotlin {
  compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(25))
  }
}
