package kr.co.mycom.travel_korea.response;

import kr.co.mycom.travel_korea.entity.UserEntity;

/**
 * 회원가입 응답.
 * UserEntity를 그대로 반환하면 비밀번호 해시와 권한 등 내부 정보가 응답에 담기므로,
 * 화면에 필요한 값만 골라 반환합니다.
 */
public record SignupResponse(Long memberId, String email, String nickname) {

    public static SignupResponse from(UserEntity user) {
        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
