package site.praytogether.praytogetherapi.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import site.praytogether.praytogetherapi.common.resolver.PrincipalIdArgumentResolver

@Configuration
class WebMvcConfig(
    private val principalIdArgumentResolver: PrincipalIdArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(principalIdArgumentResolver)
    }
}