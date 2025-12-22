package com.ryuqq.auth.common.constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Scopes")
class ScopesTest {

    @Nested
    @DisplayName("상수 값")
    class Constants {

        @Test
        @DisplayName("GLOBAL 상수 확인")
        void globalValue() {
            assertThat(Scopes.GLOBAL).isEqualTo("GLOBAL");
        }

        @Test
        @DisplayName("TENANT 상수 확인")
        void tenantValue() {
            assertThat(Scopes.TENANT).isEqualTo("TENANT");
        }

        @Test
        @DisplayName("ORGANIZATION 상수 확인")
        void organizationValue() {
            assertThat(Scopes.ORGANIZATION).isEqualTo("ORGANIZATION");
        }
    }

    @Nested
    @DisplayName("isValidScope")
    class IsValidScope {

        @Test
        @DisplayName("GLOBAL은 유효한 범위")
        void globalIsValid() {
            assertThat(Scopes.isValidScope(Scopes.GLOBAL)).isTrue();
        }

        @Test
        @DisplayName("TENANT는 유효한 범위")
        void tenantIsValid() {
            assertThat(Scopes.isValidScope(Scopes.TENANT)).isTrue();
        }

        @Test
        @DisplayName("ORGANIZATION은 유효한 범위")
        void organizationIsValid() {
            assertThat(Scopes.isValidScope(Scopes.ORGANIZATION)).isTrue();
        }

        @Test
        @DisplayName("알 수 없는 범위는 무효")
        void unknownScopeIsInvalid() {
            assertThat(Scopes.isValidScope("UNKNOWN")).isFalse();
            assertThat(Scopes.isValidScope("global")).isFalse();
            assertThat(Scopes.isValidScope("")).isFalse();
        }

        @Test
        @DisplayName("null은 무효")
        void nullIsInvalid() {
            assertThat(Scopes.isValidScope(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("getLevel")
    class GetLevel {

        @Test
        @DisplayName("GLOBAL 레벨은 3")
        void globalLevelIs3() {
            assertThat(Scopes.getLevel(Scopes.GLOBAL)).isEqualTo(3);
        }

        @Test
        @DisplayName("TENANT 레벨은 2")
        void tenantLevelIs2() {
            assertThat(Scopes.getLevel(Scopes.TENANT)).isEqualTo(2);
        }

        @Test
        @DisplayName("ORGANIZATION 레벨은 1")
        void organizationLevelIs1() {
            assertThat(Scopes.getLevel(Scopes.ORGANIZATION)).isEqualTo(1);
        }

        @Test
        @DisplayName("알 수 없는 범위 레벨은 0")
        void unknownLevelIs0() {
            assertThat(Scopes.getLevel("UNKNOWN")).isEqualTo(0);
            assertThat(Scopes.getLevel(null)).isEqualTo(0);
            assertThat(Scopes.getLevel("")).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("includes")
    class Includes {

        @Test
        @DisplayName("GLOBAL은 모든 범위 포함")
        void globalIncludesAll() {
            assertThat(Scopes.includes(Scopes.GLOBAL, Scopes.GLOBAL)).isTrue();
            assertThat(Scopes.includes(Scopes.GLOBAL, Scopes.TENANT)).isTrue();
            assertThat(Scopes.includes(Scopes.GLOBAL, Scopes.ORGANIZATION)).isTrue();
        }

        @Test
        @DisplayName("TENANT는 TENANT, ORGANIZATION 포함")
        void tenantIncludesTenantAndOrg() {
            assertThat(Scopes.includes(Scopes.TENANT, Scopes.GLOBAL)).isFalse();
            assertThat(Scopes.includes(Scopes.TENANT, Scopes.TENANT)).isTrue();
            assertThat(Scopes.includes(Scopes.TENANT, Scopes.ORGANIZATION)).isTrue();
        }

        @Test
        @DisplayName("ORGANIZATION은 ORGANIZATION만 포함")
        void organizationIncludesOnlyOrg() {
            assertThat(Scopes.includes(Scopes.ORGANIZATION, Scopes.GLOBAL)).isFalse();
            assertThat(Scopes.includes(Scopes.ORGANIZATION, Scopes.TENANT)).isFalse();
            assertThat(Scopes.includes(Scopes.ORGANIZATION, Scopes.ORGANIZATION)).isTrue();
        }

        @Test
        @DisplayName("알 수 없는 범위 처리")
        void unknownScopeHandling() {
            assertThat(Scopes.includes("UNKNOWN", Scopes.ORGANIZATION)).isFalse();
            assertThat(Scopes.includes(Scopes.GLOBAL, "UNKNOWN")).isTrue();
            assertThat(Scopes.includes(null, null)).isTrue();
        }
    }

    @Nested
    @DisplayName("유틸리티 클래스")
    class UtilityClass {

        @Test
        @DisplayName("인스턴스화 불가")
        void cannotInstantiate() throws Exception {
            Constructor<Scopes> constructor = Scopes.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
        }
    }
}
