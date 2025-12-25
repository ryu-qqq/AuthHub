package com.ryuqq.authhub.integration.permission.fixture;

import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.UpdatePermissionApiRequest;

/**
 * 권한 통합 테스트 Fixture
 *
 * <p>API Request/Response 객체 생성 유틸리티
 *
 * @author Development Team
 * @since 1.0.0
 */
public final class PermissionIntegrationTestFixture {

    private PermissionIntegrationTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ========================================
    // 권한 생성 요청
    // ========================================
    public static CreatePermissionApiRequest createPermissionRequest() {
        return createPermissionRequest("users", "read", "Read users permission");
    }

    public static CreatePermissionApiRequest createPermissionRequest(
            String resource, String action, String description) {
        return new CreatePermissionApiRequest(resource, action, description, false);
    }

    public static CreatePermissionApiRequest createPermissionRequestWithUniqueResource() {
        String uniqueResource = "resource" + System.currentTimeMillis();
        return new CreatePermissionApiRequest(uniqueResource, "read", "Test permission", false);
    }

    // ========================================
    // 시스템 권한 생성 요청
    // ========================================
    public static CreatePermissionApiRequest createSystemPermissionRequest() {
        return createSystemPermissionRequest("system", "admin", "System admin permission");
    }

    public static CreatePermissionApiRequest createSystemPermissionRequest(
            String resource, String action, String description) {
        return new CreatePermissionApiRequest(resource, action, description, true);
    }

    // ========================================
    // 권한 수정 요청
    // ========================================
    public static UpdatePermissionApiRequest updatePermissionRequest() {
        return updatePermissionRequest("Updated description");
    }

    public static UpdatePermissionApiRequest updatePermissionRequest(String description) {
        return new UpdatePermissionApiRequest(description);
    }

    // ========================================
    // CRUD 권한 세트
    // ========================================
    public static CreatePermissionApiRequest createReadPermissionRequest(String resource) {
        return new CreatePermissionApiRequest(resource, "read", "Read " + resource, false);
    }

    public static CreatePermissionApiRequest createWritePermissionRequest(String resource) {
        return new CreatePermissionApiRequest(resource, "write", "Write " + resource, false);
    }

    public static CreatePermissionApiRequest createDeletePermissionRequest(String resource) {
        return new CreatePermissionApiRequest(resource, "delete", "Delete " + resource, false);
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================
    public static CreatePermissionApiRequest createPermissionRequestWithEmptyResource() {
        return new CreatePermissionApiRequest("", "read", "Description", false);
    }

    public static CreatePermissionApiRequest createPermissionRequestWithEmptyAction() {
        return new CreatePermissionApiRequest("users", "", "Description", false);
    }
}
