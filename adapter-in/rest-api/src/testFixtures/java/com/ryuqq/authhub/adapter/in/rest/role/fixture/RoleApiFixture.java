package com.ryuqq.authhub.adapter.in.rest.role.fixture;

import com.ryuqq.authhub.adapter.in.rest.role.dto.request.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleIdApiResponse;
import java.time.Instant;

/**
 * Role API 테스트 픽스처
 *
 * <p>Role 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleApiFixture {

    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final String DEFAULT_TENANT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Long DEFAULT_SERVICE_ID = 1L;
    private static final String DEFAULT_NAME = "USER_MANAGER";
    private static final String DEFAULT_DISPLAY_NAME = "사용자 관리자";
    private static final String DEFAULT_DESCRIPTION = "사용자 관리 권한을 가진 역할";
    private static final String DEFAULT_TYPE = "CUSTOM";
    private static final String DEFAULT_SCOPE = "TENANT_SERVICE";
    private static final String FIXED_TIME = "2025-01-01T00:00:00Z";

    private RoleApiFixture() {}

    // ========== CreateRoleApiRequest ==========

    /** 기본 생성 요청 */
    public static CreateRoleApiRequest createRoleRequest() {
        return new CreateRoleApiRequest(
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION);
    }

    /** 커스텀 이름으로 생성 요청 */
    public static CreateRoleApiRequest createRoleRequest(String name) {
        return new CreateRoleApiRequest(
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                name,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION);
    }

    /** 글로벌 역할 생성 요청 (tenantId = null, serviceId = null) */
    public static CreateRoleApiRequest createGlobalRoleRequest() {
        return new CreateRoleApiRequest(
                null, null, DEFAULT_NAME, DEFAULT_DISPLAY_NAME, DEFAULT_DESCRIPTION);
    }

    // ========== UpdateRoleApiRequest ==========

    /** 기본 수정 요청 */
    public static UpdateRoleApiRequest updateRoleRequest() {
        return new UpdateRoleApiRequest("수정된 표시 이름", "수정된 설명");
    }

    /** displayName만 수정 요청 */
    public static UpdateRoleApiRequest updateDisplayNameRequest(String displayName) {
        return new UpdateRoleApiRequest(displayName, null);
    }

    /** description만 수정 요청 */
    public static UpdateRoleApiRequest updateDescriptionRequest(String description) {
        return new UpdateRoleApiRequest(null, description);
    }

    // ========== RoleIdApiResponse ==========

    /** 기본 ID 응답 */
    public static RoleIdApiResponse roleIdResponse() {
        return RoleIdApiResponse.of(DEFAULT_ROLE_ID);
    }

    /** 커스텀 ID 응답 */
    public static RoleIdApiResponse roleIdResponse(Long roleId) {
        return RoleIdApiResponse.of(roleId);
    }

    // ========== RoleApiResponse ==========

    /** 기본 응답 */
    public static RoleApiResponse roleResponse() {
        return new RoleApiResponse(
                DEFAULT_ROLE_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_NAME,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_TYPE,
                DEFAULT_SCOPE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 응답 */
    public static RoleApiResponse roleResponse(Long roleId, String name) {
        return new RoleApiResponse(
                roleId,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                name,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_TYPE,
                DEFAULT_SCOPE,
                FIXED_TIME,
                FIXED_TIME);
    }

    // ========== Default Values ==========

    public static Long defaultRoleId() {
        return DEFAULT_ROLE_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }

    public static String defaultDisplayName() {
        return DEFAULT_DISPLAY_NAME;
    }

    public static String defaultDescription() {
        return DEFAULT_DESCRIPTION;
    }

    public static String defaultType() {
        return DEFAULT_TYPE;
    }

    public static String defaultScope() {
        return DEFAULT_SCOPE;
    }

    public static Instant fixedTime() {
        return Instant.parse(FIXED_TIME);
    }
}
