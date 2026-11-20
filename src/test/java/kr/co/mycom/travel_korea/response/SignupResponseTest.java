package kr.co.mycom.travel_korea.response;

import kr.co.mycom.travel_korea.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignupResponseTest {

    @Test
    void 회원가입_응답에는_화면에_필요한_값만_담긴다() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setNickname("tester");
        user.setPassword("$2a$10$hashed-password");
        user.setGrade("user");
        user.setCreated_at(LocalDateTime.now());

        SignupResponse response = SignupResponse.from(user);

        assertEquals(1L, response.memberId());
        assertEquals("user@example.com", response.email());
        assertEquals("tester", response.nickname());
    }

    @Test
    void 회원가입_응답에는_비밀번호와_권한_필드가_없다() {
        List<String> fields = Arrays.stream(SignupResponse.class.getRecordComponents())
                .map(RecordComponent::getName)
                .toList();

        assertEquals(List.of("memberId", "email", "nickname"), fields);
    }
}
