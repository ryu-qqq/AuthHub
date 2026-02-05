package com.ryuqq.authhub.adapter.in.rest.permission.fixture;

import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionIdApiResponse;
import java.time.Instant;

/**
 * Permission API 테스트 픽스처
 *
 * <p>Permission 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionApiFixture {

    private static final Long DEFAULT_PERMISSION_ID = 1L;
    private static final String DEFAULT_RESOURCE = "user";
    private static final String DEFAULT_ACTION = "read";
    private static final String DEFAULT_PERMISSION_KEY = "user:read";
    private static final String DEFAULT_DESCRIPTION = "사용자 조회 권한";
    private static final String DEFAULT_TYPE = "CUSTOM";
    private static final String FIXED_TIME = "2025-01-01T00:00:00Z";

    private PermissionApiFixture() {}

    // ========== CreatePermissionApiRequest ==========

    /** 기본 생성 요청 */
    public static CreatePermissionApiRequest createPermissionRequest() {
        return new CreatePermissionApiRequest(
                null, DEFAULT_RESOURCE, DEFAULT_ACTION, DEFAULT_DESCRIPTION);
    }

    /** 커스텀 리소스와 액션으로 생성 요청 */
    public static CreatePermissionApiRequest createPermissionRequest(
            String resource, String action) {
        return new CreatePermissionApiRequest(null, resource, action, DEFAULT_DESCRIPTION);
    }

    /** 커스텀 리소스, 액션, 설명으로 생성 요청 */
    public static CreatePermissionApiRequest createPermissionRequest(
            String resource, String action, String description) {
        return new CreatePermissionApiRequest(null, resource, action, description);
    }

    // ========== UpdatePermissionApiRequest ==========

    /** 기본 수정 요청 */
    public static UpdatePermissionApiRequest updatePermissionRequest() {
        return new UpdatePermissionApiRequest("수정된 권한 설명");
    }

    /** 커스텀 설명으로 수정 요청 */
    public static UpdatePermissionApiRequest updatePermissionRequest(String description) {
        return new UpdatePermissionApiRequest(description);
    }

    // ========== PermissionIdApiResponse ==========

    /** 기본 ID 응답 */
    public static PermissionIdApiResponse permissionIdResponse() {
        return PermissionIdApiResponse.of(DEFAULT_PERMISSION_ID);
    }

    /** 커스텀 ID 응답 */
    public static PermissionIdApiResponse permissionIdResponse(Long permissionId) {
        return PermissionIdApiResponse.of(permissionId);
    }

    // ========== PermissionApiResponse ==========

    /** 기본 응답 */
    public static PermissionApiResponse permissionResponse() {
        return new PermissionApiResponse(
                DEFAULT_PERMISSION_ID,
                null,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_RESOURCE,
                DEFAULT_ACTION,
                DEFAULT_DESCRIPTION,
                DEFAULT_TYPE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 응답 */
    public static PermissionApiResponse permissionResponse(
            Long permissionId, String resource, String action) {
        return new PermissionApiResponse(
                permissionId,
                null,
                resource + ":" + action,
                resource,
                action,
                DEFAULT_DESCRIPTION,
                DEFAULT_TYPE,
                FIXED_TIME,
                FIXED_TIME);
    }

    // ========== Default Values ==========

    public static Long defaultPermissionId() {
        return DEFAULT_PERMISSION_ID;
    }

    public static String defaultResource() {
        return DEFAULT_RESOURCE;
    }

    public static String defaultAction() {
        return DEFAULT_ACTION;
    }

    public static String defaultPermissionKey() {
        return DEFAULT_PERMISSION_KEY;
    }

    public static String defaultDescription() {
        return DEFAULT_DESCRIPTION;
    }

    public static String defaultType() {
        return DEFAULT_TYPE;
    }

    public static Instant fixedTime() {
        return Instant.parse(FIXED_TIME);
    }
}
