package admin.lightUp.common.status

enum class ResultCode(val msg: String) {
    SUCCESS("정상 처리 되었습니다."),
    ERROR("에러가 발생 했습니다.")
}
enum class ROLE{
    PROTECTOR,
    MEMBER
}
//에러코드 enum클래스로 만들고 코드 번호별로 분류해야됨 무슨에러인지
//ex 성공 001/id 옳은값 아님