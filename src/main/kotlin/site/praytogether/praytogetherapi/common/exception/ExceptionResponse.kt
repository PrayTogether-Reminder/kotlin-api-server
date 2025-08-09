package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.GlobalExceptionSpec.*
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

data class ExceptionResponse(
    val status: Int,
    val code: String,
    val message: String
) {
    companion object {
        fun of(status: Int, code: String, message: String): ExceptionResponse {
            return ExceptionResponse(status, code, message)
        }

        fun of(ex: MethodArgumentNotValidException): ExceptionResponse {
            val message = ex.bindingResult.fieldError?.defaultMessage ?: "Validation failed"
            return ExceptionResponse(
                METHOD_ARGUMENT_NOT_VALID.getStatus().value(),
                METHOD_ARGUMENT_NOT_VALID.getCode(),
                message
            )
        }

        fun of(ex: ConstraintViolationException): ExceptionResponse {
            val violation = ex.constraintViolations.firstOrNull()
            val message = violation?.message ?: "Constraint violation"
            return ExceptionResponse(
                CONSTRAINT_VIOLATE.getStatus().value(),
                CONSTRAINT_VIOLATE.getCode(),
                message
            )
        }

        fun of(ex: MethodArgumentTypeMismatchException): ExceptionResponse {
            return ExceptionResponse(
                METHOD_ARGUMENT_TYPE_MISMATCH.getStatus().value(),
                METHOD_ARGUMENT_TYPE_MISMATCH.getCode(),
                "올바른 요청이 아닙니다."
            )
        }

        fun of(ex: Exception): ExceptionResponse {
            return ExceptionResponse(
                UNKNOWN_EXCEPTION.getStatus().value(),
                UNKNOWN_EXCEPTION.getCode(),
                "알 수 없는 오류가 발생했습니다."
            )
        }
    }
}