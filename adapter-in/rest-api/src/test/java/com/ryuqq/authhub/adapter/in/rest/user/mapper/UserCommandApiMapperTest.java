package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.user.dto.request.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.request.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.request.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.fixture.UserApiFixture;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserCommandApiMapper 단위 테스트")
class UserCommandApiMapperTest {

    private UserCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateUserApiRequest) 메서드는")
    class ToCommandCreateUser {

        @Test
        @DisplayName("CreateUserApiRequest를 CreateUserCommand로 변환한다")
        void shouldConvertToCreateUserCommand() {
            // Given
            CreateUserApiRequest request = UserApiFixture.createUserRequest();

            // When
            CreateUserCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.organizationId()).isEqualTo(UserApiFixture.defaultOrganizationId());
            assertThat(result.identifier()).isEqualTo(UserApiFixture.defaultIdentifier());
            assertThat(result.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(result.rawPassword()).isEqualTo("password123!");
        }

        @Test
        @DisplayName("전화번호가 null인 요청도 처리한다")
        void shouldHandleNullPhoneNumber() {
            // Given
            CreateUserApiRequest request = UserApiFixture.createUserRequestWithoutPhone();

            // When
            CreateUserCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.organizationId()).isEqualTo(UserApiFixture.defaultOrganizationId());
            assertThat(result.identifier()).isEqualTo(UserApiFixture.defaultIdentifier());
            assertThat(result.phoneNumber()).isNull();
            assertThat(result.rawPassword()).isEqualTo("password123!");
        }
    }

    @Nested
    @DisplayName("toCommand(String, UpdateUserApiRequest) 메서드는")
    class ToCommandUpdateUser {

        @Test
        @DisplayName("UpdateUserApiRequest를 UpdateUserCommand로 변환한다")
        void shouldConvertToUpdateUserCommand() {
            // Given
            String userId = UserApiFixture.defaultUserId();
            UpdateUserApiRequest request = UserApiFixture.updateUserRequest();

            // When
            UpdateUserCommand result = mapper.toCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.phoneNumber()).isEqualTo("010-9999-8888");
        }

        @Test
        @DisplayName("전화번호가 null인 요청도 처리한다")
        void shouldHandleNullPhoneNumber() {
            // Given
            String userId = UserApiFixture.defaultUserId();
            UpdateUserApiRequest request = UserApiFixture.updateUserRequestClearPhone();

            // When
            UpdateUserCommand result = mapper.toCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.phoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("toCommand(String, ChangePasswordApiRequest) 메서드는")
    class ToCommandChangePassword {

        @Test
        @DisplayName("ChangePasswordApiRequest를 ChangePasswordCommand로 변환한다")
        void shouldConvertToChangePasswordCommand() {
            // Given
            String userId = UserApiFixture.defaultUserId();
            ChangePasswordApiRequest request = UserApiFixture.changePasswordRequest();

            // When
            ChangePasswordCommand result = mapper.toCommand(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.currentPassword()).isEqualTo("currentPassword123!");
            assertThat(result.newPassword()).isEqualTo("newPassword456!");
        }
    }
}
