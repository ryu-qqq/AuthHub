package com.ryuqq.authhub.application.user.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.user.aggregate.User;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandAssembler 단위 테스트
 *
 * <p>CommandAssembler 규칙:
 *
 * <ul>
 *   <li>Command → Domain 변환 담당
 *   <li>비밀번호 해싱은 PasswordHasher 사용
 *   <li>null-safe 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandAssembler 테스트")
class UserCommandAssemblerTest {

    @Mock private PasswordHasher passwordHasher;

    @Mock private ClockHolder clockHolder;

    @Mock private Clock clock;

    private UserCommandAssembler userCommandAssembler;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-15T10:00:00Z");
    private static final String HASHED_PASSWORD = "$2a$10$hashedPasswordValue";

    @BeforeEach
    void setUp() {
        userCommandAssembler = new UserCommandAssembler(passwordHasher, clockHolder);
    }

    @Nested
    @DisplayName("toUser() - Command -> Domain 변환")
    class ToUser {

        @Test
        @DisplayName("CreateUserCommand를 User Domain으로 변환해야 한다")
        void shouldConvertCommandToUser() {
            // Given
            CreateUserCommand command = createValidCommand();
            given(passwordHasher.hash(command.rawPassword())).willReturn(HASHED_PASSWORD);
            given(clockHolder.clock()).willReturn(clock);
            given(clock.now()).willReturn(FIXED_TIME);

            // When
            User user = userCommandAssembler.toUser(command);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.isNew()).isTrue();
            assertThat(user.tenantIdValue()).isEqualTo(command.tenantId());
            assertThat(user.organizationIdValue()).isEqualTo(command.organizationId());
            assertThat(user.userTypeValue()).isEqualTo(command.userType());
            assertThat(user.getCredential().identifier()).isEqualTo(command.identifier());
            assertThat(user.getCredential().password().hashedValue()).isEqualTo(HASHED_PASSWORD);
            assertThat(user.getProfile().name()).isEqualTo(command.name());
        }

        @Test
        @DisplayName("비밀번호가 해싱되어야 한다")
        void shouldHashPassword() {
            // Given
            CreateUserCommand command = createValidCommand();
            given(passwordHasher.hash(command.rawPassword())).willReturn(HASHED_PASSWORD);
            given(clockHolder.clock()).willReturn(clock);
            given(clock.now()).willReturn(FIXED_TIME);

            // When
            User user = userCommandAssembler.toUser(command);

            // Then
            assertThat(user.getCredential().password().hashedValue()).isEqualTo(HASHED_PASSWORD);
        }

        @Test
        @DisplayName("UserType이 null이면 null로 설정되어야 한다")
        void shouldHandleNullUserType() {
            // Given
            CreateUserCommand command = createCommandWithNullUserType();
            given(passwordHasher.hash(anyString())).willReturn(HASHED_PASSWORD);
            given(clockHolder.clock()).willReturn(clock);
            given(clock.now()).willReturn(FIXED_TIME);

            // When
            User user = userCommandAssembler.toUser(command);

            // Then
            assertThat(user.userTypeValue()).isEqualTo("PUBLIC"); // Domain 기본값
        }
    }

    // ========== Helper Methods ==========

    private CreateUserCommand createValidCommand() {
        return new CreateUserCommand(
                1L, // tenantId
                1L, // organizationId
                "test@example.com", // identifier
                "password123", // rawPassword
                "PUBLIC", // userType
                "홍길동", // name
                "+821012345678" // phoneNumber
                );
    }

    private CreateUserCommand createCommandWithNullUserType() {
        return new CreateUserCommand(
                1L, // tenantId
                1L, // organizationId
                "test@example.com", // identifier
                "password123", // rawPassword
                null, // userType (null)
                "홍길동", // name
                "+821012345678" // phoneNumber
                );
    }
}
