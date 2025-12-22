package com.ryuqq.auth.common.constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Roles")
class RolesTest {

    @Nested
    @DisplayName("상수 값")
    class Constants {

        @Test
        @DisplayName("SUPER_ADMIN은 ROLE_SUPER_ADMIN")
        void superAdminValue() {
            assertThat(Roles.SUPER_ADMIN).isEqualTo("ROLE_SUPER_ADMIN");
        }

        @Test
        @DisplayName("TENANT_ADMIN은 ROLE_TENANT_ADMIN")
        void tenantAdminValue() {
            assertThat(Roles.TENANT_ADMIN).isEqualTo("ROLE_TENANT_ADMIN");
        }

        @Test
        @DisplayName("ORG_ADMIN은 ROLE_ORG_ADMIN")
        void orgAdminValue() {
            assertThat(Roles.ORG_ADMIN).isEqualTo("ROLE_ORG_ADMIN");
        }

        @Test
        @DisplayName("USER는 ROLE_USER")
        void userValue() {
            assertThat(Roles.USER).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("SERVICE는 ROLE_SERVICE")
        void serviceValue() {
            assertThat(Roles.SERVICE).isEqualTo("ROLE_SERVICE");
        }
    }

    @Nested
    @DisplayName("isSystemRole")
    class IsSystemRole {

        @Test
        @DisplayName("SUPER_ADMIN은 시스템 역할")
        void superAdminIsSystemRole() {
            assertThat(Roles.isSystemRole(Roles.SUPER_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 시스템 역할")
        void tenantAdminIsSystemRole() {
            assertThat(Roles.isSystemRole(Roles.TENANT_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("ORG_ADMIN은 시스템 역할")
        void orgAdminIsSystemRole() {
            assertThat(Roles.isSystemRole(Roles.ORG_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("USER는 시스템 역할")
        void userIsSystemRole() {
            assertThat(Roles.isSystemRole(Roles.USER)).isTrue();
        }

        @Test
        @DisplayName("SERVICE는 시스템 역할")
        void serviceIsSystemRole() {
            assertThat(Roles.isSystemRole(Roles.SERVICE)).isTrue();
        }

        @Test
        @DisplayName("알 수 없는 역할은 시스템 역할 아님")
        void unknownRoleIsNotSystemRole() {
            assertThat(Roles.isSystemRole("ROLE_CUSTOM")).isFalse();
            assertThat(Roles.isSystemRole("ADMIN")).isFalse();
            assertThat(Roles.isSystemRole("")).isFalse();
        }

        @Test
        @DisplayName("null은 시스템 역할 아님")
        void nullIsNotSystemRole() {
            assertThat(Roles.isSystemRole(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("isAdminRole")
    class IsAdminRole {

        @Test
        @DisplayName("SUPER_ADMIN은 관리자 역할")
        void superAdminIsAdminRole() {
            assertThat(Roles.isAdminRole(Roles.SUPER_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 관리자 역할")
        void tenantAdminIsAdminRole() {
            assertThat(Roles.isAdminRole(Roles.TENANT_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("ORG_ADMIN은 관리자 역할")
        void orgAdminIsAdminRole() {
            assertThat(Roles.isAdminRole(Roles.ORG_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("USER는 관리자 역할 아님")
        void userIsNotAdminRole() {
            assertThat(Roles.isAdminRole(Roles.USER)).isFalse();
        }

        @Test
        @DisplayName("SERVICE는 관리자 역할 아님")
        void serviceIsNotAdminRole() {
            assertThat(Roles.isAdminRole(Roles.SERVICE)).isFalse();
        }

        @Test
        @DisplayName("알 수 없는 역할은 관리자 역할 아님")
        void unknownRoleIsNotAdminRole() {
            assertThat(Roles.isAdminRole("ROLE_MANAGER")).isFalse();
            assertThat(Roles.isAdminRole(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("유틸리티 클래스")
    class UtilityClass {

        @Test
        @DisplayName("인스턴스화 불가")
        void cannotInstantiate() throws Exception {
            Constructor<Roles> constructor = Roles.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(AssertionError.class);
        }
    }
}
