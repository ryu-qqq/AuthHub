package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationStatus 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationStatus 테스트")
class OrganizationStatusTest {

    @Nested
    @DisplayName("OrganizationStatus description 테스트")
    class DescriptionTests {

        @Test
        @DisplayName("ACTIVE의 description은 '활성'이다")
        void activeDescriptionShouldBeActive() {
            // then
            assertThat(OrganizationStatus.ACTIVE.description()).isEqualTo("활성");
        }

        @Test
        @DisplayName("INACTIVE의 description은 '비활성'이다")
        void inactiveDescriptionShouldBeInactive() {
            // then
            assertThat(OrganizationStatus.INACTIVE.description()).isEqualTo("비활성");
        }
    }

    @Nested
    @DisplayName("OrganizationStatus isActive 테스트")
    class IsActiveTests {

        @Test
        @DisplayName("ACTIVE의 isActive()는 true를 반환한다")
        void activeShouldReturnTrue() {
            // then
            assertThat(OrganizationStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE의 isActive()는 false를 반환한다")
        void inactiveShouldReturnFalse() {
            // then
            assertThat(OrganizationStatus.INACTIVE.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("OrganizationStatus isInactive 테스트")
    class IsInactiveTests {

        @Test
        @DisplayName("ACTIVE의 isInactive()는 false를 반환한다")
        void activeShouldReturnFalse() {
            // then
            assertThat(OrganizationStatus.ACTIVE.isInactive()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE의 isInactive()는 true를 반환한다")
        void inactiveShouldReturnTrue() {
            // then
            assertThat(OrganizationStatus.INACTIVE.isInactive()).isTrue();
        }
    }
}
