package com.ryuqq.authhub.application.endpointpermission.factory.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import java.time.Clock;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * EndpointPermissionCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("EndpointPermissionCommandFactory 단위 테스트")
class EndpointPermissionCommandFactoryTest {

    private EndpointPermissionCommandFactory factory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = EndpointPermissionFixture.fixedClock();
        factory = new EndpointPermissionCommandFactory(clock);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("Public 엔드포인트 권한을 성공적으로 생성한다")
        void shouldCreatePublicEndpointPermission() {
            // given
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub", "/api/v1/health", "GET", "헬스체크 엔드포인트", true, null, null);

            // when
            EndpointPermission result = factory.create(command);

            // then
            assertThat(result.serviceNameValue()).isEqualTo("auth-hub");
            assertThat(result.pathValue()).isEqualTo("/api/v1/health");
            assertThat(result.methodValue()).isEqualTo("GET");
            assertThat(result.descriptionValue()).isEqualTo("헬스체크 엔드포인트");
            assertThat(result.isPublic()).isTrue();
            assertThat(result.requiredPermissionValues()).isEmpty();
            assertThat(result.requiredRoleValues()).isEmpty();
        }

        @Test
        @DisplayName("Protected 엔드포인트 권한을 권한으로 생성한다")
        void shouldCreateProtectedEndpointPermissionWithPermissions() {
            // given
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub",
                            "/api/v1/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            null);

            // when
            EndpointPermission result = factory.create(command);

            // then
            assertThat(result.isPublic()).isFalse();
            assertThat(result.requiredPermissionValues()).containsExactly("user:read");
            assertThat(result.requiredRoleValues()).isEmpty();
        }

        @Test
        @DisplayName("Protected 엔드포인트 권한을 역할로 생성한다")
        void shouldCreateProtectedEndpointPermissionWithRoles() {
            // given
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub",
                            "/api/v1/admin/**",
                            "GET",
                            "관리자 API",
                            false,
                            null,
                            Set.of("ADMIN"));

            // when
            EndpointPermission result = factory.create(command);

            // then
            assertThat(result.isPublic()).isFalse();
            assertThat(result.requiredPermissionValues()).isEmpty();
            assertThat(result.requiredRoleValues()).containsExactly("ADMIN");
        }

        @Test
        @DisplayName("Protected 엔드포인트 권한을 권한과 역할 모두로 생성한다")
        void shouldCreateProtectedEndpointPermissionWithBothPermissionsAndRoles() {
            // given
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "DELETE",
                            "사용자 삭제",
                            false,
                            Set.of("user:delete"),
                            Set.of("ADMIN", "SUPER_ADMIN"));

            // when
            EndpointPermission result = factory.create(command);

            // then
            assertThat(result.isPublic()).isFalse();
            assertThat(result.requiredPermissionValues()).containsExactly("user:delete");
            assertThat(result.requiredRoleValues())
                    .containsExactlyInAnyOrder("ADMIN", "SUPER_ADMIN");
        }

        @Test
        @DisplayName("설명이 null인 경우 빈 설명으로 생성한다")
        void shouldCreateWithEmptyDescriptionWhenNull() {
            // given
            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            "auth-hub", "/api/v1/test", "GET", null, true, null, null);

            // when
            EndpointPermission result = factory.create(command);

            // then
            assertThat(result.descriptionValue()).isNull();
        }
    }
}
