package com.ryuqq.authhub.domain.auth.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Role 상수 클래스 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Role 테스트")
class RoleTest {

    @Nested
    @DisplayName("역할 상수 테스트")
    class RoleConstantsTest {

        @Test
        @DisplayName("SUPER_ADMIN 역할이 정의되어 있다")
        void shouldHaveSuperAdminRole() {
            assertThat(Role.SUPER_ADMIN).isEqualTo("ROLE_SUPER_ADMIN");
        }

        @Test
        @DisplayName("TENANT_ADMIN 역할이 정의되어 있다")
        void shouldHaveTenantAdminRole() {
            assertThat(Role.TENANT_ADMIN).isEqualTo("ROLE_TENANT_ADMIN");
        }

        @Test
        @DisplayName("ORG_ADMIN 역할이 정의되어 있다")
        void shouldHaveOrgAdminRole() {
            assertThat(Role.ORG_ADMIN).isEqualTo("ROLE_ORG_ADMIN");
        }

        @Test
        @DisplayName("USER 역할이 정의되어 있다")
        void shouldHaveUserRole() {
            assertThat(Role.USER).isEqualTo("ROLE_USER");
        }
    }

    @Nested
    @DisplayName("역할 형식 테스트")
    class RoleFormatTest {

        @Test
        @DisplayName("모든 역할은 ROLE_ 접두사를 가진다")
        void shouldHaveRolePrefix() {
            assertThat(Role.SUPER_ADMIN).startsWith("ROLE_");
            assertThat(Role.TENANT_ADMIN).startsWith("ROLE_");
            assertThat(Role.ORG_ADMIN).startsWith("ROLE_");
            assertThat(Role.USER).startsWith("ROLE_");
        }

        @Test
        @DisplayName("모든 역할은 대문자로 정의되어 있다")
        void shouldBeUppercase() {
            assertThat(Role.SUPER_ADMIN).isEqualTo(Role.SUPER_ADMIN.toUpperCase());
            assertThat(Role.TENANT_ADMIN).isEqualTo(Role.TENANT_ADMIN.toUpperCase());
            assertThat(Role.ORG_ADMIN).isEqualTo(Role.ORG_ADMIN.toUpperCase());
            assertThat(Role.USER).isEqualTo(Role.USER.toUpperCase());
        }
    }

    @Nested
    @DisplayName("역할 고유성 테스트")
    class RoleUniquenessTest {

        @Test
        @DisplayName("모든 역할은 서로 다르다")
        void shouldHaveUniqueRoles() {
            assertThat(Role.SUPER_ADMIN)
                    .isNotEqualTo(Role.TENANT_ADMIN)
                    .isNotEqualTo(Role.ORG_ADMIN)
                    .isNotEqualTo(Role.USER);

            assertThat(Role.TENANT_ADMIN).isNotEqualTo(Role.ORG_ADMIN).isNotEqualTo(Role.USER);

            assertThat(Role.ORG_ADMIN).isNotEqualTo(Role.USER);
        }
    }
}
