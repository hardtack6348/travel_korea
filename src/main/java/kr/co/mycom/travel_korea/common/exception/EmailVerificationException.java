package kr.co.mycom.travel_korea.common.exception;

/**
 * 이메일 인증을 완료하지 않은 상태로 회원가입이나 비밀번호 변경을 요청했을 때 발생합니다.
 * GlobalExceptionHandler에서 403 Forbidden으로 변환합니다.
 */
public class EmailVerificationException extends RuntimeException {

    public EmailVerificationException(String message) {
        super(message);
    }
}
