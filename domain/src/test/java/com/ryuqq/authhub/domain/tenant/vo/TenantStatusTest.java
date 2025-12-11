package com.ryuqq.authhub.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantStatus 상태 enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantStatus 테스트")
class TenantStatusTest {

    @Nested
    @DisplayName("ACTIVE 상태")
    class ActiveStatusTest {

        @Test
        @DisplayName("INACTIVE로 전이 가능하다")
        void shouldAllowTransitionToInactive() {
            assertThat(TenantStatus.ACTIVE.canTransitionTo(TenantStatus.INACTIVE)).isTrue();
        }

        @Test
        @DisplayName("DELETED로 전이 가능하다")
        void shouldAllowTransitionToDeleted() {
            assertThat(TenantStatus.ACTIVE.canTransitionTo(TenantStatus.DELETED)).isTrue();
        }

        @Test
        @DisplayName("ACTIVE로 전이 불가능하다")
        void shouldNotAllowTransitionToActive() {
            assertThat(TenantStatus.ACTIVE.canTransitionTo(TenantStatus.ACTIVE)).isFalse();
        }
    }

    @Nested
    @DisplayName("INACTIVE 상태")
    class InactiveStatusTest {

        @Test
        @DisplayName("ACTIVE로 전이 가능하다")
        void shouldAllowTransitionToActive() {
            assertThat(TenantStatus.INACTIVE.canTransitionTo(TenantStatus.ACTIVE)).isTrue();
        }

        @Test
        @DisplayName("DELETED로 전이 가능하다")
        void shouldAllowTransitionToDeleted() {
            assertThat(TenantStatus.INACTIVE.canTransitionTo(TenantStatus.DELETED)).isTrue();
        }

        @Test
        @DisplayName("INACTIVE로 전이 불가능하다")
        void shouldNotAllowTransitionToInactive() {
            assertThat(TenantStatus.INACTIVE.canTransitionTo(TenantStatus.INACTIVE)).isFalse();
        }
    }

    @Nested
    @DisplayName("DELETED 상태")
    class DeletedStatusTest {

        @Test
        @DisplayName("ACTIVE로 전이 불가능하다")
        void shouldNotAllowTransitionToActive() {
            assertThat(TenantStatus.DELETED.canTransitionTo(TenantStatus.ACTIVE)).isFalse();
        }

        @Test
        @DisplayName("INACTIVE로 전이 불가능하다")
        void shouldNotAllowTransitionToInactive() {
            assertThat(TenantStatus.DELETED.canTransitionTo(TenantStatus.INACTIVE)).isFalse();
        }

        @Test
        @DisplayName("DELETED로 전이 불가능하다")
        void shouldNotAllowTransitionToDeleted() {
            assertThat(TenantStatus.DELETED.canTransitionTo(TenantStatus.DELETED)).isFalse();
        }
    }

    @Nested
    @DisplayName("enum 기본 기능")
    class EnumBasicsTest {

        @Test
        @DisplayName("values()는 모든 상태를 반환한다")
        void shouldReturnAllValues() {
            TenantStatus[] values = TenantStatus.values();

            assertThat(values).hasSize(3);
            assertThat(values)
                    .containsExactly(
                            TenantStatus.ACTIVE, TenantStatus.INACTIVE, TenantStatus.DELETED);
        }

        @Test
        @DisplayName("valueOf()는 이름으로 상태를 반환한다")
        void shouldReturnStatusByName() {
            assertThat(TenantStatus.valueOf("ACTIVE")).isEqualTo(TenantStatus.ACTIVE);
            assertThat(TenantStatus.valueOf("INACTIVE")).isEqualTo(TenantStatus.INACTIVE);
            assertThat(TenantStatus.valueOf("DELETED")).isEqualTo(TenantStatus.DELETED);
        }
    }
}
