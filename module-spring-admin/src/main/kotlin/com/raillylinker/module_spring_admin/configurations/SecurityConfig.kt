package com.raillylinker.module_spring_admin.configurations

import de.codecentric.boot.admin.server.config.AdminServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


// [서비스 보안 시큐리티 설정]
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>

    // !!!경로별 적용할 Security 필터 체인 Bean 작성하기!!!

    // [기본적으로 모든 요청 Open]
    @Bean
    @Order(Int.MAX_VALUE)
    fun securityFilterChainMainSc(
        http: HttpSecurity,
        adminServer: AdminServerProperties
    ): SecurityFilterChain {
        // 로그인 성공 시 메인페이지로 이동
        val loginSuccessHandler = SavedRequestAwareAuthenticationSuccessHandler()
        loginSuccessHandler.setTargetUrlParameter("redirectTo")
        loginSuccessHandler.setDefaultTargetUrl(adminServer.path("/"))

        // (API 요청 제한)
        // 기본적으로 모두 Open
        http.authorizeHttpRequests { authorizeHttpRequestsCustomizer ->
            // login 페이지 접근 가능
            authorizeHttpRequestsCustomizer.requestMatchers(adminServer.path("/assets/**")).permitAll()
            authorizeHttpRequestsCustomizer.requestMatchers(adminServer.path("/login")).permitAll()
            // 나머지 접근은 권한 필요
            authorizeHttpRequestsCustomizer.anyRequest().authenticated()
        }

        http.formLogin { formLoginCustomizer ->
            formLoginCustomizer.loginPage(adminServer.path("/login")).successHandler(loginSuccessHandler)
        }

        http.logout { logoutCustomizer ->
            logoutCustomizer.logoutUrl(adminServer.path("/logout"))
        }

        http.httpBasic { }
        http.csrf { csrfCustomizer ->
            csrfCustomizer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            csrfCustomizer.ignoringRequestMatchers(
                AntPathRequestMatcher(adminServer.path("/instances")),
                AntPathRequestMatcher(adminServer.path("/instances/*")),
                AntPathRequestMatcher(adminServer.path("/actuator/**"))
            )
        }

        return http.build()
    }
}