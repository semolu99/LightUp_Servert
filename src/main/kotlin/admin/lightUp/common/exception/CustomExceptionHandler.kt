package admin.lightUp.common.exception

import admin.lightUp.common.dto.BaseResponse
import admin.lightUp.common.status.ResultCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.context.properties.bind.BindException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class CustomExceptionHandler {


    /**
     * DTO유효성 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<BaseResponse<String>> {
        val errors = ex.bindingResult.fieldErrors
        val errorMessages = getErrorCode(errors.firstOrNull()!!).message
        val combinedErrorCode = getCombinedErrorCode(errors)

        return ResponseEntity(BaseResponse(statusCode = ResultCode.BAD_REQUEST.statusCode, statusMessage = errorMessages, code = combinedErrorCode), HttpStatus.BAD_REQUEST)
    }

    /**
     * 매개 변수가 유효 하지 않을 떄
     */
    @ExceptionHandler(IllegalArgumentException::class)
    protected fun illegalArgumentException(ex: IllegalArgumentException): ResponseEntity<BaseResponse<String>> {
        return ResponseEntity(BaseResponse(statusCode = ResultCode.BAD_REQUEST.statusCode, statusMessage = ex.message, code = ResultCode.BAD_REQUEST.code), HttpStatus.BAD_REQUEST)
    }

    /**
     * 기본 에러 처리
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ResponseEntity<BaseResponse<String>> {
        val resultCode = when (ex) {
            is BadCredentialsException -> ResultCode.LOGIN_ERROR
            is BindException -> ResultCode.INVALID_DATA
            is HttpMessageNotReadableException -> ResultCode.INVALID_JSON
            is SignatureException, is SecurityException, is MalformedJwtException -> ResultCode.INVALID_ACCESS_TOKEN
            is ExpiredJwtException -> ResultCode.TOKEN_EXPIRED
            is NoHandlerFoundException -> ResultCode.NOT_FOUND
            is RuntimeException -> ResultCode.RUN_TIME_ERROR
            else -> ResultCode.INTERNAL_SERVER_ERROR
        }

        return ResponseEntity(BaseResponse(resultCode.statusCode, resultCode.message, code = resultCode.code), HttpStatusCode.valueOf(resultCode.statusCode))
    }

    /**
     * jwt 서명이 유효하지 않을 때
     */
    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(ex: Exception): ResponseEntity<BaseResponse<String>>{
        return ResponseEntity(BaseResponse(statusCode = ResultCode.INVALID_ACCESS_TOKEN.statusCode, statusMessage = ResultCode.INVALID_ACCESS_TOKEN.message, code = ResultCode.INVALID_ACCESS_TOKEN.code), HttpStatus.BAD_REQUEST)
    }

    /**
     * jwt 형식이 유효 하지 않을 때
     */
    @ExceptionHandler(MalformedJwtException::class)
    fun handleMalformedJwtException(ex: Exception): ResponseEntity<BaseResponse<String>>{
        return ResponseEntity(BaseResponse(statusCode = ResultCode.INVALID_ACCESS_TOKEN.statusCode, statusMessage = ResultCode.INVALID_ACCESS_TOKEN.message, code = ResultCode.INVALID_ACCESS_TOKEN.code), HttpStatus.BAD_REQUEST)
    }

    /**
     * jwt 토큰 유효 기간이 만료 됬을 때
     */
    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: Exception): ResponseEntity<BaseResponse<String>>{
        return ResponseEntity(BaseResponse(statusCode = ResultCode.TOKEN_EXPIRED.statusCode, statusMessage = ResultCode.TOKEN_EXPIRED.message, code = ResultCode.TOKEN_EXPIRED.code), HttpStatus.BAD_REQUEST)
    }

    /**
     * HTTP 메서드가 알맞지 않을 때
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    protected fun httpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException, req: HttpServletRequest): ResponseEntity<BaseResponse<String>> {
        return ResponseEntity(BaseResponse(statusCode = ResultCode.REST_TYPE_ERROR.statusCode, statusMessage = "Does not support request method '" + req.method + "'", code = ResultCode.REST_TYPE_ERROR.code), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * 커스텀 예외 처리
     */
    @ExceptionHandler(InvalidInputException::class)
    protected fun apiCustomException(ex: InvalidInputException): ResponseEntity<BaseResponse<String>> {
        return ResponseEntity(BaseResponse(statusCode = ex.statusCode, statusMessage = ex.statusMessage, code = ex.code), HttpStatus.BAD_REQUEST)
    }

    /**
     * 필드 에러 종류 반환
     */
    @ExceptionHandler(BadCredentialsException::class)
    protected fun badCredentialsException(ex: BadCredentialsException):
            ResponseEntity<BaseResponse<Map<String, String>>> {
        val resultCode = ResultCode.LOGIN_ERROR
        return ResponseEntity(
            BaseResponse(
                statusCode = resultCode.statusCode,
                statusMessage = resultCode.message,
                code = resultCode.code
            ), HttpStatus.BAD_REQUEST
        )
    }
    /**
     * 필드 에러의 첫번째 오류 코드 반환
     */
    private fun getErrorCode(fieldError: FieldError): ResultCode {

        return when (fieldError.code) {

            "NotBlank", "Pattern", "ValidEnum", "Max", "Min","Email" -> when (fieldError.field) {//회원 가입 및 로그인 시 입력 에러
                "_loginId" -> ResultCode.WRONG_FORMAT_LOGIN_ID
                "_password" -> ResultCode.WRONG_FORMAT_PASSWORD
                "_email"->ResultCode.WRONG_FORMAT_EMAIL
                "_name" -> ResultCode.WRONG_FORMAT_NAME
                "_role" -> ResultCode.WRONG_FORMAT_ROLE
                "_currentPassword" -> ResultCode.WRONG_FORMAT_ORIGINAL_PASSWORD
                "_newPassword" -> ResultCode.WRONG_FORMAT_NEW_PASSWORD
                else -> ResultCode.BAD_REQUEST // 기본 에러 코드
            }
            else -> ResultCode.BAD_REQUEST // 기본 에러 코드
        }
    }
    //첫번쨰 에러 코드 반환
    private fun getCombinedErrorCode(errors: List<FieldError>): String {
        return errors.firstOrNull()?.let { fieldError ->
            getErrorCode(fieldError).code
        } ?: ResultCode.BAD_REQUEST.code
    }
}