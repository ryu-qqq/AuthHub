package com.ryuqq.authhub.application.rolepermission.fixture;

import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import java.util.List;

/**
 * RolePermission Command DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Command DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermissionCommandFixtures {

    private static final Long DEFAULT_ROLE_ID = RolePermissionFixture.defaultRoleIdValue();
    private static final Long DEFAULT_PERMISSION_ID =
            RolePermissionFixture.defaultPermissionIdValue();

    private RolePermissionCommandFixtures() {}

    /** 기본 Grant Command 반환 */
    public static GrantRolePermissionCommand grantCommand() {
        return new GrantRolePermissionCommand(DEFAULT_ROLE_ID, List.of(DEFAULT_PERMISSION_ID));
    }

    /** 지정된 역할 ID와 권한 ID 목록으로 Grant Command 반환 */
    public static GrantRolePermissionCommand grantCommand(Long roleId, List<Long> permissionIds) {
        return new GrantRolePermissionCommand(roleId, permissionIds);
    }

    /** 기본 Revoke Command 반환 */
    public static RevokeRolePermissionCommand revokeCommand() {
        return new RevokeRolePermissionCommand(DEFAULT_ROLE_ID, List.of(DEFAULT_PERMISSION_ID));
    }

    /** 지정된 역할 ID와 권한 ID 목록으로 Revoke Command 반환 */
    public static RevokeRolePermissionCommand revokeCommand(Long roleId, List<Long> permissionIds) {
        return new RevokeRolePermissionCommand(roleId, permissionIds);
    }
}
