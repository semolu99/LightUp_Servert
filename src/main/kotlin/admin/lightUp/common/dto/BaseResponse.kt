package admin.lightUp.common.dto

import admin.lightUp.common.status.ResultCode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BaseResponse<T>(
    val statusCode: Int = ResultCode.SUCCESS.statusCode,
    val statusMessage: String? = ResultCode.SUCCESS.message,
    val responseTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    val data: T? = null,
    val code: String = ResultCode.SUCCESS.code
)//시간/코드/에러마다의 코드 번호/succes or 에러 인지/메시지
