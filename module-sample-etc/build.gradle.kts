plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"

    // JPA 추가
    kotlin("plugin.allopen") version "2.0.21" // allOpen 에 지정한 어노테이션으로 만든 클래스에 open 키워드를 적용
    kotlin("plugin.noarg") version "2.0.21" // noArg 에 지정한 어노테이션으로 만든 클래스에 자동으로 no-arg 생성자를 생성

    // QueryDSL Kapt
    kotlin("kapt") version "2.0.21"
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

    // (폰트 파일 내부 이름 가져오기용)
    implementation("org.apache.pdfbox:pdfbox:3.0.3")

    // (JSOUP - HTML 태그 조작)
    implementation("org.jsoup:jsoup:1.18.1")

    // (AWS)
    implementation("io.awspring.cloud:spring-cloud-starter-aws:2.4.4")

    // (Spring email)
    // : 스프링 이메일 발송
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.5")

    // (Excel File Read Write)
    // : 액셀 파일 입출력 라이브러리
    implementation("org.apache.poi:poi:5.3.0")
    implementation("org.apache.poi:poi-ooxml:5.3.0")
    implementation("sax:sax:2.0.1")

    // (HTML 2 PDF)
    // : HTML -> PDF 변환 라이브러리
    implementation("org.xhtmlrenderer:flying-saucer-pdf:9.10.2")

    // (retrofit2 네트워크 요청)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

    // (OkHttp3)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // (JPA)
    // : DB ORM
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5:2.18.1")
    implementation("org.hibernate:hibernate-validator:8.0.1.Final")
    implementation("com.mysql:mysql-connector-j:9.1.0") // MySQL

    // (QueryDSL)
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api:3.0.0")
    kapt("jakarta.persistence:jakarta.persistence-api:3.2.0")
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

// (Querydsl 설정부 추가 - start)
val generated = file("src/main/generated")
// querydsl QClass 파일 생성 위치를 지정
tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generated)
}
// kotlin source set 에 querydsl QClass 위치 추가
sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}
// gradle clean 시에 QClass 디렉토리 삭제
tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
}
kapt {
    generateStubs = true
}
// (Querydsl 설정부 추가 - end)

// kotlin jpa : 아래의 어노테이션 클래스에 no-arg 생성자를 생성
noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// kotlin jpa : 아래의 어노테이션 클래스를 open class 로 자동 설정
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}