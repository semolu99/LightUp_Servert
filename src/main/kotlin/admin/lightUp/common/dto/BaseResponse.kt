package admin.lightUp.common.dto

import admin.lightUp.common.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String? = ResultCode.SUCCESS.msg,
)//시간/코드/에러마다의 코드 번호/succes or 에러 인지/메시지
