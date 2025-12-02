package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * UpdateUserUseCase 단위 테스트
 *
 * <p>Kent Beck TDD - Red Phase: 실패하는 테스트 먼저 작성
 *
 * <p>UseCase 테스트 규칙:
 * <ul>
 *   <li>Port 모킹으로 외부 의존성 격리</li>
 *   <li>성공 시나리오와 실패 시나리오 모두 테스트</li>
 *   <li>Domain 로직이 아닌 UseCase 흐름만 테스트</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase 테스트")
class UpdateUserUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private UserAssembler userAssembler;

    @Mock
    private Clock clock;

    private UpdateUserUseCase updateUserUseCase;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-15T10:00:00Z");
    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        updateUserUseCase = new UpdateUserUseCaseImpl(
                userQueryPort,
                userPersistencePort,
                userAssembler,
                clock
        );
    }

    @Nested
    @DisplayName("execute() - 사용자 정보 수정")
    class Execute {

        @Test
        @DisplayName("유효한 Command로 사용자 프로필을 성공적으로 수정해야 한다")
        void shouldUpdateUserProfileSuccessfully() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User existingUser = UserFixture.aUser(UserId.of(USER_ID));
            User updatedUser = UserFixture.builder()
                    .asExisting()
                    .userId(UserId.of(USER_ID))
                    .profile(com.ryuqq.authhub.domain.user.vo.UserProfile.of(
                            command.name(),
                            command.nickname(),
                            command.profileImageUrl()
                    ))
                    .build();

            UserResponse expectedResponse = createExpectedResponse(command);

            given(userQueryPort.findById(any(UserId.class)))
                    .willReturn(Optional.of(existingUser));
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(USER_ID));
            given(userAssembler.toResponse(any(User.class)))
                    .willReturn(expectedResponse);

            // When
            UserResponse response = updateUserUseCase.execute(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(USER_ID);
            assertThat(response.name()).isEqualTo(command.name());
            assertThat(response.nickname()).isEqualTo(command.nickname());
            assertThat(response.profileImageUrl()).isEqualTo(command.profileImageUrl());

            // Verify interactions
            verify(userQueryPort).findById(any(UserId.class));
            verify(userPersistencePort).persist(any(User.class));
            verify(userAssembler).toResponse(any(User.class));
        }

        @Test
        @DisplayName("프로필 정보만 변경되어야 한다 (다른 필드는 유지)")
        void shouldOnlyUpdateProfileFields() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User existingUser = UserFixture.aUser(UserId.of(USER_ID));
            UserResponse expectedResponse = createExpectedResponse(command);

            given(userQueryPort.findById(any(UserId.class)))
                    .willReturn(Optional.of(existingUser));
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(USER_ID));
            given(userAssembler.toResponse(any(User.class)))
                    .willReturn(expectedResponse);

            // When
            updateUserUseCase.execute(command);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userPersistencePort).persist(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            // 프로필 필드 변경 확인
            assertThat(capturedUser.getProfile().name()).isEqualTo(command.name());
            assertThat(capturedUser.getProfile().nickname()).isEqualTo(command.nickname());
            assertThat(capturedUser.getProfile().profileImageUrl()).isEqualTo(command.profileImageUrl());

            // 다른 필드 유지 확인
            assertThat(capturedUser.tenantIdValue()).isEqualTo(existingUser.tenantIdValue());
            assertThat(capturedUser.getUserType()).isEqualTo(existingUser.getUserType());
            assertThat(capturedUser.getUserStatus()).isEqualTo(existingUser.getUserStatus());
        }

        @Test
        @DisplayName("일부 필드만 업데이트해도 동작해야 한다")
        void shouldUpdatePartialFields() {
            // Given
            UpdateUserCommand command = new UpdateUserCommand(
                    USER_ID,
                    "새이름",    // name만 변경
                    null,       // nickname 유지
                    null        // profileImageUrl 유지
            );
            User existingUser = UserFixture.aUser(UserId.of(USER_ID));
            UserResponse expectedResponse = new UserResponse(
                    USER_ID,
                    existingUser.tenantIdValue(),
                    existingUser.organizationIdValue(),
                    existingUser.userTypeValue(),
                    existingUser.statusValue(),
                    "새이름",
                    existingUser.getProfile().nickname(),
                    existingUser.getProfile().profileImageUrl(),
                    existingUser.createdAt(),
                    FIXED_TIME
            );

            given(userQueryPort.findById(any(UserId.class)))
                    .willReturn(Optional.of(existingUser));
            given(clock.now()).willReturn(FIXED_TIME);
            given(userPersistencePort.persist(any(User.class)))
                    .willReturn(UserId.of(USER_ID));
            given(userAssembler.toResponse(any(User.class)))
                    .willReturn(expectedResponse);

            // When
            UserResponse response = updateUserUseCase.execute(command);

            // Then
            assertThat(response.name()).isEqualTo("새이름");
        }
    }

    @Nested
    @DisplayName("검증 실패 시나리오")
    class ValidationFailure {

        @Test
        @DisplayName("존재하지 않는 User 수정 시 예외가 발생해야 한다")
        void shouldThrowWhenUserNotFound() {
            // Given
            UpdateUserCommand command = createValidCommand();
            given(userQueryPort.findById(any(UserId.class)))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User");

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("삭제된 User 수정 시 예외가 발생해야 한다")
        void shouldThrowWhenUserDeleted() {
            // Given
            UpdateUserCommand command = createValidCommand();
            User deletedUser = UserFixture.aUserWithStatus(UserStatus.DELETED);

            given(userQueryPort.findById(any(UserId.class)))
                    .willReturn(Optional.of(deletedUser));

            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("삭제");

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("null Command로 수정 시 예외가 발생해야 한다")
        void shouldThrowWhenCommandIsNull() {
            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(null))
                    .isInstanceOf(NullPointerException.class);

            verify(userPersistencePort, never()).persist(any(User.class));
        }

        @Test
        @DisplayName("null userId로 수정 시 예외가 발생해야 한다")
        void shouldThrowWhenUserIdIsNull() {
            // Given
            UpdateUserCommand command = new UpdateUserCommand(
                    null,       // userId가 null
                    "이름",
                    "닉네임",
                    null
            );

            // When & Then
            assertThatThrownBy(() -> updateUserUseCase.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(userPersistencePort, never()).persist(any(User.class));
        }
    }

    // ========== Helper Methods ==========

    private UpdateUserCommand createValidCommand() {
        return new UpdateUserCommand(
                USER_ID,
                "새이름",
                "새닉네임",
                "https://example.com/new-image.jpg"
        );
    }

    private UserResponse createExpectedResponse(UpdateUserCommand command) {
        return new UserResponse(
                USER_ID,
                1L,
                100L,
                "PUBLIC",
                "ACTIVE",
                command.name(),
                command.nickname(),
                command.profileImageUrl(),
                Instant.parse("2025-11-24T00:00:00Z"),
                FIXED_TIME
        );
    }
}
