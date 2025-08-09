package site.praytogether.praytogetherapi.common.resolver

import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import site.praytogether.praytogetherapi.common.annotation.PrincipalId
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal

@Component
class PrincipalIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(PrincipalId::class.java) &&
                parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication?.principal is PrayTogetherPrincipal) {
            (authentication.principal as PrayTogetherPrincipal).id
        } else {
            null
        }
    }
}