package com.ryuqq.authhub.domain.user.identifier.fixture;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.UUID;

/**
 * UserIdFixture - UserId 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserIdFixture {

    private UserIdFixture() {}

    public static UserId aUserId() {
        return UserId.forNew();
    }

    public static UserId aUserId(UUID uuid) {
        return UserId.of(uuid);
    }
}
