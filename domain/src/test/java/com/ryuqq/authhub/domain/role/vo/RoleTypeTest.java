package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleType 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleType 테스트")
class RoleTypeTest {

    @Nested
    @DisplayName("isSystem 메서드")
    class IsSystemTest {

        @Test
        @DisplayName("SYSTEM 타입은 true를 반환한다")
        void shouldReturnTrueForSystem() {
            assertThat(RoleType.SYSTEM.isSystem()).isTrue();
        }

        @Test
        @DisplayName("CUSTOM 타입은 false를 반환한다")
        void shouldReturnFalseForCustom() {
            assertThat(RoleType.CUSTOM.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("isCustom 메서드")
    class IsCustomTest {

        @Test
        @DisplayName("CUSTOM 타입은 true를 반환한다")
        void shouldReturnTrueForCustom() {
            assertThat(RoleType.CUSTOM.isCustom()).isTrue();
        }

        @Test
        @DisplayName("SYSTEM 타입은 false를 반환한다")
        void shouldReturnFalseForSystem() {
            assertThat(RoleType.SYSTEM.isCustom()).isFalse();
        }
    }

    @Nested
    @DisplayName("isModifiable 메서드")
    class IsModifiableTest {

        @Test
        @DisplayName("CUSTOM 타입은 수정 가능하다")
        void shouldBeModifiableForCustom() {
            assertThat(RoleType.CUSTOM.isModifiable()).isTrue();
        }

        @Test
        @DisplayName("SYSTEM 타입은 수정 불가능하다")
        void shouldNotBeModifiableForSystem() {
            assertThat(RoleType.SYSTEM.isModifiable()).isFalse();
        }
    }
}
