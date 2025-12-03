package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangePasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.ChangeUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;

/**
 * UserApiMapper 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>API Request → Command 변환</li>
 *   <li>모든 필드 매핑 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("UserApiMapper 테스트")
class UserApiMapperTest {

    private UserApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserApiMapper();
    }

    @Nested
    @DisplayName("toCreateUserCommand() 테스트")
    class ToCreateUserCommandTest {

        @Test
        @DisplayName("모든 필드 변환 성공")
        void givenCreateUserApiRequest_whenToCommand_thenAllFieldsMapped() {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, 10L, "user@example.com", "password123",
                    "ADMIN", "홍길동", "010-1234-5678");

            // when
            CreateUserCommand command = mapper.toCreateUserCommand(request);

            // then
            assertThat(command.tenantId()).isEqualTo(request.tenantId());
            assertThat(command.organizationId()).isEqualTo(request.organizationId());
            assertThat(command.identifier()).isEqualTo(request.identifier());
            assertThat(command.rawPassword()).isEqualTo(request.password());
            assertThat(command.userType()).isEqualTo(request.userType());
            assertThat(command.name()).isEqualTo(request.name());
            assertThat(command.phoneNumber()).isEqualTo(request.phoneNumber());
        }

        @Test
        @DisplayName("선택 필드 null 변환")
        void givenRequestWithNullOptionalFields_whenToCommand_thenNullFieldsPreserved() {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, 10L, "user@example.com", "password123",
                    null, null, null);

            // when
            CreateUserCommand command = mapper.toCreateUserCommand(request);

            // then
            assertThat(command.userType()).isNull();
            assertThat(command.name()).isNull();
            assertThat(command.phoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("toUpdateUserCommand() 테스트")
    class ToUpdateUserCommandTest {

        @Test
        @DisplayName("모든 필드 변환 성공")
        void givenUpdateUserApiRequest_whenToCommand_thenAllFieldsMapped() {
            // given
            UUID userId = UUID.randomUUID();
            UpdateUserApiRequest request = new UpdateUserApiRequest("홍길동", "010-1234-5678");

            // when
            UpdateUserCommand command = mapper.toUpdateUserCommand(userId, request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.name()).isEqualTo(request.name());
            assertThat(command.phoneNumber()).isEqualTo(request.phoneNumber());
        }

        @Test
        @DisplayName("선택 필드 null 변환")
        void givenRequestWithNullFields_whenToCommand_thenNullFieldsPreserved() {
            // given
            UUID userId = UUID.randomUUID();
            UpdateUserApiRequest request = new UpdateUserApiRequest(null, null);

            // when
            UpdateUserCommand command = mapper.toUpdateUserCommand(userId, request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.name()).isNull();
            assertThat(command.phoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("toChangePasswordCommand() 테스트")
    class ToChangePasswordCommandTest {

        @Test
        @DisplayName("일반 비밀번호 변경 변환")
        void givenChangePasswordRequest_whenToCommand_thenAllFieldsMapped() {
            // given
            UUID userId = UUID.randomUUID();
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    "oldPassword123", "newPassword456", false);

            // when
            ChangePasswordCommand command = mapper.toChangePasswordCommand(userId, request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.currentPassword()).isEqualTo(request.currentPassword());
            assertThat(command.newPassword()).isEqualTo(request.newPassword());
            assertThat(command.verified()).isEqualTo(request.verified());
        }

        @Test
        @DisplayName("비밀번호 재설정 변환 (verified=true)")
        void givenResetPasswordRequest_whenToCommand_thenAllFieldsMapped() {
            // given
            UUID userId = UUID.randomUUID();
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    null, "newPassword456", true);

            // when
            ChangePasswordCommand command = mapper.toChangePasswordCommand(userId, request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.currentPassword()).isNull();
            assertThat(command.newPassword()).isEqualTo(request.newPassword());
            assertThat(command.verified()).isTrue();
        }
    }

    @Nested
    @DisplayName("toChangeUserStatusCommand() 테스트")
    class ToChangeUserStatusCommandTest {

        @Test
        @DisplayName("모든 필드 변환 성공")
        void givenChangeUserStatusRequest_whenToCommand_thenAllFieldsMapped() {
            // given
            UUID userId = UUID.randomUUID();
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest(
                    "INACTIVE", "휴면 계정 전환");

            // when
            ChangeUserStatusCommand command = mapper.toChangeUserStatusCommand(userId, request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.targetStatus()).isEqualTo(request.targetStatus());
            assertThat(command.reason()).isEqualTo(request.reason());
        }

        @Test
        @DisplayName("reason null 변환")
        void givenRequestWithNullReason_whenToCommand_thenNullReasonPreserved() {
            // given
            UUID userId = UUID.randomUUID();
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest("SUSPENDED", null);

            // when
            ChangeUserStatusCommand command = mapper.toChangeUserStatusCommand(userId, request);

            // then
            assertThat(command.userId()).isEqualTo(userId);
            assertThat(command.targetStatus()).isEqualTo(request.targetStatus());
            assertThat(command.reason()).isNull();
        }
    }
}
