package com.ryuqq.authhub.application.userrole.fixture;

import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import java.util.List;

/**
 * UserRole Command DTO 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRoleCommandFixtures {

    private static final String DEFAULT_USER_ID = UserRoleFixture.defaultUserIdString();
    private static final Long DEFAULT_ROLE_ID = UserRoleFixture.defaultRoleIdValue();

    private UserRoleCommandFixtures() {}

    /** 기본 Assign Command 반환 */
    public static AssignUserRoleCommand assignCommand() {
        return new AssignUserRoleCommand(DEFAULT_USER_ID, List.of(DEFAULT_ROLE_ID));
    }

    /** 지정된 값으로 Assign Command 반환 */
    public static AssignUserRoleCommand assignCommand(String userId, List<Long> roleIds) {
        return new AssignUserRoleCommand(userId, roleIds);
    }

    /** 기본 Revoke Command 반환 */
    public static RevokeUserRoleCommand revokeCommand() {
        return new RevokeUserRoleCommand(DEFAULT_USER_ID, List.of(DEFAULT_ROLE_ID));
    }

    /** 지정된 값으로 Revoke Command 반환 */
    public static RevokeUserRoleCommand revokeCommand(String userId, List<Long> roleIds) {
        return new RevokeUserRoleCommand(userId, roleIds);
    }
}
