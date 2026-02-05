package com.ryuqq.authhub.domain.tenantservice.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceStatus Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceStatus 테스트")
class TenantServiceStatusTest {

    @Nested
    @DisplayName("TenantServiceStatus description() 테스트")
    class DescriptionTests {

        @Test
        @DisplayName("ACTIVE는 활성 설명을 반환한다")
        void activeShouldReturnCorrectDescription() {
            assertThat(TenantServiceStatus.ACTIVE.description()).isEqualTo("활성");
        }

        @Test
        @DisplayName("INACTIVE는 비활성 설명을 반환한다")
        void inactiveShouldReturnCorrectDescription() {
            assertThat(TenantServiceStatus.INACTIVE.description()).isEqualTo("비활성");
        }

        @Test
        @DisplayName("SUSPENDED는 일시 중지 설명을 반환한다")
        void suspendedShouldReturnCorrectDescription() {
            assertThat(TenantServiceStatus.SUSPENDED.description()).isEqualTo("일시 중지");
        }
    }

    @Nested
    @DisplayName("TenantServiceStatus isActive() 테스트")
    class IsActiveTests {

        @Test
        @DisplayName("ACTIVE는 isActive()가 true를 반환한다")
        void activeShouldReturnTrue() {
            assertThat(TenantServiceStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE는 isActive()가 false를 반환한다")
        void inactiveShouldReturnFalse() {
            assertThat(TenantServiceStatus.INACTIVE.isActive()).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED는 isActive()가 false를 반환한다")
        void suspendedShouldReturnFalse() {
            assertThat(TenantServiceStatus.SUSPENDED.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("TenantServiceStatus isInactive() 테스트")
    class IsInactiveTests {

        @Test
        @DisplayName("INACTIVE는 isInactive()가 true를 반환한다")
        void inactiveShouldReturnTrue() {
            assertThat(TenantServiceStatus.INACTIVE.isInactive()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE는 isInactive()가 false를 반환한다")
        void activeShouldReturnFalse() {
            assertThat(TenantServiceStatus.ACTIVE.isInactive()).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED는 isInactive()가 false를 반환한다")
        void suspendedShouldReturnFalse() {
            assertThat(TenantServiceStatus.SUSPENDED.isInactive()).isFalse();
        }
    }

    @Nested
    @DisplayName("TenantServiceStatus isSuspended() 테스트")
    class IsSuspendedTests {

        @Test
        @DisplayName("SUSPENDED는 isSuspended()가 true를 반환한다")
        void suspendedShouldReturnTrue() {
            assertThat(TenantServiceStatus.SUSPENDED.isSuspended()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE는 isSuspended()가 false를 반환한다")
        void activeShouldReturnFalse() {
            assertThat(TenantServiceStatus.ACTIVE.isSuspended()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE는 isSuspended()가 false를 반환한다")
        void inactiveShouldReturnFalse() {
            assertThat(TenantServiceStatus.INACTIVE.isSuspended()).isFalse();
        }
    }
}
