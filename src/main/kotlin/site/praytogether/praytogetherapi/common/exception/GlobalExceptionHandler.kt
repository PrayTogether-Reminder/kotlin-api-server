package site.praytogether.praytogetherapi.common.exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import site.praytogether.praytogetherapi.common.exception.spec.ExceptionSpec
import site.praytogether.praytogetherapi.common.exception.spec.GlobalExceptionSpec.*

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // API BaseException
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ExceptionResponse> {
        logger.error(e.getLogMessage())
        val exceptionSpec = e.getExceptionSpec()
        return ResponseEntity.status(exceptionSpec.getStatus())
            .body(
                ExceptionResponse.of(
                    exceptionSpec.getStatus().value(),
                    exceptionSpec.getCode(),
                    e.getClientMessage()
                )
            )
    }

    // 요청 데이터 유효성 검사 실패 (@Valid)
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logMethodArgumentNotValid(ex)
        return ResponseEntity.badRequest().body(ExceptionResponse.of(ex))
    }

    private fun logMethodArgumentNotValid(ex: MethodArgumentNotValidException) {
        val fields = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "")
        }

        val fieldEntries = fields.entries.joinToString(", ") { "${it.key}=${it.value}" }
        val fieldsString = if (fieldEntries.isNotEmpty()) "[ $fieldEntries ]" else "[]"
        val message = "[ERROR] 유효성 검사 실패 : $fieldsString"
        logger.error(message)
    }

    // 요청 데이터 제약 조건 위반 (@Valid)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ExceptionResponse> {
        logConstraintViolationException(e)
        return ResponseEntity.badRequest().body(ExceptionResponse.of(e))
    }

    private fun logConstraintViolationException(e: ConstraintViolationException) {
        val details = e.constraintViolations.map { violation ->
            val propertyPath = violation.propertyPath.toString()
            val message = violation.message
            val invalidValue = violation.invalidValue
            "$message : $propertyPath = $invalidValue"
        }

        val detailMessage = details.joinToString(", ")
        val fieldsString = if (detailMessage.isNotEmpty()) "[ $detailMessage ]" else "[]"
        logger.error("[ERROR] 제약 조건 위반 : $fieldsString")
    }

    // 요청 데이터 타입 변환 실패
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: WebRequest
    ): ResponseEntity<Any> {
        logMethodArgumentTypeMismatch(ex)
        val response = ExceptionResponse.of(ex)
        return ResponseEntity.badRequest().body(response)
    }

    private fun logMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException) {
        val name = ex.name
        val type = ex.requiredType?.simpleName ?: "unknown"
        val value = ex.value
        val message = "$name = $value -> $type Type"
        logger.error("[ERROR] 타입 변환 실패 : $message")
    }

    // Before Controller
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val cause = ex.cause

        when (cause) {
            is InvalidFormatException -> logInvalidFormat(cause)
            is UnrecognizedPropertyException -> logUnrecognizedProperty(cause)
            is JsonParseException -> logJsonParse(cause)
            is MismatchedInputException -> logMismatchedInput(cause)
            else -> logger.error("[ERROR] 알 수 없는 HTTP 메시지 읽기 오류: ${ex.message}")
        }

        return ResponseEntity.badRequest()
            .body(
                ExceptionResponse.of(
                    METHOD_ARGUMENT_NOT_VALID.getStatus().value(),
                    METHOD_ARGUMENT_NOT_VALID.getCode(),
                    "올바르지 않은 요청입니다."
                )
            )
    }

    private fun logInvalidFormat(ex: InvalidFormatException) {
        val fieldName = getFieldNameFromPath(ex.path)
        val invalidValue = ex.value
        val targetType = ex.targetType?.simpleName ?: "unknown"
        val message = "'$fieldName = $invalidValue' 은(는) '$targetType' 타입으로 변환할 수 없습니다."
        logger.error("[ERROR] 타입 변환 실패: $message")
    }

    private fun logUnrecognizedProperty(ex: UnrecognizedPropertyException) {
        val propertyName = ex.propertyName
        val className = ex.referringClass.simpleName
        val detailMessage = "'$propertyName' 은(는) '$className' 클래스에 존재하지 않습니다"
        logger.error("[ERROR] 알 수 없는 필드: $detailMessage")
    }

    private fun logJsonParse(ex: JsonParseException) {
        val detailMessage = "JSON 파싱 오류: ${ex.originalMessage}"
        logger.error("[ERROR] JSON 구문 오류: $detailMessage (위치: ${ex.location})")
    }

    private fun logMismatchedInput(ex: MismatchedInputException) {
        val fieldName = getFieldNameFromPath(ex.path)
        val targetType = ex.targetType?.simpleName ?: "unknown"
        val detailMessage = "'$fieldName' 이(가) '$targetType'과(와) 일치하지 않습니다"
        logger.error("[ERROR] 입력 타입 불일치: $detailMessage")
    }

    private fun getFieldNameFromPath(path: List<JsonMappingException.Reference>?): String {
        if (path.isNullOrEmpty()) {
            return "unknown"
        }

        return path.joinToString(".") { reference ->
            reference.fieldName ?: "[${reference.index}]"
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleAllException(ex: Exception): ResponseEntity<ExceptionResponse> {
        logger.error("[ERROR] 정의되지 않은 예외 발생: ${ex.message}")
        ex.printStackTrace()
        return ResponseEntity.badRequest().body(ExceptionResponse.of(ex))
    }
}