package com.ryuqq.authhub.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantStatus 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantStatus 테스트")
class TenantStatusTest {

    @Nested
    @DisplayName("TenantStatus description 테스트")
    class DescriptionTests {

        @Test
        @DisplayName("ACTIVE의 description은 '활성'이다")
        void activeDescriptionShouldBeActive() {
            // then
            assertThat(TenantStatus.ACTIVE.description()).isEqualTo("활성");
        }

        @Test
        @DisplayName("INACTIVE의 description은 '비활성'이다")
        void inactiveDescriptionShouldBeInactive() {
            // then
            assertThat(TenantStatus.INACTIVE.description()).isEqualTo("비활성");
        }
    }

    @Nested
    @DisplayName("TenantStatus isActive 테스트")
    class IsActiveTests {

        @Test
        @DisplayName("ACTIVE의 isActive()는 true를 반환한다")
        void activeShouldReturnTrue() {
            // then
            assertThat(TenantStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE의 isActive()는 false를 반환한다")
        void inactiveShouldReturnFalse() {
            // then
            assertThat(TenantStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("TenantStatus isInactive 테스트")
    class IsInactiveTests {

        @Test
        @DisplayName("ACTIVE의 isInactive()는 false를 반환한다")
        void activeShouldReturnFalse() {
            // then
            assertThat(TenantStatus.ACTIVE.isInactive()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE의 isInactive()는 true를 반환한다")
        void inactiveShouldReturnTrue() {
            // then
            assertThat(TenantStatus.INACTIVE.isInactive()).isTrue();
        }
    }
}
