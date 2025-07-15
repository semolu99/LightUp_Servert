package admin.lightUp.common.status

import org.springframework.http.HttpStatus


enum class ROLE{
    PROTECTOR,
    MEMBER
}
enum class ResultCode(val statusCode: Int,val code: String, val message: String) {
    //001 ~ 100 서버 환경
    //101 ~ 150 member
    //401 ~ 500 토큰, 메소드 오류
    /**
     *  Server
     */
    SUCCESS(HttpStatus.OK.value(), "SUCCESS","성공"), // 200
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERR001","서버 에러가 발생했습니다"), // 500
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "ERR002","요청하신 api를 찾을 수 없습니다."), // 404
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "ERR003","항목이 올바르지 않습니다"), // 400
    MAIL_ERROR(HttpStatus.BAD_REQUEST.value(),"ERR004","발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다."),
    INVALID_JSON(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERR005","전달된 JSON 형식이 올바르지 않습니다"), // 400
    RUN_TIME_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"ERR006","런타임 에러"),
    /**
     *  Member
     */
    WRONG_FORMAT_LOGIN_ID(HttpStatus.BAD_REQUEST.value(), "ERR101","로그인 아이디를 형식에 맞게 작성해 주세요."),
    WRONG_FORMAT_EMAIL(HttpStatus.BAD_REQUEST.value(),"ERR102","이메일을 @을 포함한 이메일 형식에 맞게 작성해 주세요."),
    WRONG_FORMAT_PASSWORD(HttpStatus.BAD_REQUEST.value(),"ERR103","영문, 숫자, 특수 문자를 포함한 8~20자리로 입력 해 주세요."),
    WRONG_FORMAT_NAME(HttpStatus.BAD_REQUEST.value(),"ERR104","이름을 형식에 맞게 작성해 주세요."),
    WRONG_FORMAT_ROLE(HttpStatus.BAD_REQUEST.value(),"ERR105","role을 MEMBER 와 PROTECTOR 중 하나를 선택해 주세요"),
    WRONG_FORMAT_ORIGINAL_PASSWORD(HttpStatus.BAD_REQUEST.value(),"ERR106","기존 비밀 번호 : 영문, 숫자, 특수문자를 포함한 8~20자리로 입력 해 주세요."),
    WRONG_FORMAT_NEW_PASSWORD(HttpStatus.BAD_REQUEST.value(),"ERR107","새로운 비밀 번호 : 영문, 숫자, 특수 문자를 포함한 8~20자리로 입력 해 주세요."),
    WRONG_FORMAT_AUTH_CODE(HttpStatus.BAD_REQUEST.value(),"ERR108", "이메일 인증 코드를 입력해 주세요"),
    DUPLICATION_NEW_PASSWORD_(HttpStatus.BAD_REQUEST.value(),"ERR109","새로운 비밀 번호는 기존 비밀 번호와 달라야 합니다."),
    DUPLICATION_ID(HttpStatus.BAD_REQUEST.value(),"ERR110","중복된 아이디 입니다."),
    DUPLICATION_EMAIL(HttpStatus.BAD_REQUEST.value(),"ERR111","중복된 이메일 입니다."),
    ALREADY_SIGNUP_MEMBER(HttpStatus.BAD_REQUEST.value(),"ERR112", "이미 가입된 회원입니다."),
    LOGIN_ERROR(HttpStatus.BAD_REQUEST.value(), "ERR113","아이디 혹은 비밀 번호가 일치 하지 않습니다."), // 403
    PASSWORD_ERROR(HttpStatus.BAD_REQUEST.value(), "ERR114", "비밀 번호가 맞지 않습니다."),
    NOT_MATCH_ORIGINAL_PASSWORD(HttpStatus.BAD_REQUEST.value(),"ERR115","기존 비밀 번호가 일치 하지 않습니다."),
    NOT_MATCH_EMAIL(HttpStatus.BAD_REQUEST.value(),"ERR116", "이메일 전송 시 입력한 이메일과 다른 이메일 입니다."),
    NOT_MATCH_EMAIL_CODE(HttpStatus.BAD_REQUEST.value(), "ERR117", "발급 받은 인증 코드 만료되었거나 일치하지 않습니다"),
    NOT_FIND_ID(HttpStatus.NOT_FOUND.value(),"ERR118","존재 하지 않은 아이디 입니다."),
    NOT_SEND_EMAIL(HttpStatus.NOT_FOUND.value(),"ERR119","존재 하지 않은 아이디 입니다."),
    NOT_MEMBER(HttpStatus.NOT_FOUND.value(), "ERR120", "회원 정보를 찾을 수 없습니다."),
    NOT_MAIL_CHECKED(HttpStatus.NOT_FOUND.value(),"ERR121","메일 인증이 되어 있지 않습니다."),
    NOT_FIND_EMAIL(HttpStatus.NOT_FOUND.value(),"ERR122","회원 가입 되어 있지 않는 이메일입니다."),
    /**
     *      Token, Auth
     */
    INVALID_DATA(HttpStatus.BAD_REQUEST.value(), "ERR401","데이터 처리 오류 발생"), // 400
    TOKEN_EXPIRED(HttpStatus.FORBIDDEN.value(), "EER402","토큰이 만료 되었습니다"), // 403
    INVALID_ACCESS_TOKEN(HttpStatus.FORBIDDEN.value(), "ERR403","토큰이 유효 하지 않습니다."), // 403
    REST_TYPE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERR404","REST API TYPE 오류"), // 500
}
//에러코드 enum클래스로 만들고 코드 번호별로 분류해야됨 무슨에러인지
//ex 성공 001/id 옳은값 아님