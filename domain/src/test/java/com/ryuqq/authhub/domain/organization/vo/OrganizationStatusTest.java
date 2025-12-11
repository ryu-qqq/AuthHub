package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationStatus VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationStatus 테스트")
class OrganizationStatusTest {

    @Nested
    @DisplayName("ACTIVE 상태 전이")
    class ActiveTransitionTest {

        @Test
        @DisplayName("ACTIVE → INACTIVE 전이 가능")
        void shouldAllowTransitionToInactive() {
            assertThat(OrganizationStatus.ACTIVE.canTransitionTo(OrganizationStatus.INACTIVE))
                    .isTrue();
        }

        @Test
        @DisplayName("ACTIVE → DELETED 전이 가능")
        void shouldAllowTransitionToDeleted() {
            assertThat(OrganizationStatus.ACTIVE.canTransitionTo(OrganizationStatus.DELETED))
                    .isTrue();
        }

        @Test
        @DisplayName("ACTIVE → ACTIVE 전이 불가")
        void shouldNotAllowTransitionToSelf() {
            assertThat(OrganizationStatus.ACTIVE.canTransitionTo(OrganizationStatus.ACTIVE))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("INACTIVE 상태 전이")
    class InactiveTransitionTest {

        @Test
        @DisplayName("INACTIVE → ACTIVE 전이 가능")
        void shouldAllowTransitionToActive() {
            assertThat(OrganizationStatus.INACTIVE.canTransitionTo(OrganizationStatus.ACTIVE))
                    .isTrue();
        }

        @Test
        @DisplayName("INACTIVE → DELETED 전이 가능")
        void shouldAllowTransitionToDeleted() {
            assertThat(OrganizationStatus.INACTIVE.canTransitionTo(OrganizationStatus.DELETED))
                    .isTrue();
        }

        @Test
        @DisplayName("INACTIVE → INACTIVE 전이 불가")
        void shouldNotAllowTransitionToSelf() {
            assertThat(OrganizationStatus.INACTIVE.canTransitionTo(OrganizationStatus.INACTIVE))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("DELETED 상태 전이")
    class DeletedTransitionTest {

        @Test
        @DisplayName("DELETED → ACTIVE 전이 불가")
        void shouldNotAllowTransitionToActive() {
            assertThat(OrganizationStatus.DELETED.canTransitionTo(OrganizationStatus.ACTIVE))
                    .isFalse();
        }

        @Test
        @DisplayName("DELETED → INACTIVE 전이 불가")
        void shouldNotAllowTransitionToInactive() {
            assertThat(OrganizationStatus.DELETED.canTransitionTo(OrganizationStatus.INACTIVE))
                    .isFalse();
        }

        @Test
        @DisplayName("DELETED → DELETED 전이 불가")
        void shouldNotAllowTransitionToSelf() {
            assertThat(OrganizationStatus.DELETED.canTransitionTo(OrganizationStatus.DELETED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("enum 기본 동작")
    class EnumBasicsTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void shouldHaveAllStatusValues() {
            assertThat(OrganizationStatus.values())
                    .containsExactly(
                            OrganizationStatus.ACTIVE,
                            OrganizationStatus.INACTIVE,
                            OrganizationStatus.DELETED);
        }

        @Test
        @DisplayName("valueOf로 상태를 가져올 수 있다")
        void shouldGetStatusByValueOf() {
            assertThat(OrganizationStatus.valueOf("ACTIVE")).isEqualTo(OrganizationStatus.ACTIVE);
            assertThat(OrganizationStatus.valueOf("INACTIVE"))
                    .isEqualTo(OrganizationStatus.INACTIVE);
            assertThat(OrganizationStatus.valueOf("DELETED")).isEqualTo(OrganizationStatus.DELETED);
        }
    }
}
