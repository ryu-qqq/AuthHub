package com.ryuqq.authhub.integration.user.fixture;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserPasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserStatusApiRequest;
import java.util.UUID;

/**
 * 사용자 통합 테스트 Fixture
 *
 * <p>API Request/Response 객체 생성 유틸리티
 *
 * @author Development Team
 * @since 1.0.0
 */
public final class UserIntegrationTestFixture {

    private static final String DEFAULT_PASSWORD = "Password123!";

    private UserIntegrationTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ========================================
    // 사용자 생성 요청
    // ========================================
    public static CreateUserApiRequest createUserRequest(String tenantId, String organizationId) {
        return createUserRequest(
                tenantId, organizationId, "testuser@example.com", DEFAULT_PASSWORD);
    }

    public static CreateUserApiRequest createUserRequest(
            String tenantId, String organizationId, String identifier, String password) {
        return new CreateUserApiRequest(tenantId, organizationId, identifier, password);
    }

    public static CreateUserApiRequest createUserRequestWithUniqueIdentifier(
            String tenantId, String organizationId) {
        String uniqueIdentifier = "user" + System.currentTimeMillis() + "@example.com";
        return new CreateUserApiRequest(
                tenantId, organizationId, uniqueIdentifier, DEFAULT_PASSWORD);
    }

    // ========================================
    // 사용자 수정 요청
    // ========================================
    public static UpdateUserApiRequest updateUserRequest() {
        return new UpdateUserApiRequest("Updated User");
    }

    // ========================================
    // 비밀번호 변경 요청
    // ========================================
    public static UpdateUserPasswordApiRequest updatePasswordRequest() {
        return updatePasswordRequest(DEFAULT_PASSWORD, "NewPassword456!");
    }

    public static UpdateUserPasswordApiRequest updatePasswordRequest(
            String currentPassword, String newPassword) {
        return new UpdateUserPasswordApiRequest(currentPassword, newPassword);
    }

    // ========================================
    // 사용자 상태 변경 요청
    // ========================================
    public static UpdateUserStatusApiRequest activateUserRequest() {
        return new UpdateUserStatusApiRequest("ACTIVE");
    }

    public static UpdateUserStatusApiRequest deactivateUserRequest() {
        return new UpdateUserStatusApiRequest("INACTIVE");
    }

    public static UpdateUserStatusApiRequest suspendUserRequest() {
        return new UpdateUserStatusApiRequest("SUSPENDED");
    }

    // ========================================
    // 역할 할당 요청
    // ========================================
    public static AssignUserRoleApiRequest assignRoleRequest(UUID roleId) {
        return new AssignUserRoleApiRequest(roleId);
    }

    public static AssignUserRoleApiRequest assignRoleRequest(String roleId) {
        return new AssignUserRoleApiRequest(UUID.fromString(roleId));
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================
    public static CreateUserApiRequest createUserRequestWithEmptyIdentifier(
            String tenantId, String organizationId) {
        return new CreateUserApiRequest(tenantId, organizationId, "", DEFAULT_PASSWORD);
    }

    public static CreateUserApiRequest createUserRequestWithShortPassword(
            String tenantId, String organizationId) {
        return new CreateUserApiRequest(tenantId, organizationId, "user@example.com", "short");
    }

    public static UpdateUserApiRequest updateUserRequestWithEmptyIdentifier() {
        return new UpdateUserApiRequest("");
    }

    public static UpdateUserPasswordApiRequest updatePasswordRequestWithShortNewPassword() {
        return new UpdateUserPasswordApiRequest(DEFAULT_PASSWORD, "short");
    }

    public static UpdateUserPasswordApiRequest updatePasswordRequestWithEmptyCurrentPassword() {
        return new UpdateUserPasswordApiRequest("", "NewPassword456!");
    }
}
