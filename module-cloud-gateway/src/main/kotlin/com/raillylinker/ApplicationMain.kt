package com.raillylinker

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import java.util.*

// Spring Cloud Eureka 에서 등록된 마이크로 서비스의 api 가 503(Service Unavailable) 을 반환하면,
// 동일하게 등록된 다른 복제 서버에 로드 밸런싱을 하게 됩니다.
@ComponentScan(
    // !!!Bean 스캔할 모듈들의 패키지 리스트(group) 추가하기!!!
    basePackages =
        [
            "com.raillylinker"
        ]
)
@SpringBootApplication
class ApplicationMain {
    @Bean
    fun init() = CommandLineRunner {
        // 서버 타임존 명시적 설정 (UTC, Asia/Seoul, ...)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
        // println("Current TimeZone: ${TimeZone.getDefault().id}")
    }
}

fun main(args: Array<String>) {
    runApplication<ApplicationMain>(*args)
}