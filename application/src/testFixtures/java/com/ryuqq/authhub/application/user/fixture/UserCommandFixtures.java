package com.ryuqq.authhub.application.user.fixture;

import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import java.util.List;

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

    /** 기본 비밀번호 변경 Command 반환 */
    public static ChangePasswordCommand changePasswordCommand() {
        return new ChangePasswordCommand(
                UserFixture.defaultIdString(), "currentPassword", "newPassword123!");
    }

    /** 지정된 값으로 비밀번호 변경 Command 반환 */
    public static ChangePasswordCommand changePasswordCommand(
            String userId, String currentPassword, String newPassword) {
        return new ChangePasswordCommand(userId, currentPassword, newPassword);
    }

    /** 기본 사용자 수정 Command 반환 */
    public static UpdateUserCommand updateUserCommand() {
        return new UpdateUserCommand(UserFixture.defaultIdString(), "010-9999-8888");
    }

    /** 지정된 값으로 사용자 수정 Command 반환 */
    public static UpdateUserCommand updateUserCommand(String userId, String phoneNumber) {
        return new UpdateUserCommand(userId, phoneNumber);
    }

    /** 기본 사용자+역할 생성 Command 반환 */
    public static CreateUserWithRolesCommand createUserWithRolesCommand() {
        return new CreateUserWithRolesCommand(
                DEFAULT_ORG_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE,
                DEFAULT_PASSWORD,
                "SVC_DEFAULT",
                List.of("ADMIN"));
    }

    /** 지정된 값으로 사용자+역할 생성 Command 반환 */
    public static CreateUserWithRolesCommand createUserWithRolesCommand(
            String organizationId,
            String identifier,
            String phoneNumber,
            String rawPassword,
            String serviceCode,
            List<String> roleNames) {
        return new CreateUserWithRolesCommand(
                organizationId, identifier, phoneNumber, rawPassword, serviceCode, roleNames);
    }
}
