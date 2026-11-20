package kr.co.mycom.travel_korea.service;

import com.nimbusds.jose.JOSEException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import kr.co.mycom.travel_korea.config.JwtConfig;
import kr.co.mycom.travel_korea.config.SecurityConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import kr.co.mycom.travel_korea.request.MailRequest;
import kr.co.mycom.travel_korea.request.UserRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.mycom.travel_korea.common.exception.DuplicateMemberException;
import kr.co.mycom.travel_korea.common.exception.EmailVerificationException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtConfig jwt;
    private final SecurityConfig security;
    private final UserRepository repo;
    private final JavaMailSender emailSender;
    private final HttpSession session;
    private static final String EMAIL_KEY = "verificationEmail";
    private static final String CODE_KEY = "verificationCode";
    private static final String EXPIRES_AT_KEY = "verificationExpiresAt";
    private static final String PURPOSE_KEY = "verificationPurpose";
    private static final String ATTEMPTS_KEY = "verificationAttempts";
    private static final String VERIFIED_UNTIL_KEY = "verificationVerifiedUntil";

    // 인증번호 입력 최대 시도 횟수
    static final int MAX_ATTEMPTS = 5;
    // 인증 성공 후 회원가입/비밀번호 변경을 완료해야 하는 시간 (10분)
    static final long VERIFIED_VALID_MILLIS = 10 * 60 * 1000L;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${spring.mail.auth-code-expiration-millis}")
    private Long authCodeExpiration;

    // 이메일 인증의 용도. 회원가입용 인증으로 비밀번호를 바꾸는 등 다른 용도로 쓰는 것을 막는다.
    public enum VerificationPurpose { SIGNUP, PASSWORD_RESET }

    public UserEntity signup(UserRequest userInput) {
        requireVerified(userInput.getEmail(), VerificationPurpose.SIGNUP);

        // 화면의 중복 확인을 거치지 않은 요청도 막기 위해 서버에서 다시 검사한다.
        // 중복이면 인증 상태는 지우지 않아, 닉네임만 바꿔 다시 가입할 수 있다.
        if (repo.existsByEmail(userInput.getEmail())) {
            throw new DuplicateMemberException("이미 가입된 이메일입니다.");
        }
        if (repo.existsByNickname(userInput.getNickname())) {
            throw new DuplicateMemberException("이미 사용 중인 닉네임입니다.");
        }

        UserEntity rep = new UserEntity();
        rep.setCreated_at(LocalDateTime.now());
        rep.setEmail(userInput.getEmail());
        rep.setPassword(security.passwordEncoder().encode(userInput.getPassword()));
        rep.setNickname(userInput.getNickname());
        rep.setGrade("user");
        UserEntity saved = repo.save(rep);

        // 인증은 한 번만 사용할 수 있다.
        clearVerification(session);
        return saved;
    }

    public ResponseEntity login(@RequestBody UserRequest request) throws JOSEException {
        UserEntity dbUser = repo.findByEmail(request.getEmail()).orElse(null);
        if (dbUser ==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
        if (security.passwordEncoder().matches(request.getPassword(), dbUser.getPassword())) {
            JwtConfig.TokenResponse tokens = jwt.createTokenPair(dbUser.getEmail());
            ResponseCookie refreshCookie = jwt.createRefreshTokenCookie(tokens.refreshToken());

            /*
             * 헤더에는 닉네임이 필요하지만 비밀번호가 포함된 UserEntity 전체를
             * 반환하면 안 되므로 화면에 필요한 안전한 회원 정보만 전달합니다.
             */
            Map<String, Object> member = Map.of(
                    "memberId", dbUser.getId(),
                    "email", dbUser.getEmail(),
                    "nickname", dbUser.getNickname(),
                    "role", dbUser.getGrade()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(Map.of(
                            "accessToken", tokens.accessToken(),
                            "member", member
                    ));

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    public ResponseCookie logout() {
//        엑세스 토큰 삭제
        return jwt.deleteToken();
    }

    public ResponseEntity<?> refreshToken(String refreshTokenCookie) {
        // Access Token 재발급
        // Refresh Token이 없거나, 위조되었거나, 만료되었거나, 회원이 없으면 재발급하지 않고 401을 반환한다.
        // 프론트엔드는 401을 받으면 다시 로그인하도록 안내한다.
        if (refreshTokenCookie == null || refreshTokenCookie.isBlank()) {
            return unauthorized("Refresh Token이 존재하지 않습니다. 다시 로그인하세요.");
        }

        String email;
        try {
            // 서명, 타입, 만료 검증 (만료 시 예외 발생)
            email = jwt.validateRefreshToken(refreshTokenCookie);
        } catch (Exception e) {
            return unauthorized("유효하지 않거나 만료된 Refresh Token입니다. 다시 로그인하세요.");
        }

        // 토큰이 유효해도 탈퇴 등으로 회원이 없으면 재발급하지 않는다.
        if (repo.findByEmail(email).isEmpty()) {
            return unauthorized("회원 정보를 찾을 수 없습니다. 다시 로그인하세요.");
        }

        try {
            String newAccessToken = jwt.createAccessToken(email);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (JOSEException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "토큰 발급 중 오류가 발생했습니다."));
        }
    }

    // 재발급 실패 시 Refresh Token 쿠키를 삭제하고 401을 반환한다.
    private ResponseEntity<Map<String, String>> unauthorized(String message) {
        ResponseCookie deleteCookie = jwt.deleteToken();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(Map.of("message", message));
    }

    private void createEmailForm(String toEmail,
                                        String title,
                                        String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(text,true);
        emailSender.send(message);
    }



    public ResponseEntity<Map<String, Object>> emailVerificationConfirm(MailRequest request) {
        // 이메일 인증번호 확인
        String savedEmail = (String) session.getAttribute(EMAIL_KEY);
        Integer savedCode = (Integer) session.getAttribute(CODE_KEY);
        Long expiresAt = (Long) session.getAttribute(EXPIRES_AT_KEY);

        // 인증번호를 요청하지 않았거나 이미 인증을 마친 경우
        if (savedEmail == null || savedCode == null || expiresAt == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "인증번호를 먼저 요청해 주세요."));
        }

        if (System.currentTimeMillis() > expiresAt) {
            clearVerification(session);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "인증번호가 만료되었습니다. 다시 요청해 주세요."));
        }

        Integer savedAttempts = (Integer) session.getAttribute(ATTEMPTS_KEY);
        int attempts = savedAttempts == null ? 0 : savedAttempts;
        if (attempts >= MAX_ATTEMPTS) {
            clearVerification(session);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "입력 횟수를 초과했습니다. 인증번호를 다시 요청해 주세요."));
        }

        if (!savedEmail.equals(request.getEmail()) || !savedCode.equals(request.getAuthCode())) {
            attempts++;
            session.setAttribute(ATTEMPTS_KEY, attempts);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "인증번호가 올바르지 않습니다.",
                            "remainingAttempts", MAX_ATTEMPTS - attempts
                    ));
        }

        // 인증 성공: 인증번호는 지우고, 인증 완료 상태를 10분간 유지한다.
        session.removeAttribute(CODE_KEY);
        session.removeAttribute(EXPIRES_AT_KEY);
        session.removeAttribute(ATTEMPTS_KEY);
        session.setAttribute(VERIFIED_UNTIL_KEY, System.currentTimeMillis() + VERIFIED_VALID_MILLIS);
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    public void changePassword(UserRequest request) {
        requireVerified(request.getEmail(), VerificationPurpose.PASSWORD_RESET);

        UserEntity user = repo.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다."));
        user.setPassword(security.passwordEncoder().encode(request.getPassword()));
        repo.save(user);

        // 인증은 한 번만 사용할 수 있다.
        clearVerification(session);
    }

    // 해당 이메일이 해당 용도로 인증을 마쳤는지 확인한다. 아니면 403을 반환하도록 예외를 던진다.
    private void requireVerified(String email, VerificationPurpose purpose) {
        String verifiedEmail = (String) session.getAttribute(EMAIL_KEY);
        Object verifiedPurpose = session.getAttribute(PURPOSE_KEY);
        Long verifiedUntil = (Long) session.getAttribute(VERIFIED_UNTIL_KEY);

        boolean verified = email != null
                && email.equals(verifiedEmail)
                && purpose == verifiedPurpose
                && verifiedUntil != null
                && System.currentTimeMillis() <= verifiedUntil;

        if (!verified) {
            throw new EmailVerificationException("이메일 인증을 먼저 완료해 주세요.");
        }
    }

    public void sendCodeToEmail(String email, VerificationPurpose purpose) {
        // 확인코드 담긴 이메일 발송
        String title = "Waylog 이메일 인증 번호";
        int checkNum = SECURE_RANDOM.nextInt(900000) + 100000; // 100000 ~ 999999
        String content =
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head><meta charset='UTF-8'></head>"
                        + "<body style='margin: 0; padding: 0; background-color: #f4f6f8; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif;'>"
                        + "  <table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='background-color: #f4f6f8; padding: 40px 0;'>"
                        + "    <tr>"
                        + "      <td align='center'>"
                        + "        <table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='max-width: 500px; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden;'>"
                        // 브랜드 헤더
                        + "          <tr>"
                        + "            <td style='background-color: #2563eb; padding: 24px; text-align: center;'>"
                        + "              <h1 style='color: #ffffff; margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.5px;'>Waylog</h1>"
                        + "            </td>"
                        + "          </tr>"
                        // 본문 콘텐츠
                        + "          <tr>"
                        + "            <td style='padding: 40px 32px; text-align: center;'>"
                        + "              <h2 style='color: #1e293b; margin: 0 0 12px 0; font-size: 20px; font-weight: 600;'>이메일 인증 번호</h2>"
                        + "              <p style='color: #64748b; margin: 0 0 28px 0; font-size: 14px; line-height: 1.5;'>Waylog를 이용해 주셔서 감사합니다.<br>아래의 인증 번호를 홈페이지 인증 창에 입력해 주세요.</p>"
                        // 인증코드 강조 박스
                        + "              <div style='background-color: #f1f5f9; border-radius: 8px; padding: 16px 24px; display: inline-block; margin-bottom: 24px;'>"
                        + "                <span style='color: #2563eb; font-size: 32px; font-weight: 800; letter-spacing: 6px; font-family: monospace;'>" + checkNum + "</span>"
                        + "              </div>"
                        + "              <p style='color: #ef4444; margin: 0; font-size: 13px; font-weight: 500;'>※ 본 인증번호는 3분 동안만 유효합니다.</p>"
                        + "            </td>"
                        + "          </tr>"
                        // 푸터
                        + "          <tr>"
                        + "            <td style='background-color: #f8fafc; padding: 20px 32px; border-top: 1px solid #e2e8f0; text-align: center;'>"
                        + "              <p style='color: #94a3b8; font-size: 12px; margin: 0; line-height: 1.5;'>본 메일은 발신 전용 메일이므로 회신되지 않습니다.<br>© Waylog. All rights reserved.</p>"
                        + "            </td>"
                        + "          </tr>"
                        + "        </table>"
                        + "      </td>"
                        + "    </tr>"
                        + "  </table>"
                        + "</body>"
                        + "</html>";
        try {
            createEmailForm(email, title, content);
            // 인증번호를 다시 발급하면 이전 인증 정보와 인증 완료 상태를 모두 해제
            clearVerification(session);
            // 인증번호 관련 정보를 세션에 저장
            session.setAttribute(EMAIL_KEY, email);
            session.setAttribute(CODE_KEY, checkNum);
            session.setAttribute(PURPOSE_KEY, purpose);
            session.setAttribute(ATTEMPTS_KEY, 0);
            session.setAttribute(
                    EXPIRES_AT_KEY,
                    System.currentTimeMillis() + authCodeExpiration
            );
        } catch (Exception e) {
            e.printStackTrace();
            // 또는 로거를 사용하여 상세한 예외 정보 로깅
            throw new RuntimeException("Unable to send email in sendCodeToEmail", e); // 원인 예외를 포함시키기
        }
    }
    public void clearVerification(
            HttpSession session
    ) {
        session.removeAttribute(EMAIL_KEY);
        session.removeAttribute(CODE_KEY);
        session.removeAttribute(EXPIRES_AT_KEY);
        session.removeAttribute(PURPOSE_KEY);
        session.removeAttribute(ATTEMPTS_KEY);
        session.removeAttribute(VERIFIED_UNTIL_KEY);
    }

}

