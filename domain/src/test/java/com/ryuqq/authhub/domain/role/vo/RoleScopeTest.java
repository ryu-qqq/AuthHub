package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleScope 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleScope 테스트")
class RoleScopeTest {

    @Nested
    @DisplayName("RoleScope isGlobal 테스트")
    class IsGlobalTests {

        @Test
        @DisplayName("GLOBAL은 isGlobal()이 true를 반환한다")
        void shouldReturnTrueForGlobal() {
            // when & then
            assertThat(RoleScope.GLOBAL.isGlobal()).isTrue();
        }

        @Test
        @DisplayName("SERVICE는 isGlobal()이 false를 반환한다")
        void shouldReturnFalseForService() {
            // when & then
            assertThat(RoleScope.SERVICE.isGlobal()).isFalse();
        }

        @Test
        @DisplayName("TENANT는 isGlobal()이 false를 반환한다")
        void shouldReturnFalseForTenant() {
            // when & then
            assertThat(RoleScope.TENANT.isGlobal()).isFalse();
        }

        @Test
        @DisplayName("TENANT_SERVICE는 isGlobal()이 false를 반환한다")
        void shouldReturnFalseForTenantService() {
            // when & then
            assertThat(RoleScope.TENANT_SERVICE.isGlobal()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleScope hasService 테스트")
    class HasServiceTests {

        @Test
        @DisplayName("SERVICE는 hasService()이 true를 반환한다")
        void shouldReturnTrueForService() {
            // when & then
            assertThat(RoleScope.SERVICE.hasService()).isTrue();
        }

        @Test
        @DisplayName("TENANT_SERVICE는 hasService()이 true를 반환한다")
        void shouldReturnTrueForTenantService() {
            // when & then
            assertThat(RoleScope.TENANT_SERVICE.hasService()).isTrue();
        }

        @Test
        @DisplayName("GLOBAL은 hasService()이 false를 반환한다")
        void shouldReturnFalseForGlobal() {
            // when & then
            assertThat(RoleScope.GLOBAL.hasService()).isFalse();
        }

        @Test
        @DisplayName("TENANT는 hasService()이 false를 반환한다")
        void shouldReturnFalseForTenant() {
            // when & then
            assertThat(RoleScope.TENANT.hasService()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleScope hasTenant 테스트")
    class HasTenantTests {

        @Test
        @DisplayName("TENANT는 hasTenant()이 true를 반환한다")
        void shouldReturnTrueForTenant() {
            // when & then
            assertThat(RoleScope.TENANT.hasTenant()).isTrue();
        }

        @Test
        @DisplayName("TENANT_SERVICE는 hasTenant()이 true를 반환한다")
        void shouldReturnTrueForTenantService() {
            // when & then
            assertThat(RoleScope.TENANT_SERVICE.hasTenant()).isTrue();
        }

        @Test
        @DisplayName("GLOBAL은 hasTenant()이 false를 반환한다")
        void shouldReturnFalseForGlobal() {
            // when & then
            assertThat(RoleScope.GLOBAL.hasTenant()).isFalse();
        }

        @Test
        @DisplayName("SERVICE는 hasTenant()이 false를 반환한다")
        void shouldReturnFalseForService() {
            // when & then
            assertThat(RoleScope.SERVICE.hasTenant()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleScope parseList 테스트")
    class ParseListTests {

        @Test
        @DisplayName("유효한 문자열 목록으로 RoleScope 목록을 파싱한다")
        void shouldParseValidStringList() {
            // when
            List<RoleScope> result =
                    RoleScope.parseList(List.of("GLOBAL", "SERVICE", "TENANT", "TENANT_SERVICE"));

            // then
            assertThat(result)
                    .containsExactly(
                            RoleScope.GLOBAL,
                            RoleScope.SERVICE,
                            RoleScope.TENANT,
                            RoleScope.TENANT_SERVICE);
        }

        @Test
        @DisplayName("null 입력 시 null을 반환한다")
        void shouldReturnNullWhenInputIsNull() {
            // when
            List<RoleScope> result = RoleScope.parseList(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환한다")
        void shouldReturnNullWhenInputIsEmpty() {
            // when
            List<RoleScope> result = RoleScope.parseList(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 값은 무시된다")
        void shouldIgnoreInvalidValues() {
            // when
            List<RoleScope> result = RoleScope.parseList(List.of("GLOBAL", "INVALID", "SERVICE"));

            // then
            assertThat(result).containsExactly(RoleScope.GLOBAL, RoleScope.SERVICE);
        }

        @Test
        @DisplayName("모든 값이 유효하지 않으면 null을 반환한다")
        void shouldReturnNullWhenAllValuesAreInvalid() {
            // when
            List<RoleScope> result = RoleScope.parseList(List.of("INVALID1", "INVALID2"));

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("대소문자 무관하게 파싱한다")
        void shouldParseCaseInsensitive() {
            // when
            List<RoleScope> result = RoleScope.parseList(List.of("global", "SERVICE", "Tenant"));

            // then
            assertThat(result)
                    .containsExactly(RoleScope.GLOBAL, RoleScope.SERVICE, RoleScope.TENANT);
        }
    }
}
