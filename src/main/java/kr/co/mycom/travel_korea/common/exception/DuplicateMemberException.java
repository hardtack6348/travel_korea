package kr.co.mycom.travel_korea.common.exception;

/**
 * 이미 가입된 이메일이나 사용 중인 닉네임으로 회원가입을 요청했을 때 발생합니다.
 * GlobalExceptionHandler에서 409 Conflict로 변환합니다.
 */
public class DuplicateMemberException extends RuntimeException {

    public DuplicateMemberException(String message) {
        super(message);
    }
}
