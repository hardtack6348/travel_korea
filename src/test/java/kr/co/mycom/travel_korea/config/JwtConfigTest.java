package kr.co.mycom.travel_korea.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {

    // HS256은 32바이트 이상의 키가 필요하다.
    private static final String SECRET = "test-secret-key-for-jwt-config-test-0123456789";
    private static final long ACCESS_EXPIRATION = 60_000L;      // 1분
    private static final long REFRESH_EXPIRATION = 600_000L;    // 10분
    private static final String EMAIL = "user@example.com";

    @Test
    void 유효한_Refresh_Token이면_이메일을_반환한다() throws Exception {
        JwtConfig jwt = new JwtConfig(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
        String refreshToken = jwt.createTokenPair(EMAIL).refreshToken();

        assertEquals(EMAIL, jwt.validateRefreshToken(refreshToken));
    }

    @Test
    void 만료된_Refresh_Token이면_예외가_발생한다() throws Exception {
        // 만료 시간을 음수로 주어 발급 즉시 만료된 토큰을 만든다.
        JwtConfig expiredJwt = new JwtConfig(SECRET, ACCESS_EXPIRATION, -1_000L);
        String expiredRefreshToken = expiredJwt.createTokenPair(EMAIL).refreshToken();

        assertThrows(IllegalArgumentException.class,
                () -> expiredJwt.validateRefreshToken(expiredRefreshToken));
    }

    @Test
    void Access_Token으로는_재발급할_수_없다() throws Exception {
        JwtConfig jwt = new JwtConfig(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
        String accessToken = jwt.createAccessToken(EMAIL);

        assertThrows(IllegalArgumentException.class,
                () -> jwt.validateRefreshToken(accessToken));
    }

    @Test
    void 다른_키로_서명된_토큰이면_예외가_발생한다() throws Exception {
        JwtConfig otherJwt = new JwtConfig(SECRET + "-other", ACCESS_EXPIRATION, REFRESH_EXPIRATION);
        String forgedToken = otherJwt.createTokenPair(EMAIL).refreshToken();

        JwtConfig jwt = new JwtConfig(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
        assertThrows(IllegalArgumentException.class,
                () -> jwt.validateRefreshToken(forgedToken));
    }
}
