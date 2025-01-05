package com.raillylinker.configurations

import de.codecentric.boot.admin.server.config.AdminServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

// [서비스 보안 시큐리티 설정]
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val adminServer: AdminServerProperties
) {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
            .securityMatcher(
                NegatedServerWebExchangeMatcher(
                    ServerWebExchangeMatchers.pathMatchers("/instances")
                )
            )
            .securityMatcher(
                NegatedServerWebExchangeMatcher(
                    ServerWebExchangeMatchers.pathMatchers("/actuator/**")
                )
            )

        http.authorizeExchange {
            it.pathMatchers(this.adminServer.contextPath + "/login").permitAll()
            it.pathMatchers(this.adminServer.contextPath + "/assets/**").permitAll()
            it.anyExchange().authenticated()
        }

        http.formLogin {
            it.loginPage(this.adminServer.contextPath + "/login")
        }

        http.logout {
            it.logoutUrl(this.adminServer.contextPath + "/logout")
        }

        http.httpBasic { }

        http.csrf { it.disable() }

        return http.build()
    }
}