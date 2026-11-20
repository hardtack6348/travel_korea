package kr.co.mycom.travel_korea.config;

import kr.co.mycom.travel_korea.entity.UserEntity;
import kr.co.mycom.travel_korea.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 인증 실패(토큰 없음·잘못됨·만료)는 401, 인증은 됐지만 권한이 없으면 403을 반환하는지 확인합니다.
 * 프론트엔드는 401을 받으면 /refresh로 토큰을 재발급받고, 403은 재발급하지 않습니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "local-mock"})
class AuthErrorStatusTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserRepository userRepository;

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path, String accessToken) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET();
        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String tokenForNewUser(String email) throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setNickname(email.substring(0, email.indexOf('@')));
        user.setPassword("$2a$10$test-hash");
        user.setGrade("user");
        user.setCreated_at(LocalDateTime.now());
        userRepository.save(user);
        return jwtConfig.createAccessToken(email);
    }

    @Test
    void 토큰_없이_회원_전용_API를_호출하면_401() throws Exception {
        HttpResponse<String> response = get("/api/v1/tour-bookmarks", null);

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("message"));
    }

    @Test
    void 잘못된_토큰으로_호출하면_401() throws Exception {
        HttpResponse<String> response = get("/api/v1/tour-bookmarks", "not-a-valid-token");

        assertEquals(401, response.statusCode());
    }

    @Test
    void 일반_회원이_관리자_API를_호출하면_403() throws Exception {
        String token = tokenForNewUser("member403@example.com");

        HttpResponse<String> response = get("/api/v1/admin/notices", token);

        assertEquals(403, response.statusCode());
        assertTrue(response.body().contains("message"));
    }

    @Test
    void 공개_API는_토큰_없이_호출할_수_있다() throws Exception {
        HttpResponse<String> response = get("/api/v1/regions", null);

        assertEquals(200, response.statusCode());
    }
}
