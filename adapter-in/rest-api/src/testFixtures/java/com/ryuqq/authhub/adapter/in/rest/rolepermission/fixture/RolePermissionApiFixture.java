package com.ryuqq.authhub.adapter.in.rest.rolepermission.fixture;

import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.RevokeRolePermissionApiRequest;
import java.util.List;

/**
 * RolePermission API 테스트 픽스처
 *
 * <p>RolePermission 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermissionApiFixture {

    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final List<Long> DEFAULT_PERMISSION_IDS = List.of(1L, 2L, 3L);

    private RolePermissionApiFixture() {}

    // ========== GrantRolePermissionApiRequest ==========

    /** 기본 권한 부여 요청 */
    public static GrantRolePermissionApiRequest grantRolePermissionRequest() {
        return new GrantRolePermissionApiRequest(DEFAULT_PERMISSION_IDS);
    }

    /** 커스텀 권한 ID 목록으로 부여 요청 */
    public static GrantRolePermissionApiRequest grantRolePermissionRequest(
            List<Long> permissionIds) {
        return new GrantRolePermissionApiRequest(permissionIds);
    }

    /** 단일 권한 부여 요청 */
    public static GrantRolePermissionApiRequest grantSinglePermissionRequest(Long permissionId) {
        return new GrantRolePermissionApiRequest(List.of(permissionId));
    }

    // ========== RevokeRolePermissionApiRequest ==========

    /** 기본 권한 제거 요청 */
    public static RevokeRolePermissionApiRequest revokeRolePermissionRequest() {
        return new RevokeRolePermissionApiRequest(DEFAULT_PERMISSION_IDS);
    }

    /** 커스텀 권한 ID 목록으로 제거 요청 */
    public static RevokeRolePermissionApiRequest revokeRolePermissionRequest(
            List<Long> permissionIds) {
        return new RevokeRolePermissionApiRequest(permissionIds);
    }

    /** 단일 권한 제거 요청 */
    public static RevokeRolePermissionApiRequest revokeSinglePermissionRequest(Long permissionId) {
        return new RevokeRolePermissionApiRequest(List.of(permissionId));
    }

    // ========== Default Values ==========

    public static Long defaultRoleId() {
        return DEFAULT_ROLE_ID;
    }

    public static List<Long> defaultPermissionIds() {
        return DEFAULT_PERMISSION_IDS;
    }
}
