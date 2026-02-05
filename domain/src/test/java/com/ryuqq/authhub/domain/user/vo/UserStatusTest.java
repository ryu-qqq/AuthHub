package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserStatus Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserStatus 테스트")
class UserStatusTest {

    @Nested
    @DisplayName("isActive 테스트")
    class IsActiveTests {

        @Test
        @DisplayName("ACTIVE는 isActive()가 true를 반환한다")
        void activeShouldReturnTrue() {
            assertThat(UserStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 isActive()가 false를 반환한다")
        void inactiveShouldReturnFalse() {
            assertThat(UserStatus.INACTIVE.isActive()).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED는 isActive()가 false를 반환한다")
        void suspendedShouldReturnFalse() {
            assertThat(UserStatus.SUSPENDED.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("canLogin 테스트")
    class CanLoginTests {

        @Test
        @DisplayName("ACTIVE는 canLogin()이 true를 반환한다")
        void activeShouldReturnTrue() {
            assertThat(UserStatus.ACTIVE.canLogin()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 canLogin()이 false를 반환한다")
        void inactiveShouldReturnFalse() {
            assertThat(UserStatus.INACTIVE.canLogin()).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED는 canLogin()이 false를 반환한다")
        void suspendedShouldReturnFalse() {
            assertThat(UserStatus.SUSPENDED.canLogin()).isFalse();
        }
    }

    @Nested
    @DisplayName("isSuspended 테스트")
    class IsSuspendedTests {

        @Test
        @DisplayName("ACTIVE는 isSuspended()가 false를 반환한다")
        void activeShouldReturnFalse() {
            assertThat(UserStatus.ACTIVE.isSuspended()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE는 isSuspended()가 false를 반환한다")
        void inactiveShouldReturnFalse() {
            assertThat(UserStatus.INACTIVE.isSuspended()).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED는 isSuspended()가 true를 반환한다")
        void suspendedShouldReturnTrue() {
            assertThat(UserStatus.SUSPENDED.isSuspended()).isTrue();
        }
    }
}
