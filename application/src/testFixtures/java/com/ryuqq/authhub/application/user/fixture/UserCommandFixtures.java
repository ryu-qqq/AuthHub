package com.ryuqq.authhub.application.user.fixture;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;

/**
 * User Command DTO 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserCommandFixtures {

    private static final String DEFAULT_ORG_ID = UserFixture.defaultOrganizationIdString();
    private static final String DEFAULT_IDENTIFIER = UserFixture.defaultIdentifierString();
    private static final String DEFAULT_PHONE = "010-1234-5678";
    private static final String DEFAULT_PASSWORD = "password123!";

    private UserCommandFixtures() {}

    /** 기본 생성 Command 반환 */
    public static CreateUserCommand createCommand() {
        return new CreateUserCommand(
                DEFAULT_ORG_ID, DEFAULT_IDENTIFIER, DEFAULT_PHONE, DEFAULT_PASSWORD);
    }

    /** 전화번호 없이 생성 Command 반환 */
    public static CreateUserCommand createCommandWithoutPhone() {
        return new CreateUserCommand(DEFAULT_ORG_ID, DEFAULT_IDENTIFIER, null, DEFAULT_PASSWORD);
    }

    /** 지정된 값으로 생성 Command 반환 */
    public static CreateUserCommand createCommand(
            String organizationId, String identifier, String phoneNumber, String rawPassword) {
        return new CreateUserCommand(organizationId, identifier, phoneNumber, rawPassword);
    }
}
