package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserStatus VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserStatus 테스트")
class UserStatusTest {

    @Nested
    @DisplayName("ACTIVE 상태")
    class ActiveStatusTest {

        @Test
        @DisplayName("ACTIVE 상태는 활성 상태이다")
        void shouldBeActive() {
            assertThat(UserStatus.ACTIVE.isActive()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE 상태는 로그인 가능하다")
        void shouldBeAbleToLogin() {
            assertThat(UserStatus.ACTIVE.canLogin()).isTrue();
        }

        @Test
        @DisplayName("ACTIVE 상태의 설명은 '활성'이다")
        void shouldHaveCorrectDescription() {
            assertThat(UserStatus.ACTIVE.getDescription()).isEqualTo("활성");
        }
    }

    @Nested
    @DisplayName("INACTIVE 상태")
    class InactiveStatusTest {

        @Test
        @DisplayName("INACTIVE 상태는 비활성 상태이다")
        void shouldNotBeActive() {
            assertThat(UserStatus.INACTIVE.isActive()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE 상태는 로그인 불가하다")
        void shouldNotBeAbleToLogin() {
            assertThat(UserStatus.INACTIVE.canLogin()).isFalse();
        }

        @Test
        @DisplayName("INACTIVE 상태의 설명은 '비활성'이다")
        void shouldHaveCorrectDescription() {
            assertThat(UserStatus.INACTIVE.getDescription()).isEqualTo("비활성");
        }
    }

    @Nested
    @DisplayName("LOCKED 상태")
    class LockedStatusTest {

        @Test
        @DisplayName("LOCKED 상태는 비활성 상태이다")
        void shouldNotBeActive() {
            assertThat(UserStatus.LOCKED.isActive()).isFalse();
        }

        @Test
        @DisplayName("LOCKED 상태는 로그인 불가하다")
        void shouldNotBeAbleToLogin() {
            assertThat(UserStatus.LOCKED.canLogin()).isFalse();
        }

        @Test
        @DisplayName("LOCKED 상태의 설명은 '잠금'이다")
        void shouldHaveCorrectDescription() {
            assertThat(UserStatus.LOCKED.getDescription()).isEqualTo("잠금");
        }
    }

    @Nested
    @DisplayName("DELETED 상태")
    class DeletedStatusTest {

        @Test
        @DisplayName("DELETED 상태는 비활성 상태이다")
        void shouldNotBeActive() {
            assertThat(UserStatus.DELETED.isActive()).isFalse();
        }

        @Test
        @DisplayName("DELETED 상태는 로그인 불가하다")
        void shouldNotBeAbleToLogin() {
            assertThat(UserStatus.DELETED.canLogin()).isFalse();
        }

        @Test
        @DisplayName("DELETED 상태의 설명은 '삭제됨'이다")
        void shouldHaveCorrectDescription() {
            assertThat(UserStatus.DELETED.getDescription()).isEqualTo("삭제됨");
        }
    }

    @Nested
    @DisplayName("enum 기본 동작")
    class EnumBasicsTest {

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void shouldHaveAllStatusValues() {
            assertThat(UserStatus.values())
                    .containsExactly(
                            UserStatus.ACTIVE,
                            UserStatus.INACTIVE,
                            UserStatus.LOCKED,
                            UserStatus.DELETED);
        }

        @Test
        @DisplayName("valueOf로 상태를 가져올 수 있다")
        void shouldGetStatusByValueOf() {
            assertThat(UserStatus.valueOf("ACTIVE")).isEqualTo(UserStatus.ACTIVE);
            assertThat(UserStatus.valueOf("INACTIVE")).isEqualTo(UserStatus.INACTIVE);
            assertThat(UserStatus.valueOf("LOCKED")).isEqualTo(UserStatus.LOCKED);
            assertThat(UserStatus.valueOf("DELETED")).isEqualTo(UserStatus.DELETED);
        }
    }
}
