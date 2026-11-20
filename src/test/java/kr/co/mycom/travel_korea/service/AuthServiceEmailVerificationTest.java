package kr.co.mycom.travel_korea.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import kr.co.mycom.travel_korea.common.exception.DuplicateMemberException;
import kr.co.mycom.travel_korea.common.exception.EmailVerificationException;
import kr.co.mycom.travel_korea.config.JwtConfig;
import kr.co.mycom.travel_korea.config.SecurityConfig;
import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import kr.co.mycom.travel_korea.request.MailRequest;
import kr.co.mycom.travel_korea.request.UserRequest;
import kr.co.mycom.travel_korea.service.AuthService.VerificationPurpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceEmailVerificationTest {

    private static final String EMAIL = "user@example.com";

    private UserRepository repo;
    private MockHttpSession session;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        when(repo.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SecurityConfig security = mock(SecurityConfig.class);
        when(security.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());

        JavaMailSender mailSender = mock(JavaMailSender.class);
        when(mailSender.createMimeMessage()).thenAnswer(invocation -> new MimeMessage((Session) null));

        session = new MockHttpSession();
        authService = new AuthService(mock(JwtConfig.class), security, repo, mailSender, session);
        ReflectionTestUtils.setField(authService, "authCodeExpiration", 180_000L);
    }

    private Integer sentCode() {
        return (Integer) session.getAttribute("verificationCode");
    }

    private HttpStatus confirm(String email, Integer code) {
        return (HttpStatus) authService.emailVerificationConfirm(new MailRequest(email, code)).getStatusCode();
    }

    private UserRequest userRequest(String email) {
        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword("new-password");
        request.setNickname("tester");
        return request;
    }

    private UserEntity existingUser() {
        UserEntity user = new UserEntity();
        user.setEmail(EMAIL);
        user.setPassword("old-hash");
        return user;
    }

    @Test
    void 인증번호는_6자리다() {
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);

        int code = sentCode();
        assertEquals(true, code >= 100000 && code <= 999999);
    }

    @Test
    void 인증번호를_요청하지_않고_확인하면_400을_반환한다() {
        assertEquals(HttpStatus.BAD_REQUEST, confirm(EMAIL, 123456));
    }

    @Test
    void 인증하지_않으면_회원가입할_수_없다() {
        assertThrows(EmailVerificationException.class, () -> authService.signup(userRequest(EMAIL)));
        verify(repo, never()).save(any());
    }

    @Test
    void 인증하면_회원가입할_수_있고_인증은_한_번만_쓸_수_있다() {
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);
        assertEquals(HttpStatus.OK, confirm(EMAIL, sentCode()));

        authService.signup(userRequest(EMAIL));
        verify(repo).save(any(UserEntity.class));

        // 같은 인증으로 다시 가입할 수 없다.
        assertThrows(EmailVerificationException.class, () -> authService.signup(userRequest(EMAIL)));
    }

    @Test
    void 인증하지_않으면_비밀번호를_변경할_수_없다() {
        assertThrows(EmailVerificationException.class, () -> authService.changePassword(userRequest(EMAIL)));
        verify(repo, never()).findByEmail(anyString());
    }

    @Test
    void 비밀번호_재설정_인증을_마치면_비밀번호를_변경할_수_있다() {
        UserEntity user = existingUser();
        when(repo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        authService.sendCodeToEmail(EMAIL, VerificationPurpose.PASSWORD_RESET);
        assertEquals(HttpStatus.OK, confirm(EMAIL, sentCode()));

        authService.changePassword(userRequest(EMAIL));
        assertEquals(true, new BCryptPasswordEncoder().matches("new-password", user.getPassword()));
    }

    @Test
    void 회원가입용_인증으로는_비밀번호를_변경할_수_없다() {
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);
        assertEquals(HttpStatus.OK, confirm(EMAIL, sentCode()));

        assertThrows(EmailVerificationException.class, () -> authService.changePassword(userRequest(EMAIL)));
    }

    @Test
    void 다른_이메일로_인증받은_상태로는_가입할_수_없다() {
        authService.sendCodeToEmail("other@example.com", VerificationPurpose.SIGNUP);
        assertEquals(HttpStatus.OK, confirm("other@example.com", sentCode()));

        assertThrows(EmailVerificationException.class, () -> authService.signup(userRequest(EMAIL)));
    }

    @Test
    void 인증번호를_5번_틀리면_차단되고_맞는_번호도_거부한다() {
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);
        Integer code = sentCode();
        Integer wrongCode = code == 999999 ? 100000 : code + 1;

        for (int i = 0; i < AuthService.MAX_ATTEMPTS; i++) {
            assertEquals(HttpStatus.UNAUTHORIZED, confirm(EMAIL, wrongCode));
        }

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, confirm(EMAIL, code));
        // 차단 후에는 인증 정보가 지워져 다시 요청해야 한다.
        assertEquals(HttpStatus.BAD_REQUEST, confirm(EMAIL, code));
    }

    @Test
    void 만료된_인증번호는_거부한다() {
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);
        session.setAttribute("verificationExpiresAt", System.currentTimeMillis() - 1);

        assertEquals(HttpStatus.UNAUTHORIZED, confirm(EMAIL, sentCode()));
    }

    @Test
    void 이미_가입된_이메일이면_가입할_수_없다() {
        when(repo.existsByEmail(EMAIL)).thenReturn(true);
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);
        assertEquals(HttpStatus.OK, confirm(EMAIL, sentCode()));

        assertThrows(DuplicateMemberException.class, () -> authService.signup(userRequest(EMAIL)));
        verify(repo, never()).save(any());
    }

    @Test
    void 닉네임이_중복이면_가입할_수_없고_인증은_유지되어_다시_시도할_수_있다() {
        when(repo.existsByNickname("tester")).thenReturn(true);
        authService.sendCodeToEmail(EMAIL, VerificationPurpose.SIGNUP);
        assertEquals(HttpStatus.OK, confirm(EMAIL, sentCode()));

        assertThrows(DuplicateMemberException.class, () -> authService.signup(userRequest(EMAIL)));

        // 닉네임을 바꾸면 같은 인증으로 가입할 수 있다.
        UserRequest retry = userRequest(EMAIL);
        retry.setNickname("another");
        authService.signup(retry);
        verify(repo).save(any(UserEntity.class));
    }
}
