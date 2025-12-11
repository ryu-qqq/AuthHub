package com.ryuqq.authhub.application.auth.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TokenClaimsContext 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TokenClaimsContext 단위 테스트")
class TokenClaimsContextTest {

    @Nested
    @DisplayName("Builder 패턴")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드를 설정하여 TokenClaimsContext를 생성한다")
        void shouldBuildWithAllFields() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            UUID tenantId = UUID.randomUUID();
            String tenantName = "테스트 테넌트";
            UUID organizationId = UUID.randomUUID();
            String organizationName = "테스트 조직";
            String email = "test@example.com";
            Set<String> roles = Set.of("ROLE_ADMIN", "ROLE_USER");
            Set<String> permissions = Set.of("READ_USER", "WRITE_USER");

            // when
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(userId)
                            .tenantId(tenantId)
                            .tenantName(tenantName)
                            .organizationId(organizationId)
                            .organizationName(organizationName)
                            .email(email)
                            .roles(roles)
                            .permissions(permissions)
                            .build();

            // then
            assertThat(context.userId()).isEqualTo(userId);
            assertThat(context.tenantId()).isEqualTo(tenantId);
            assertThat(context.tenantName()).isEqualTo(tenantName);
            assertThat(context.organizationId()).isEqualTo(organizationId);
            assertThat(context.organizationName()).isEqualTo(organizationName);
            assertThat(context.email()).isEqualTo(email);
            assertThat(context.roles()).isEqualTo(roles);
            assertThat(context.permissions()).isEqualTo(permissions);
        }

        @Test
        @DisplayName("roles가 null이면 빈 Set으로 초기화된다")
        void shouldInitializeRolesAsEmptySetWhenNull() {
            // given & when
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트 테넌트")
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email("test@example.com")
                            .roles(null)
                            .permissions(Set.of("READ_USER"))
                            .build();

            // then
            assertThat(context.roles()).isNotNull();
            assertThat(context.roles()).isEmpty();
        }

        @Test
        @DisplayName("permissions가 null이면 빈 Set으로 초기화된다")
        void shouldInitializePermissionsAsEmptySetWhenNull() {
            // given & when
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트 테넌트")
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email("test@example.com")
                            .roles(Set.of("ROLE_USER"))
                            .permissions(null)
                            .build();

            // then
            assertThat(context.permissions()).isNotNull();
            assertThat(context.permissions()).isEmpty();
        }

        @Test
        @DisplayName("roles와 permissions 없이 생성하면 빈 Set이 기본값이다")
        void shouldHaveEmptySetAsDefaultForRolesAndPermissions() {
            // given & when
            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트 테넌트")
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email("test@example.com")
                            .build();

            // then
            assertThat(context.roles()).isNotNull();
            assertThat(context.roles()).isEmpty();
            assertThat(context.permissions()).isNotNull();
            assertThat(context.permissions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Record 특성")
    class RecordCharacteristicsTest {

        @Test
        @DisplayName("동일한 값을 가진 Context는 equals가 true이다")
        void shouldBeEqualWithSameValues() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Set<String> roles = Set.of("ROLE_USER");
            Set<String> permissions = Set.of("READ_USER");

            // when
            TokenClaimsContext context1 =
                    TokenClaimsContext.builder()
                            .userId(userId)
                            .tenantId(tenantId)
                            .tenantName("테스트")
                            .organizationId(organizationId)
                            .organizationName("조직")
                            .email("test@example.com")
                            .roles(roles)
                            .permissions(permissions)
                            .build();

            TokenClaimsContext context2 =
                    TokenClaimsContext.builder()
                            .userId(userId)
                            .tenantId(tenantId)
                            .tenantName("테스트")
                            .organizationId(organizationId)
                            .organizationName("조직")
                            .email("test@example.com")
                            .roles(roles)
                            .permissions(permissions)
                            .build();

            // then
            assertThat(context1).isEqualTo(context2);
            assertThat(context1.hashCode()).isEqualTo(context2.hashCode());
        }

        @Test
        @DisplayName("서로 다른 값을 가진 Context는 equals가 false이다")
        void shouldNotBeEqualWithDifferentValues() {
            // given
            UserId userId1 = UserId.of(UUID.randomUUID());
            UserId userId2 = UserId.of(UUID.randomUUID());

            // when
            TokenClaimsContext context1 =
                    TokenClaimsContext.builder()
                            .userId(userId1)
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트1")
                            .organizationId(UUID.randomUUID())
                            .organizationName("조직1")
                            .email("test1@example.com")
                            .build();

            TokenClaimsContext context2 =
                    TokenClaimsContext.builder()
                            .userId(userId2)
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트2")
                            .organizationId(UUID.randomUUID())
                            .organizationName("조직2")
                            .email("test2@example.com")
                            .build();

            // then
            assertThat(context1).isNotEqualTo(context2);
        }

        @Test
        @DisplayName("toString은 모든 필드 정보를 포함한다")
        void shouldIncludeAllFieldsInToString() {
            // given
            String tenantName = "테스트 테넌트";
            String email = "test@example.com";

            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName(tenantName)
                            .organizationId(UUID.randomUUID())
                            .organizationName("테스트 조직")
                            .email(email)
                            .build();

            // when
            String toString = context.toString();

            // then
            assertThat(toString).contains("tenantName=" + tenantName);
            assertThat(toString).contains("email=" + email);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("TokenClaimsContext는 불변 객체이다")
        void shouldBeImmutable() {
            // given
            Set<String> originalRoles = Set.of("ROLE_USER");
            Set<String> originalPermissions = Set.of("READ_USER");

            TokenClaimsContext context =
                    TokenClaimsContext.builder()
                            .userId(UserId.of(UUID.randomUUID()))
                            .tenantId(UUID.randomUUID())
                            .tenantName("테스트")
                            .organizationId(UUID.randomUUID())
                            .organizationName("조직")
                            .email("test@example.com")
                            .roles(originalRoles)
                            .permissions(originalPermissions)
                            .build();

            // when & then - Record는 자동으로 불변
            assertThat(context.roles()).isEqualTo(originalRoles);
            assertThat(context.permissions()).isEqualTo(originalPermissions);
        }
    }
}
