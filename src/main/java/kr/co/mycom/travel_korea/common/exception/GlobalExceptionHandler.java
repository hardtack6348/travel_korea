package kr.co.mycom.travel_korea.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * API에서 발생하는 입력값 오류를 공통 JSON 응답으로 반환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스 검증 로직에서 발생한 잘못된 요청을 400으로 반환합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
                Map.of("message", exception.getMessage())
        );
    }

    /**
     * 이메일 인증 없이 회원가입이나 비밀번호 변경을 요청하면 403으로 반환합니다.
     */
    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<Map<String, String>> handleEmailVerification(EmailVerificationException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("message", exception.getMessage())
        );
    }

    /**
     * 이미 가입된 이메일이나 사용 중인 닉네임으로 회원가입하면 409로 반환합니다.
     */
    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateMember(DuplicateMemberException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", exception.getMessage())
        );
    }
}
