package com.ryuqq.authhub.adapter.in.rest.userrole.fixture;

import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.RevokeUserRoleApiRequest;
import java.util.List;

/**
 * UserRole API 테스트 픽스처
 *
 * <p>UserRole 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRoleApiFixture {

    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789abc";
    private static final List<Long> DEFAULT_ROLE_IDS = List.of(1L, 2L);

    private UserRoleApiFixture() {}

    // ========== AssignUserRoleApiRequest ==========

    /** 기본 역할 할당 요청 */
    public static AssignUserRoleApiRequest assignUserRoleRequest() {
        return new AssignUserRoleApiRequest(DEFAULT_ROLE_IDS);
    }

    /** 커스텀 역할 ID 목록으로 할당 요청 */
    public static AssignUserRoleApiRequest assignUserRoleRequest(List<Long> roleIds) {
        return new AssignUserRoleApiRequest(roleIds);
    }

    /** 단일 역할 할당 요청 */
    public static AssignUserRoleApiRequest assignSingleRoleRequest(Long roleId) {
        return new AssignUserRoleApiRequest(List.of(roleId));
    }

    // ========== RevokeUserRoleApiRequest ==========

    /** 기본 역할 철회 요청 */
    public static RevokeUserRoleApiRequest revokeUserRoleRequest() {
        return new RevokeUserRoleApiRequest(DEFAULT_ROLE_IDS);
    }

    /** 커스텀 역할 ID 목록으로 철회 요청 */
    public static RevokeUserRoleApiRequest revokeUserRoleRequest(List<Long> roleIds) {
        return new RevokeUserRoleApiRequest(roleIds);
    }

    /** 단일 역할 철회 요청 */
    public static RevokeUserRoleApiRequest revokeSingleRoleRequest(Long roleId) {
        return new RevokeUserRoleApiRequest(List.of(roleId));
    }

    // ========== Default Values ==========

    public static String defaultUserId() {
        return DEFAULT_USER_ID;
    }

    public static List<Long> defaultRoleIds() {
        return DEFAULT_ROLE_IDS;
    }
}
