plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.raillylinker"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // (기본)
    implementation("org.springframework.boot:spring-boot-starter:3.4.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.1.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")

    // (Spring Starter Web)
    // : 스프링 부트 웹 개발
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.0")

    // (Spring Actuator)
    // : 서버 모니터링 정보
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.4.0")

    // (ThymeLeaf)
    // : 웹 뷰 라이브러리
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.4.0")

    // (Swagger)
    // : API 자동 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // (Eureka Client)
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.2.0")

    // (Spring Admin Client)
    implementation("de.codecentric:spring-boot-admin-starter-client:3.4.0")

    // (Kafka)
    implementation("org.springframework.kafka:spring-kafka:3.2.4")

    // (jackson)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    jvmArgs("-Xshare:off")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0") // Cloud version
    }
}