package com.ryuqq.authhub.application.endpointpermission.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecResponse;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * EndpointPermissionAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("EndpointPermissionAssembler 단위 테스트")
class EndpointPermissionAssemblerTest {

    private EndpointPermissionAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new EndpointPermissionAssembler();
    }

    @Nested
    @DisplayName("toResponse 메서드")
    class ToResponseTest {

        @Test
        @DisplayName("Public 엔드포인트 권한을 Response로 변환한다")
        void shouldConvertPublicEndpointPermissionToResponse() {
            // given
            EndpointPermission endpointPermission = EndpointPermissionFixture.createPublic();

            // when
            EndpointPermissionResponse response = assembler.toResponse(endpointPermission);

            // then
            assertThat(response.id()).isNotNull();
            assertThat(response.serviceName()).isEqualTo(endpointPermission.serviceNameValue());
            assertThat(response.path()).isEqualTo(endpointPermission.pathValue());
            assertThat(response.method()).isEqualTo(endpointPermission.methodValue());
            assertThat(response.description()).isEqualTo(endpointPermission.descriptionValue());
            assertThat(response.isPublic()).isTrue();
            assertThat(response.requiredPermissions()).isEmpty();
            assertThat(response.requiredRoles()).isEmpty();
        }

        @Test
        @DisplayName("Protected 엔드포인트 권한을 Response로 변환한다")
        void shouldConvertProtectedEndpointPermissionToResponse() {
            // given
            EndpointPermission endpointPermission = EndpointPermissionFixture.createProtected();

            // when
            EndpointPermissionResponse response = assembler.toResponse(endpointPermission);

            // then
            assertThat(response.isPublic()).isFalse();
            assertThat(response.requiredPermissions()).containsExactly("user:read");
        }

        @Test
        @DisplayName("역할 기반 Protected 엔드포인트를 Response로 변환한다")
        void shouldConvertRoleBasedProtectedEndpointPermissionToResponse() {
            // given
            EndpointPermission endpointPermission =
                    EndpointPermissionFixture.createProtectedWithRole("/api/v1/admin", "ADMIN");

            // when
            EndpointPermissionResponse response = assembler.toResponse(endpointPermission);

            // then
            assertThat(response.isPublic()).isFalse();
            assertThat(response.requiredPermissions()).isEmpty();
            assertThat(response.requiredRoles()).containsExactly("ADMIN");
        }

        @Test
        @DisplayName("재구성된 엔드포인트를 Response로 변환한다")
        void shouldConvertReconstitutedEndpointPermissionToResponse() {
            // given
            EndpointPermission endpointPermission = EndpointPermissionFixture.createReconstituted();

            // when
            EndpointPermissionResponse response = assembler.toResponse(endpointPermission);

            // then
            assertThat(response.version()).isEqualTo(1L);
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toSpecResponse 메서드")
    class ToSpecResponseTest {

        @Test
        @DisplayName("Public 엔드포인트의 Spec Response를 생성한다")
        void shouldConvertPublicEndpointPermissionToSpecResponse() {
            // given
            EndpointPermission endpointPermission = EndpointPermissionFixture.createPublic();

            // when
            EndpointPermissionSpecResponse response = assembler.toSpecResponse(endpointPermission);

            // then
            assertThat(response.isPublic()).isTrue();
            assertThat(response.requiredPermissions()).isEmpty();
            assertThat(response.requiredRoles()).isEmpty();
        }

        @Test
        @DisplayName("Protected 엔드포인트의 Spec Response를 생성한다")
        void shouldConvertProtectedEndpointPermissionToSpecResponse() {
            // given
            EndpointPermission endpointPermission =
                    EndpointPermissionFixture.createProtectedWithPermissionAndRole(
                            "/api/v1/users", Set.of("user:read", "user:write"), Set.of("ADMIN"));

            // when
            EndpointPermissionSpecResponse response = assembler.toSpecResponse(endpointPermission);

            // then
            assertThat(response.isPublic()).isFalse();
            assertThat(response.requiredPermissions())
                    .containsExactlyInAnyOrder("user:read", "user:write");
            assertThat(response.requiredRoles()).containsExactly("ADMIN");
        }
    }
}
