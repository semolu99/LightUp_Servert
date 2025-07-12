package admin.lightUp.common.dto

import admin.lightUp.common.status.ResultCode

data class BaseResponse<T>(
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String? = ResultCode.SUCCESS.msg,
)
