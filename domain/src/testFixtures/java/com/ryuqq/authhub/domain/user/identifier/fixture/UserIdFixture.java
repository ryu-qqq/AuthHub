package com.ryuqq.authhub.domain.user.identifier.fixture;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.UUID;

/** UserId Test Fixture Object Mother 패턴을 사용한 테스트 데이터 생성 */
public class UserIdFixture {

    private static final UUID DEFAULT_UUID =
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    /**
     * 기본 UserId 생성
     *
     * @return UserId 인스턴스
     */
    public static UserId aUserId() {
        return UserId.of(DEFAULT_UUID);
    }

    /**
     * 특정 UUID로 UserId 생성
     *
     * @param uuid UUID 값
     * @return UserId 인스턴스
     */
    public static UserId aUserId(UUID uuid) {
        return UserId.of(uuid);
    }

    /**
     * 새로운 UUID로 UserId 생성
     *
     * @return UserId 인스턴스 (랜덤 UUID)
     */
    public static UserId aNewUserId() {
        return UserId.forNew();
    }

    private UserIdFixture() {
        // Utility class
    }
}
