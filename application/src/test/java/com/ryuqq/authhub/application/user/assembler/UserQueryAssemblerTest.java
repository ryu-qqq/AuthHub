package com.ryuqq.authhub.application.user.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import com.ryuqq.authhub.domain.user.vo.fixture.UserProfileFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UserQueryAssembler 단위 테스트
 *
 * <p>QueryAssembler 규칙:
 *
 * <ul>
 *   <li>Domain → Response 변환 담당
 *   <li>민감 정보 필터링 (credential 등)
 *   <li>null-safe 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("UserQueryAssembler 테스트")
class UserQueryAssemblerTest {

    private UserQueryAssembler userQueryAssembler;

    @BeforeEach
    void setUp() {
        userQueryAssembler = new UserQueryAssembler();
    }

    @Nested
    @DisplayName("toResponse() - Domain -> Response 변환")
    class ToResponse {

        @Test
        @DisplayName("User Domain을 UserResponse로 변환해야 한다")
        void shouldConvertUserToResponse() {
            // Given
            User user = UserFixture.aUser();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(user.userIdValue());
            assertThat(response.tenantId()).isEqualTo(user.tenantIdValue());
            assertThat(response.organizationId()).isEqualTo(user.organizationIdValue());
            assertThat(response.userType()).isEqualTo(user.userTypeValue());
            assertThat(response.status()).isEqualTo(user.statusValue());
            assertThat(response.createdAt()).isEqualTo(user.createdAt());
            assertThat(response.updatedAt()).isEqualTo(user.updatedAt());
        }

        @Test
        @DisplayName("UserProfile 정보가 정확히 변환되어야 한다")
        void shouldConvertProfileCorrectly() {
            // Given
            UserProfile profile = UserProfileFixture.aUserProfile();
            User user = UserFixture.builder().asExisting().profile(profile).build();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.name()).isEqualTo(profile.name());
            assertThat(response.phoneNumber()).isEqualTo(profile.phoneNumberValue());
        }

        @Test
        @DisplayName("organizationId가 null인 경우도 처리해야 한다")
        void shouldHandleNullOrganizationId() {
            // Given
            User user = UserFixture.aPublicUserWithoutOrganization();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.organizationId()).isNull();
        }

        @Test
        @DisplayName("빈 프로필인 경우 null 값이 반환되어야 한다")
        void shouldHandleEmptyProfile() {
            // Given
            User user = UserFixture.builder().asExisting().profile(UserProfile.empty()).build();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.name()).isNull();
            assertThat(response.phoneNumber()).isNull();
        }

        @Test
        @DisplayName("null User 입력 시 NullPointerException이 발생해야 한다")
        void shouldThrowExceptionWhenUserIsNull() {
            // When & Then
            assertThatThrownBy(() -> userQueryAssembler.toResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toCreateResponse() - Domain -> CreateResponse 변환")
    class ToCreateResponse {

        @Test
        @DisplayName("User Domain을 CreateUserResponse로 변환해야 한다")
        void shouldConvertUserToCreateResponse() {
            // Given
            User user = UserFixture.aUser();

            // When
            CreateUserResponse response = userQueryAssembler.toCreateResponse(user);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(user.userIdValue());
            assertThat(response.createdAt()).isEqualTo(user.createdAt());
        }

        @Test
        @DisplayName("null User 입력 시 NullPointerException이 발생해야 한다")
        void shouldThrowExceptionWhenUserIsNull() {
            // When & Then
            assertThatThrownBy(() -> userQueryAssembler.toCreateResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("다양한 UserStatus 변환")
    class StatusConversion {

        @Test
        @DisplayName("ACTIVE 상태가 정확히 변환되어야 한다")
        void shouldConvertActiveStatus() {
            // Given
            User user = UserFixture.aUserWithStatus(UserStatus.ACTIVE);

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상태가 정확히 변환되어야 한다")
        void shouldConvertInactiveStatus() {
            // Given
            User user = UserFixture.aUserWithStatus(UserStatus.INACTIVE);

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("SUSPENDED 상태가 정확히 변환되어야 한다")
        void shouldConvertSuspendedStatus() {
            // Given
            User user = UserFixture.aUserWithStatus(UserStatus.SUSPENDED);

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.status()).isEqualTo("SUSPENDED");
        }
    }

    @Nested
    @DisplayName("다양한 UserType 변환")
    class TypeConversion {

        @Test
        @DisplayName("PUBLIC 타입이 정확히 변환되어야 한다")
        void shouldConvertPublicType() {
            // Given
            User user = UserFixture.builder().asExisting().userType(UserType.PUBLIC).build();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.userType()).isEqualTo("PUBLIC");
        }

        @Test
        @DisplayName("INTERNAL 타입이 정확히 변환되어야 한다")
        void shouldConvertInternalType() {
            // Given
            User user = UserFixture.anInternalUser();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(response.userType()).isEqualTo("INTERNAL");
        }
    }

    @Nested
    @DisplayName("보안 규칙 검증")
    class SecurityRules {

        @Test
        @DisplayName("UserResponse에 credential 정보가 포함되지 않아야 한다")
        void shouldNotIncludeCredential() {
            // Given
            User user = UserFixture.aUser();

            // When
            UserResponse response = userQueryAssembler.toResponse(user);

            // Then
            assertThat(UserResponse.class.getRecordComponents())
                    .noneMatch(rc -> rc.getName().toLowerCase().contains("password"))
                    .noneMatch(rc -> rc.getName().toLowerCase().contains("credential"));
        }
    }
}
