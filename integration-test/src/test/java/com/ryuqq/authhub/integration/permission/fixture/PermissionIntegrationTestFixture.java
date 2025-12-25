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

    /**
     * 기본 권한 생성 요청 생성
     *
     * <p>resource: "users", action: "read"로 설정된 기본 권한 요청을 생성합니다.
     *
     * @return 기본 권한 생성 요청
     */
    public static CreatePermissionApiRequest createPermissionRequest() {
        return createPermissionRequest("users", "read", "Read users permission");
    }

    /**
     * 커스텀 권한 생성 요청 생성
     *
     * @param resource 권한 대상 리소스 (예: "users", "orders")
     * @param action 허용 액션 (예: "read", "write", "delete")
     * @param description 권한 설명
     * @return 커스텀 권한 생성 요청
     */
    public static CreatePermissionApiRequest createPermissionRequest(
            String resource, String action, String description) {
        return new CreatePermissionApiRequest(resource, action, description, false);
    }

    /**
     * 유니크 리소스명을 가진 권한 생성 요청 생성
     *
     * <p>테스트 간 충돌 방지를 위해 타임스탬프 기반 유니크 리소스명을 사용합니다.
     *
     * @return 유니크 리소스를 가진 권한 생성 요청
     */
    public static CreatePermissionApiRequest createPermissionRequestWithUniqueResource() {
        String uniqueResource = "resource" + System.currentTimeMillis();
        return new CreatePermissionApiRequest(uniqueResource, "read", "Test permission", false);
    }

    // ========================================
    // 시스템 권한 생성 요청
    // ========================================

    /**
     * 기본 시스템 권한 생성 요청 생성
     *
     * <p>isSystem=true로 설정된 시스템 레벨 권한 요청을 생성합니다.
     *
     * @return 기본 시스템 권한 생성 요청
     */
    public static CreatePermissionApiRequest createSystemPermissionRequest() {
        return createSystemPermissionRequest("system", "admin", "System admin permission");
    }

    /**
     * 커스텀 시스템 권한 생성 요청 생성
     *
     * @param resource 권한 대상 리소스
     * @param action 허용 액션
     * @param description 권한 설명
     * @return 시스템 권한 생성 요청 (isSystem=true)
     */
    public static CreatePermissionApiRequest createSystemPermissionRequest(
            String resource, String action, String description) {
        return new CreatePermissionApiRequest(resource, action, description, true);
    }

    // ========================================
    // 권한 수정 요청
    // ========================================

    /**
     * 기본 권한 수정 요청 생성
     *
     * @return "Updated description"으로 설정된 수정 요청
     */
    public static UpdatePermissionApiRequest updatePermissionRequest() {
        return updatePermissionRequest("Updated description");
    }

    /**
     * 커스텀 권한 수정 요청 생성
     *
     * @param description 새로운 권한 설명
     * @return 권한 수정 요청
     */
    public static UpdatePermissionApiRequest updatePermissionRequest(String description) {
        return new UpdatePermissionApiRequest(description);
    }

    // ========================================
    // CRUD 권한 세트
    // ========================================

    /**
     * Read 권한 생성 요청 생성
     *
     * @param resource 권한 대상 리소스
     * @return action="read"인 권한 생성 요청
     */
    public static CreatePermissionApiRequest createReadPermissionRequest(String resource) {
        return new CreatePermissionApiRequest(resource, "read", "Read " + resource, false);
    }

    /**
     * Write 권한 생성 요청 생성
     *
     * @param resource 권한 대상 리소스
     * @return action="write"인 권한 생성 요청
     */
    public static CreatePermissionApiRequest createWritePermissionRequest(String resource) {
        return new CreatePermissionApiRequest(resource, "write", "Write " + resource, false);
    }

    /**
     * Delete 권한 생성 요청 생성
     *
     * @param resource 권한 대상 리소스
     * @return action="delete"인 권한 생성 요청
     */
    public static CreatePermissionApiRequest createDeletePermissionRequest(String resource) {
        return new CreatePermissionApiRequest(resource, "delete", "Delete " + resource, false);
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================

    /**
     * 빈 리소스를 가진 권한 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return resource가 빈 문자열인 권한 생성 요청
     */
    public static CreatePermissionApiRequest createPermissionRequestWithEmptyResource() {
        return new CreatePermissionApiRequest("", "read", "Description", false);
    }

    /**
     * 빈 액션을 가진 권한 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return action이 빈 문자열인 권한 생성 요청
     */
    public static CreatePermissionApiRequest createPermissionRequestWithEmptyAction() {
        return new CreatePermissionApiRequest("users", "", "Description", false);
    }
}
