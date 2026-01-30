package com.ryuqq.authhub.sdk.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserContext")
class UserContextTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드를 설정하여 빌드")
        void buildWithAllFields() {
            Set<String> roles = Set.of("ROLE_ADMIN", "ROLE_USER");
            Set<String> permissions = Set.of("user:read", "user:write");

            UserContext context =
                    UserContext.builder()
                            .userId("user-123")
                            .tenantId("tenant-456")
                            .organizationId("org-789")
                            .email("test@example.com")
                            .roles(roles)
                            .permissions(permissions)
                            .scope("TENANT")
                            .serviceAccount(false)
                            .correlationId("corr-abc")
                            .requestSource("web")
                            .build();

            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getTenantId()).isEqualTo("tenant-456");
            assertThat(context.getOrganizationId()).isEqualTo("org-789");
            assertThat(context.getEmail()).isEqualTo("test@example.com");
            assertThat(context.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
            assertThat(context.getPermissions())
                    .containsExactlyInAnyOrder("user:read", "user:write");
            assertThat(context.getScope()).isEqualTo("TENANT");
            assertThat(context.isServiceAccount()).isFalse();
            assertThat(context.getCorrelationId()).isEqualTo("corr-abc");
            assertThat(context.getRequestSource()).isEqualTo("web");
        }

        @Test
        @DisplayName("최소 필드로 빌드")
        void buildWithMinimalFields() {
            UserContext context = UserContext.builder().userId("user-123").build();

            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getTenantId()).isNull();
            assertThat(context.getOrganizationId()).isNull();
            assertThat(context.getEmail()).isNull();
            assertThat(context.getRoles()).isEmpty();
            assertThat(context.getPermissions()).isEmpty();
            assertThat(context.getScope()).isNull();
            assertThat(context.isServiceAccount()).isFalse();
        }

        @Test
        @DisplayName("null roles는 빈 Set으로 처리")
        void nullRolesBecomesEmptySet() {
            UserContext context = UserContext.builder().roles(null).build();

            assertThat(context.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("null permissions는 빈 Set으로 처리")
        void nullPermissionsBecomesEmptySet() {
            UserContext context = UserContext.builder().permissions(null).build();

            assertThat(context.getPermissions()).isEmpty();
        }

        @Test
        @DisplayName("서비스 계정으로 빌드")
        void buildAsServiceAccount() {
            UserContext context = UserContext.builder().serviceAccount(true).build();

            assertThat(context.isServiceAccount()).isTrue();
        }
    }

    @Nested
    @DisplayName("불변성")
    class Immutability {

        @Test
        @DisplayName("roles 반환값은 수정 불가")
        void rolesAreUnmodifiable() {
            UserContext context = UserContext.builder().roles(Set.of("ROLE_USER")).build();

            assertThatThrownBy(() -> context.getRoles().add("ROLE_ADMIN"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("permissions 반환값은 수정 불가")
        void permissionsAreUnmodifiable() {
            UserContext context = UserContext.builder().permissions(Set.of("user:read")).build();

            assertThatThrownBy(() -> context.getPermissions().add("user:write"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("hasRole")
    class HasRole {

        @Test
        @DisplayName("보유한 역할 확인")
        void hasExistingRole() {
            UserContext context =
                    UserContext.builder().roles(Set.of("ROLE_ADMIN", "ROLE_USER")).build();

            assertThat(context.hasRole("ROLE_ADMIN")).isTrue();
            assertThat(context.hasRole("ROLE_USER")).isTrue();
        }

        @Test
        @DisplayName("미보유 역할 확인")
        void doesNotHaveRole() {
            UserContext context = UserContext.builder().roles(Set.of("ROLE_USER")).build();

            assertThat(context.hasRole("ROLE_ADMIN")).isFalse();
        }

        @Test
        @DisplayName("빈 역할 목록에서 확인")
        void hasNoRoles() {
            UserContext context = UserContext.builder().build();

            assertThat(context.hasRole("ROLE_USER")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasPermission")
    class HasPermission {

        @Test
        @DisplayName("보유한 권한 확인")
        void hasExistingPermission() {
            UserContext context =
                    UserContext.builder().permissions(Set.of("user:read", "user:write")).build();

            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("user:write")).isTrue();
        }

        @Test
        @DisplayName("미보유 권한 확인")
        void doesNotHavePermission() {
            UserContext context = UserContext.builder().permissions(Set.of("user:read")).build();

            assertThat(context.hasPermission("user:delete")).isFalse();
        }

        @Test
        @DisplayName("와일드카드 권한(*:*)은 모든 권한 포함")
        void wildcardPermissionIncludesAll() {
            UserContext context = UserContext.builder().permissions(Set.of("*:*")).build();

            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("product:write")).isTrue();
            assertThat(context.hasPermission("any:permission")).isTrue();
        }

        @Test
        @DisplayName("빈 권한 목록에서 확인")
        void hasNoPermissions() {
            UserContext context = UserContext.builder().build();

            assertThat(context.hasPermission("user:read")).isFalse();
        }
    }

    @Nested
    @DisplayName("isAuthenticated")
    class IsAuthenticated {

        @Test
        @DisplayName("userId가 있으면 인증됨")
        void authenticatedWithUserId() {
            UserContext context = UserContext.builder().userId("user-123").build();

            assertThat(context.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("userId가 null이면 미인증")
        void notAuthenticatedWithNullUserId() {
            UserContext context = UserContext.builder().userId(null).build();

            assertThat(context.isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("userId가 빈 문자열이면 미인증")
        void notAuthenticatedWithEmptyUserId() {
            UserContext context = UserContext.builder().userId("").build();

            assertThat(context.isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("userId가 공백만 있으면 미인증")
        void notAuthenticatedWithBlankUserId() {
            UserContext context = UserContext.builder().userId("   ").build();

            assertThat(context.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("equals and hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("동일한 객체는 equals")
        void sameObjectEquals() {
            UserContext context =
                    UserContext.builder().userId("user-123").tenantId("tenant-456").build();

            assertThat(context.equals(context)).isTrue();
        }

        @Test
        @DisplayName("동일한 값의 객체는 equals")
        void equalValuesAreEqual() {
            UserContext context1 =
                    UserContext.builder()
                            .userId("user-123")
                            .tenantId("tenant-456")
                            .organizationId("org-789")
                            .serviceAccount(false)
                            .build();

            UserContext context2 =
                    UserContext.builder()
                            .userId("user-123")
                            .tenantId("tenant-456")
                            .organizationId("org-789")
                            .serviceAccount(false)
                            .build();

            assertThat(context1).isEqualTo(context2);
            assertThat(context1.hashCode()).isEqualTo(context2.hashCode());
        }

        @Test
        @DisplayName("다른 userId는 not equals")
        void differentUserIdNotEqual() {
            UserContext context1 = UserContext.builder().userId("user-1").build();
            UserContext context2 = UserContext.builder().userId("user-2").build();

            assertThat(context1).isNotEqualTo(context2);
        }

        @Test
        @DisplayName("다른 tenantId는 not equals")
        void differentTenantIdNotEqual() {
            UserContext context1 =
                    UserContext.builder().userId("user-123").tenantId("tenant-1").build();
            UserContext context2 =
                    UserContext.builder().userId("user-123").tenantId("tenant-2").build();

            assertThat(context1).isNotEqualTo(context2);
        }

        @Test
        @DisplayName("다른 organizationId는 not equals")
        void differentOrganizationIdNotEqual() {
            UserContext context1 =
                    UserContext.builder().userId("user-123").organizationId("org-1").build();
            UserContext context2 =
                    UserContext.builder().userId("user-123").organizationId("org-2").build();

            assertThat(context1).isNotEqualTo(context2);
        }

        @Test
        @DisplayName("다른 serviceAccount는 not equals")
        void differentServiceAccountNotEqual() {
            UserContext context1 =
                    UserContext.builder().userId("user-123").serviceAccount(true).build();
            UserContext context2 =
                    UserContext.builder().userId("user-123").serviceAccount(false).build();

            assertThat(context1).isNotEqualTo(context2);
        }

        @Test
        @DisplayName("null과 비교")
        void notEqualToNull() {
            UserContext context = UserContext.builder().userId("user-123").build();

            assertThat(context.equals(null)).isFalse();
        }

        @Test
        @DisplayName("다른 타입과 비교")
        void notEqualToDifferentType() {
            UserContext context = UserContext.builder().userId("user-123").build();

            assertThat(context.equals("string")).isFalse();
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("toString 포맷 확인")
        void toStringFormat() {
            UserContext context =
                    UserContext.builder()
                            .userId("user-123")
                            .tenantId("tenant-456")
                            .organizationId("org-789")
                            .serviceAccount(true)
                            .build();

            String result = context.toString();

            assertThat(result).contains("UserContext");
            assertThat(result).contains("userId='user-123'");
            assertThat(result).contains("tenantId='tenant-456'");
            assertThat(result).contains("organizationId='org-789'");
            assertThat(result).contains("serviceAccount=true");
        }

        @Test
        @DisplayName("null 필드가 있는 경우 toString")
        void toStringWithNullFields() {
            UserContext context = UserContext.builder().userId("user-123").build();

            String result = context.toString();

            assertThat(result).contains("userId='user-123'");
            assertThat(result).contains("tenantId='null'");
        }
    }
}
