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

    /**
     * 기본 사용자 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @param organizationId 조직 ID (UUID 문자열)
     * @return 기본 이메일과 비밀번호가 설정된 사용자 생성 요청
     */
    public static CreateUserApiRequest createUserRequest(String tenantId, String organizationId) {
        return createUserRequest(
                tenantId, organizationId, "testuser@example.com", DEFAULT_PASSWORD);
    }

    /**
     * 커스텀 사용자 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @param organizationId 조직 ID (UUID 문자열)
     * @param identifier 사용자 식별자 (이메일)
     * @param password 비밀번호
     * @return 사용자 생성 요청
     */
    public static CreateUserApiRequest createUserRequest(
            String tenantId, String organizationId, String identifier, String password) {
        return new CreateUserApiRequest(tenantId, organizationId, identifier, password);
    }

    /**
     * 유니크 식별자를 가진 사용자 생성 요청 생성
     *
     * <p>테스트 간 충돌 방지를 위해 타임스탬프 기반 유니크 이메일을 사용합니다.
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @param organizationId 조직 ID (UUID 문자열)
     * @return 유니크 이메일을 가진 사용자 생성 요청
     */
    public static CreateUserApiRequest createUserRequestWithUniqueIdentifier(
            String tenantId, String organizationId) {
        String uniqueIdentifier = "user" + System.currentTimeMillis() + "@example.com";
        return new CreateUserApiRequest(
                tenantId, organizationId, uniqueIdentifier, DEFAULT_PASSWORD);
    }

    // ========================================
    // 사용자 수정 요청
    // ========================================

    /**
     * 기본 사용자 수정 요청 생성
     *
     * @return "Updated User"로 설정된 수정 요청
     */
    public static UpdateUserApiRequest updateUserRequest() {
        return new UpdateUserApiRequest("Updated User");
    }

    // ========================================
    // 비밀번호 변경 요청
    // ========================================

    /**
     * 기본 비밀번호 변경 요청 생성
     *
     * @return 기본 비밀번호에서 새 비밀번호로 변경하는 요청
     */
    public static UpdateUserPasswordApiRequest updatePasswordRequest() {
        return updatePasswordRequest(DEFAULT_PASSWORD, "NewPassword456!");
    }

    /**
     * 커스텀 비밀번호 변경 요청 생성
     *
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 비밀번호 변경 요청
     */
    public static UpdateUserPasswordApiRequest updatePasswordRequest(
            String currentPassword, String newPassword) {
        return new UpdateUserPasswordApiRequest(currentPassword, newPassword);
    }

    // ========================================
    // 사용자 상태 변경 요청
    // ========================================

    /**
     * 사용자 활성화 요청 생성
     *
     * @return status="ACTIVE"인 상태 변경 요청
     */
    public static UpdateUserStatusApiRequest activateUserRequest() {
        return new UpdateUserStatusApiRequest("ACTIVE");
    }

    /**
     * 사용자 비활성화 요청 생성
     *
     * @return status="INACTIVE"인 상태 변경 요청
     */
    public static UpdateUserStatusApiRequest deactivateUserRequest() {
        return new UpdateUserStatusApiRequest("INACTIVE");
    }

    /**
     * 사용자 정지 요청 생성
     *
     * @return status="SUSPENDED"인 상태 변경 요청
     */
    public static UpdateUserStatusApiRequest suspendUserRequest() {
        return new UpdateUserStatusApiRequest("SUSPENDED");
    }

    // ========================================
    // 역할 할당 요청
    // ========================================

    /**
     * 역할 할당 요청 생성 (UUID)
     *
     * @param roleId 할당할 역할 ID
     * @return 역할 할당 요청
     */
    public static AssignUserRoleApiRequest assignRoleRequest(UUID roleId) {
        return new AssignUserRoleApiRequest(roleId);
    }

    /**
     * 역할 할당 요청 생성 (문자열)
     *
     * @param roleId 할당할 역할 ID (UUID 문자열)
     * @return 역할 할당 요청
     */
    public static AssignUserRoleApiRequest assignRoleRequest(String roleId) {
        return new AssignUserRoleApiRequest(UUID.fromString(roleId));
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================

    /**
     * 빈 식별자를 가진 사용자 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @return identifier가 빈 문자열인 사용자 생성 요청
     */
    public static CreateUserApiRequest createUserRequestWithEmptyIdentifier(
            String tenantId, String organizationId) {
        return new CreateUserApiRequest(tenantId, organizationId, "", DEFAULT_PASSWORD);
    }

    /**
     * 짧은 비밀번호를 가진 사용자 생성 요청 (검증 실패용)
     *
     * <p>비밀번호 길이 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @return 짧은 비밀번호를 가진 사용자 생성 요청
     */
    public static CreateUserApiRequest createUserRequestWithShortPassword(
            String tenantId, String organizationId) {
        return new CreateUserApiRequest(tenantId, organizationId, "user@example.com", "short");
    }

    /**
     * 빈 식별자를 가진 사용자 수정 요청 (검증 실패용)
     *
     * @return identifier가 빈 문자열인 사용자 수정 요청
     */
    public static UpdateUserApiRequest updateUserRequestWithEmptyIdentifier() {
        return new UpdateUserApiRequest("");
    }

    /**
     * 짧은 새 비밀번호를 가진 비밀번호 변경 요청 (검증 실패용)
     *
     * @return 짧은 새 비밀번호를 가진 비밀번호 변경 요청
     */
    public static UpdateUserPasswordApiRequest updatePasswordRequestWithShortNewPassword() {
        return new UpdateUserPasswordApiRequest(DEFAULT_PASSWORD, "short");
    }

    /**
     * 빈 현재 비밀번호를 가진 비밀번호 변경 요청 (검증 실패용)
     *
     * @return 현재 비밀번호가 빈 문자열인 비밀번호 변경 요청
     */
    public static UpdateUserPasswordApiRequest updatePasswordRequestWithEmptyCurrentPassword() {
        return new UpdateUserPasswordApiRequest("", "NewPassword456!");
    }
}
