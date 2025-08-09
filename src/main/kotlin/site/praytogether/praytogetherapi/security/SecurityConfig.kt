package site.praytogether.praytogetherapi.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall
import site.praytogether.praytogetherapi.modules.auth.infrastructure.RefreshTokenServiceImpl
import site.praytogether.praytogetherapi.security.filter.JwtAuthFilter
import site.praytogether.praytogetherapi.security.filter.JwtLogoutFilter
import site.praytogether.praytogetherapi.security.filter.JwtValidationFilter
import site.praytogether.praytogetherapi.security.service.JwtService

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val refreshTokenService: RefreshTokenServiceImpl,
    private val jwtService: JwtService,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        return http
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/send-otp", "/api/auth/verify-otp").permitAll()
                    .requestMatchers("/health").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(JwtLogoutFilter(refreshTokenService), LogoutFilter::class.java)
            .addFilterBefore(
                JwtAuthFilter(authenticationManager, objectMapper, refreshTokenService, jwtService),
                JwtLogoutFilter::class.java
            )
            .addFilterBefore(
                JwtValidationFilter(jwtService, authenticationEntryPoint),
                JwtAuthFilter::class.java
            )
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(authenticationEntryPoint)
            }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun httpFirewall(): HttpFirewall {
        val firewall = StrictHttpFirewall()
        firewall.setAllowUrlEncodedPercent(true)
        firewall.setAllowUrlEncodedPeriod(true)
        firewall.setAllowUrlEncodedSlash(true)
        firewall.setAllowBackSlash(true)
        firewall.setAllowSemicolon(true)
        return firewall
    }
}