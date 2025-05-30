plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kotlin_springboot_mvc_template"

// (모듈 모음)
// !!!모듈 추가/수정시 아래에 반영!!!

// Spring Cloud 게이트웨이 (8080)
include("module-cloud-gateway")

// Spring Admin Server (9090)
include("module-spring-admin")

// Spring Admin Client1 (9191)
include("module-spring-admin-client1")

// Spring Admin Client2 (9192)
include("module-spring-admin-client2")

// Springboot 프로젝트 모듈 템플릿 (10000)
include("module-template")

// Spring Cloud 유레카 서버1 (10001)
include("module-cloud-eureka1")

// Spring Cloud 유레카 서버2 (10002)
include("module-cloud-eureka2")

// Spring Cloud 유레카 서버3 (10003)
include("module-cloud-eureka3")

// Spring Cloud 클라이언트 샘플 (10101)
include("module-cloud-sample-client")

// Spring Cloud 클라이언트 샘플 복제 (10102)
include("module-cloud-sample-client-copy")

// 인증/인가 서버 (11000)
include("module-auth")

// 파일 저장소 서버 (11001)
include("module-file-storage")

// 결제 서버 (11002)
include("module-payment")

// 테스트 샘플 (12000)
include("module-sample-etc")

// 스케쥴러 테스트 샘플 (12003)
include("module-sample-scheduler")

// 소켓 테스트 샘플 (12004)
include("module-sample-socket")

// Kafka 테스트 Server (12005)
include("module-sample-kafka")

// API 테스트 샘플 (12006)
include("module-sample-api")

// Retrofit2 테스트 샘플 (12007)
include("module-sample-retrofit2")

// Redis 테스트 샘플 (12008)
include("module-sample-redis")

// Mongodb 테스트 샘플 (12009)
include("module-sample-mongodb")

// JPA 테스트 샘플 (12010)
include("module-sample-jpa")

// Batch 테스트 샘플 (12011)
include("module-sample-batch")

// Quartz 테스트 샘플 (12012)
include("module-sample-quartz")

// 시큐리티 필터 적용 테스트 샘플 (12013)
include("module-sample-just-security")

// 고급 STOMP 소켓 샘플 (12014)
include("module-sample-socket-stomp-ex")

// 게시판 서비스 (13000)
include("module-service-board")

// 예약 대여 서비스 (13001)
include("module-service-rental-reservation")