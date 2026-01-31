package com.ryuqq.authhub.adapter.in.rest.user.fixture;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserIdApiResponse;
import java.time.Instant;

/**
 * User API 테스트 픽스처
 *
 * <p>User 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserApiFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789abc";
    private static final String DEFAULT_ORGANIZATION_ID = "01941234-5678-7000-8000-organization1";
    private static final String DEFAULT_IDENTIFIER = "testuser@example.com";
    private static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";
    private static final String DEFAULT_PASSWORD = "password123!";

    private UserApiFixture() {}

    // ========== CreateUserApiRequest ==========

    /** 기본 사용자 생성 요청 */
    public static CreateUserApiRequest createUserRequest() {
        return new CreateUserApiRequest(
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_PASSWORD);
    }

    /** 전화번호 없는 사용자 생성 요청 */
    public static CreateUserApiRequest createUserRequestWithoutPhone() {
        return new CreateUserApiRequest(
                DEFAULT_ORGANIZATION_ID, DEFAULT_IDENTIFIER, null, DEFAULT_PASSWORD);
    }

    /** 커스텀 식별자로 사용자 생성 요청 */
    public static CreateUserApiRequest createUserRequestWithIdentifier(String identifier) {
        return new CreateUserApiRequest(
                DEFAULT_ORGANIZATION_ID, identifier, DEFAULT_PHONE_NUMBER, DEFAULT_PASSWORD);
    }

    /** 커스텀 조직으로 사용자 생성 요청 */
    public static CreateUserApiRequest createUserRequestWithOrganization(String organizationId) {
        return new CreateUserApiRequest(
                organizationId, DEFAULT_IDENTIFIER, DEFAULT_PHONE_NUMBER, DEFAULT_PASSWORD);
    }

    // ========== UpdateUserApiRequest ==========

    /** 기본 사용자 수정 요청 */
    public static UpdateUserApiRequest updateUserRequest() {
        return new UpdateUserApiRequest("010-9999-8888");
    }

    /** 전화번호 제거 요청 */
    public static UpdateUserApiRequest updateUserRequestClearPhone() {
        return new UpdateUserApiRequest(null);
    }

    // ========== ChangePasswordApiRequest ==========

    /** 기본 비밀번호 변경 요청 */
    public static ChangePasswordApiRequest changePasswordRequest() {
        return new ChangePasswordApiRequest("currentPassword123!", "newPassword456!");
    }

    /** 커스텀 비밀번호로 변경 요청 */
    public static ChangePasswordApiRequest changePasswordRequest(
            String currentPassword, String newPassword) {
        return new ChangePasswordApiRequest(currentPassword, newPassword);
    }

    // ========== UserIdApiResponse ==========

    /** 기본 사용자 ID 응답 */
    public static UserIdApiResponse userIdResponse() {
        return UserIdApiResponse.of(DEFAULT_USER_ID);
    }

    /** 커스텀 ID로 응답 */
    public static UserIdApiResponse userIdResponse(String userId) {
        return UserIdApiResponse.of(userId);
    }

    // ========== UserApiResponse ==========

    /** 기본 사용자 조회 응답 */
    public static UserApiResponse userResponse() {
        return new UserApiResponse(
                DEFAULT_USER_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                "ACTIVE",
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 사용자 조회 응답 */
    public static UserApiResponse userResponse(String userId, String identifier, String status) {
        return new UserApiResponse(
                userId,
                DEFAULT_ORGANIZATION_ID,
                identifier,
                DEFAULT_PHONE_NUMBER,
                status,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성 사용자 응답 */
    public static UserApiResponse inactiveUserResponse() {
        return new UserApiResponse(
                DEFAULT_USER_ID,
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_IDENTIFIER,
                DEFAULT_PHONE_NUMBER,
                "INACTIVE",
                FIXED_TIME,
                FIXED_TIME);
    }

    // ========== Default Values ==========

    public static String defaultUserId() {
        return DEFAULT_USER_ID;
    }

    public static String defaultOrganizationId() {
        return DEFAULT_ORGANIZATION_ID;
    }

    public static String defaultIdentifier() {
        return DEFAULT_IDENTIFIER;
    }

    public static Instant fixedTime() {
        return FIXED_TIME;
    }
}
