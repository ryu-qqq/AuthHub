package com.ryuqq.authhub.application.user.component;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PasswordChangeValidator 단위 테스트
 *
 * <p>비밀번호 변경 검증 컴포넌트 테스트입니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>verified=false: 현재 비밀번호 검증 필요 (일반 변경)
 *   <li>verified=true: 검증 건너뜀 (비밀번호 재설정 - 본인 인증 완료)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordChangeValidator 테스트")
class PasswordChangeValidatorTest {

    @Mock private PasswordHasher passwordHasher;

    private PasswordChangeValidator passwordChangeValidator;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CURRENT_PASSWORD = "currentPassword123!";
    private static final String NEW_PASSWORD = "newPassword456!";
    private static final String STORED_HASHED_PASSWORD = "$2a$10$storedHashedPassword";

    @BeforeEach
    void setUp() {
        passwordChangeValidator = new PasswordChangeValidator(passwordHasher);
    }

    @Nested
    @DisplayName("validate() - 일반 비밀번호 변경 (verified=false)")
    class ValidateNormalChange {

        @Test
        @DisplayName("현재 비밀번호가 일치하면 검증이 통과해야 한다")
        void shouldPassWhenCurrentPasswordMatches() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, CURRENT_PASSWORD, NEW_PASSWORD);
            User user = createMockUser();

            given(user.getHashedPassword()).willReturn(STORED_HASHED_PASSWORD);
            given(passwordHasher.matches(CURRENT_PASSWORD, STORED_HASHED_PASSWORD))
                    .willReturn(true);

            // When & Then
            assertThatCode(() -> passwordChangeValidator.validate(command, user))
                    .doesNotThrowAnyException();

            verify(passwordHasher).matches(CURRENT_PASSWORD, STORED_HASHED_PASSWORD);
        }

        @Test
        @DisplayName("현재 비밀번호가 일치하지 않으면 InvalidPasswordException이 발생해야 한다")
        void shouldThrowWhenCurrentPasswordNotMatches() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, "wrongPassword", NEW_PASSWORD);
            User user = createMockUser();

            given(user.getHashedPassword()).willReturn(STORED_HASHED_PASSWORD);
            given(passwordHasher.matches("wrongPassword", STORED_HASHED_PASSWORD))
                    .willReturn(false);

            // When & Then
            assertThatThrownBy(() -> passwordChangeValidator.validate(command, user))
                    .isInstanceOf(InvalidPasswordException.class);

            verify(passwordHasher).matches("wrongPassword", STORED_HASHED_PASSWORD);
        }
    }

    @Nested
    @DisplayName("validate() - 비밀번호 재설정 (verified=true)")
    class ValidatePasswordReset {

        @Test
        @DisplayName("본인 인증 완료 시 현재 비밀번호 검증을 건너뛰어야 한다")
        void shouldSkipValidationWhenVerified() {
            // Given
            ChangePasswordCommand command = ChangePasswordCommand.forReset(USER_ID, NEW_PASSWORD);
            User user = createMockUser();

            // When & Then
            assertThatCode(() -> passwordChangeValidator.validate(command, user))
                    .doesNotThrowAnyException();

            // PasswordHasher가 호출되지 않아야 함
            verify(passwordHasher, never())
                    .matches(
                            org.mockito.ArgumentMatchers.anyString(),
                            org.mockito.ArgumentMatchers.anyString());
        }

        @Test
        @DisplayName("verified=true이면 currentPassword가 null이어도 검증이 통과해야 한다")
        void shouldPassEvenWhenCurrentPasswordIsNull() {
            // Given
            ChangePasswordCommand command = ChangePasswordCommand.forReset(USER_ID, NEW_PASSWORD);
            User user = createMockUser();

            // currentPassword가 null임을 확인
            assertThatCode(
                            () -> {
                                // forReset은 currentPassword를 null로 설정
                                assert command.currentPassword() == null;
                                passwordChangeValidator.validate(command, user);
                            })
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {

        @Test
        @DisplayName("verified=false이고 currentPassword가 빈 문자열이면 검증 실패해야 한다")
        void shouldFailWhenCurrentPasswordIsEmpty() {
            // Given
            ChangePasswordCommand command =
                    ChangePasswordCommand.forChange(USER_ID, "", NEW_PASSWORD);
            User user = createMockUser();

            given(user.getHashedPassword()).willReturn(STORED_HASHED_PASSWORD);
            given(passwordHasher.matches("", STORED_HASHED_PASSWORD)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> passwordChangeValidator.validate(command, user))
                    .isInstanceOf(InvalidPasswordException.class);
        }
    }

    // ========== Helper Methods ==========

    private User createMockUser() {
        return mock(User.class);
    }
}
