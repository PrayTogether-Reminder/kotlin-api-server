package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

interface ExceptionSpec {
    fun getStatus(): HttpStatus
    fun getCode(): String
    fun getDebugMessage(): String
}