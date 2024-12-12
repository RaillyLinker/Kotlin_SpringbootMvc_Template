package com.raillylinker.module_spring_admin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import java.util.*

@SpringBootApplication
@EnableAdminServer
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