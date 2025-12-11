package com.ryuqq.authhub.application.auth.component;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidUserStateException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LoginValidator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginValidator 단위 테스트")
class LoginValidatorTest {

    @Mock private PasswordEncoderPort passwordEncoderPort;

    private LoginValidator loginValidator;

    @BeforeEach
    void setUp() {
        loginValidator = new LoginValidator(passwordEncoderPort);
    }

    @Nested
    @DisplayName("validatePassword 메서드")
    class ValidatePasswordTest {

        @Test
        @DisplayName("비밀번호가 일치하면 예외를 발생시키지 않는다")
        void shouldNotThrowWhenPasswordMatches() {
            // given
            String rawPassword = "correct-password";
            User user = UserFixture.create();
            UUID tenantId = UserFixture.defaultTenantUUID();
            String identifier = UserFixture.defaultIdentifier();

            given(passwordEncoderPort.matches(rawPassword, user.getHashedPassword()))
                    .willReturn(true);

            // when & then
            assertThatCode(
                            () ->
                                    loginValidator.validatePassword(
                                            rawPassword, user, tenantId, identifier))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 InvalidCredentialsException을 발생시킨다")
        void shouldThrowWhenPasswordDoesNotMatch() {
            // given
            String rawPassword = "wrong-password";
            User user = UserFixture.create();
            UUID tenantId = UserFixture.defaultTenantUUID();
            String identifier = UserFixture.defaultIdentifier();

            given(passwordEncoderPort.matches(rawPassword, user.getHashedPassword()))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(
                            () ->
                                    loginValidator.validatePassword(
                                            rawPassword, user, tenantId, identifier))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("validateActiveStatus 메서드")
    class ValidateActiveStatusTest {

        @Test
        @DisplayName("활성 상태 사용자는 예외를 발생시키지 않는다")
        void shouldNotThrowForActiveUser() {
            // given
            User activeUser = UserFixture.createWithStatus(UserStatus.ACTIVE);

            // when & then
            assertThatCode(() -> loginValidator.validateActiveStatus(activeUser))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("비활성 상태 사용자는 InvalidUserStateException을 발생시킨다")
        void shouldThrowForInactiveUser() {
            // given
            User inactiveUser = UserFixture.createWithStatus(UserStatus.INACTIVE);

            // when & then
            assertThatThrownBy(() -> loginValidator.validateActiveStatus(inactiveUser))
                    .isInstanceOf(InvalidUserStateException.class);
        }

        @Test
        @DisplayName("잠금 상태 사용자는 InvalidUserStateException을 발생시킨다")
        void shouldThrowForLockedUser() {
            // given
            User lockedUser = UserFixture.createWithStatus(UserStatus.LOCKED);

            // when & then
            assertThatThrownBy(() -> loginValidator.validateActiveStatus(lockedUser))
                    .isInstanceOf(InvalidUserStateException.class);
        }

        @Test
        @DisplayName("삭제된 상태 사용자는 InvalidUserStateException을 발생시킨다")
        void shouldThrowForDeletedUser() {
            // given
            User deletedUser = UserFixture.createWithStatus(UserStatus.DELETED);

            // when & then
            assertThatThrownBy(() -> loginValidator.validateActiveStatus(deletedUser))
                    .isInstanceOf(InvalidUserStateException.class);
        }
    }

    @Nested
    @DisplayName("validate 메서드")
    class ValidateTest {

        @Test
        @DisplayName("비밀번호가 일치하고 활성 상태면 예외를 발생시키지 않는다")
        void shouldNotThrowWhenValid() {
            // given
            String rawPassword = "correct-password";
            User activeUser = UserFixture.createWithStatus(UserStatus.ACTIVE);
            UUID tenantId = UserFixture.defaultTenantUUID();
            String identifier = UserFixture.defaultIdentifier();

            given(passwordEncoderPort.matches(rawPassword, activeUser.getHashedPassword()))
                    .willReturn(true);

            // when & then
            assertThatCode(
                            () ->
                                    loginValidator.validate(
                                            rawPassword, activeUser, tenantId, identifier))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 InvalidCredentialsException을 발생시킨다")
        void shouldThrowWhenPasswordInvalid() {
            // given
            String rawPassword = "wrong-password";
            User activeUser = UserFixture.createWithStatus(UserStatus.ACTIVE);
            UUID tenantId = UserFixture.defaultTenantUUID();
            String identifier = UserFixture.defaultIdentifier();

            given(passwordEncoderPort.matches(rawPassword, activeUser.getHashedPassword()))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(
                            () ->
                                    loginValidator.validate(
                                            rawPassword, activeUser, tenantId, identifier))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        @Test
        @DisplayName("비밀번호가 일치하지만 비활성 상태면 InvalidUserStateException을 발생시킨다")
        void shouldThrowWhenUserInactive() {
            // given
            String rawPassword = "correct-password";
            User inactiveUser = UserFixture.createWithStatus(UserStatus.INACTIVE);
            UUID tenantId = UserFixture.defaultTenantUUID();
            String identifier = UserFixture.defaultIdentifier();

            given(passwordEncoderPort.matches(rawPassword, inactiveUser.getHashedPassword()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    loginValidator.validate(
                                            rawPassword, inactiveUser, tenantId, identifier))
                    .isInstanceOf(InvalidUserStateException.class);
        }
    }
}
