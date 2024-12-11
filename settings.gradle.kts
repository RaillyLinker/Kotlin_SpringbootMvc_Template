plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kotlin_springboot_mvc_template"

// (모듈 모음)
// !!!모듈 추가/수정시 아래에 반영!!!
// Springboot 프로젝트 모듈 템플릿 (10000)
include("module-template")

// Spring Cloud 유레카 서버1 (10001)
include("module-cloud-eureka1")

// Spring Cloud 유레카 서버2 (10002)
include("module-cloud-eureka2")

// Spring Cloud 유레카 서버3 (10003)
include("module-cloud-eureka3")

// Spring Cloud 클라이언트 샘플 (12000)
include("module-sample-cloud-client")

// Spring Cloud 클라이언트 샘플 복제 (12001)
include("module-sample-cloud-client-copy")

// Spring Cloud 게이트웨이 (8080)
include("module-cloud-gateway")
