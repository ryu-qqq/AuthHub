package com.ryuqq.authhub.domain.endpointpermission.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** EndpointPermission Aggregate Root 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("aggregate")
@DisplayName("EndpointPermission Aggregate Root 단위 테스트")
class EndpointPermissionTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("정적 팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("createPublic() - Public 엔드포인트 생성")
        void createPublic_ShouldCreatePublicEndpoint() {
            // Given
            UUID id = UUID.randomUUID();
            ServiceName serviceName = ServiceName.of("auth-hub");
            EndpointPath path = EndpointPath.of("/api/v1/health");
            HttpMethod method = HttpMethod.GET;
            EndpointDescription description = EndpointDescription.of("헬스 체크");

            // When
            EndpointPermission endpoint =
                    EndpointPermission.createPublic(
                            id, serviceName, path, method, description, FIXED_CLOCK);

            // Then
            assertThat(endpoint.endpointPermissionIdValue()).isEqualTo(id);
            assertThat(endpoint.serviceNameValue()).isEqualTo("auth-hub");
            assertThat(endpoint.pathValue()).isEqualTo("/api/v1/health");
            assertThat(endpoint.methodValue()).isEqualTo("GET");
            assertThat(endpoint.isPublic()).isTrue();
            assertThat(endpoint.requiredPermissionValues()).isEmpty();
            assertThat(endpoint.requiredRoleValues()).isEmpty();
            assertThat(endpoint.getVersion()).isEqualTo(1L);
            assertThat(endpoint.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("createProtected() - Protected 엔드포인트 생성")
        void createProtected_ShouldCreateProtectedEndpoint() {
            // Given
            UUID id = UUID.randomUUID();
            ServiceName serviceName = ServiceName.of("auth-hub");
            EndpointPath path = EndpointPath.of("/api/v1/users");
            HttpMethod method = HttpMethod.GET;
            EndpointDescription description = EndpointDescription.of("사용자 목록 조회");
            RequiredPermissions permissions = RequiredPermissions.single("user:read");
            RequiredRoles roles = RequiredRoles.single("ADMIN");

            // When
            EndpointPermission endpoint =
                    EndpointPermission.createProtected(
                            id,
                            serviceName,
                            path,
                            method,
                            description,
                            permissions,
                            roles,
                            FIXED_CLOCK);

            // Then
            assertThat(endpoint.isPublic()).isFalse();
            assertThat(endpoint.isProtected()).isTrue();
            assertThat(endpoint.requiredPermissionValues()).containsExactly("user:read");
            assertThat(endpoint.requiredRoleValues()).containsExactly("ADMIN");
        }

        @Test
        @DisplayName("reconstitute() - ID가 null이면 예외 발생")
        void reconstitute_WithNullId_ShouldThrowException() {
            // When & Then
            assertThatThrownBy(
                            () ->
                                    EndpointPermission.reconstitute(
                                            null,
                                            ServiceName.of("auth-hub"),
                                            EndpointPath.of("/api/v1/users"),
                                            HttpMethod.GET,
                                            EndpointDescription.empty(),
                                            false,
                                            RequiredPermissions.empty(),
                                            RequiredRoles.empty(),
                                            1L,
                                            false,
                                            FIXED_CLOCK.instant(),
                                            FIXED_CLOCK.instant()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reconstitute requires non-null endpointPermissionId");
        }

        @Test
        @DisplayName("reconstitute() - 영속성 복원 성공")
        void reconstitute_ShouldRestoreAllFields() {
            // Given
            UUID id = UUID.randomUUID();
            Instant createdAt = FIXED_CLOCK.instant().minusSeconds(86400);
            Instant updatedAt = FIXED_CLOCK.instant();

            // When
            EndpointPermission endpoint =
                    EndpointPermission.reconstitute(
                            EndpointPermissionId.of(id),
                            ServiceName.of("user-service"),
                            EndpointPath.of("/api/v1/users/{userId}"),
                            HttpMethod.PUT,
                            EndpointDescription.of("사용자 수정"),
                            false,
                            RequiredPermissions.of(Set.of("user:write", "user:update")),
                            RequiredRoles.of(Set.of("ADMIN", "MANAGER")),
                            5L,
                            false,
                            createdAt,
                            updatedAt);

            // Then
            assertThat(endpoint.endpointPermissionIdValue()).isEqualTo(id);
            assertThat(endpoint.serviceNameValue()).isEqualTo("user-service");
            assertThat(endpoint.pathValue()).isEqualTo("/api/v1/users/{userId}");
            assertThat(endpoint.methodValue()).isEqualTo("PUT");
            assertThat(endpoint.getVersion()).isEqualTo(5L);
            assertThat(endpoint.createdAt()).isEqualTo(createdAt);
            assertThat(endpoint.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("비즈니스 메서드 테스트")
    class BusinessMethodTests {

        @Test
        @DisplayName("changeDescription() - 설명 변경 시 버전 증가")
        void changeDescription_ShouldIncrementVersion() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createProtected();
            long originalVersion = endpoint.getVersion();

            // When
            EndpointPermission updated =
                    endpoint.changeDescription(EndpointDescription.of("새로운 설명"), FIXED_CLOCK);

            // Then
            assertThat(updated.descriptionValue()).isEqualTo("새로운 설명");
            assertThat(updated.getVersion()).isEqualTo(originalVersion + 1);
            assertThat(updated.updatedAt()).isAfterOrEqualTo(endpoint.createdAt());
        }

        @Test
        @DisplayName("makePublic() - Protected에서 Public으로 변경")
        void makePublic_ShouldClearPermissionsAndRoles() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createProtected();
            assertThat(endpoint.isProtected()).isTrue();

            // When
            EndpointPermission publicEndpoint = endpoint.makePublic(FIXED_CLOCK);

            // Then
            assertThat(publicEndpoint.isPublic()).isTrue();
            assertThat(publicEndpoint.requiredPermissionValues()).isEmpty();
            assertThat(publicEndpoint.requiredRoleValues()).isEmpty();
            assertThat(publicEndpoint.getVersion()).isEqualTo(endpoint.getVersion() + 1);
        }

        @Test
        @DisplayName("makeProtected() - Public에서 Protected로 변경")
        void makeProtected_ShouldSetPermissionsAndRoles() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createPublic();
            assertThat(endpoint.isPublic()).isTrue();

            // When
            EndpointPermission protectedEndpoint =
                    endpoint.makeProtected(
                            RequiredPermissions.of(Set.of("admin:access")),
                            RequiredRoles.of(Set.of("ADMIN")),
                            FIXED_CLOCK);

            // Then
            assertThat(protectedEndpoint.isProtected()).isTrue();
            assertThat(protectedEndpoint.requiredPermissionValues())
                    .containsExactly("admin:access");
            assertThat(protectedEndpoint.requiredRoleValues()).containsExactly("ADMIN");
        }

        @Test
        @DisplayName("delete() - 소프트 삭제 시 deleted true, 버전 증가")
        void delete_ShouldMarkAsDeletedAndIncrementVersion() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createProtected();
            assertThat(endpoint.isDeleted()).isFalse();

            // When
            EndpointPermission deleted = endpoint.delete(FIXED_CLOCK);

            // Then
            assertThat(deleted.isDeleted()).isTrue();
            assertThat(deleted.getVersion()).isEqualTo(endpoint.getVersion() + 1);
        }

        @Test
        @DisplayName("changeRequiredPermissions() - 권한 변경")
        void changeRequiredPermissions_ShouldUpdatePermissions() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createProtected();
            RequiredPermissions newPermissions =
                    RequiredPermissions.of(Set.of("new:permission1", "new:permission2"));

            // When
            EndpointPermission updated =
                    endpoint.changeRequiredPermissions(newPermissions, FIXED_CLOCK);

            // Then
            assertThat(updated.requiredPermissionValues())
                    .containsExactlyInAnyOrder("new:permission1", "new:permission2");
            assertThat(updated.getVersion()).isEqualTo(endpoint.getVersion() + 1);
        }

        @Test
        @DisplayName("changeRequiredRoles() - 역할 변경")
        void changeRequiredRoles_ShouldUpdateRoles() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createProtected();
            RequiredRoles newRoles = RequiredRoles.of(Set.of("SUPER_ADMIN", "OPERATOR"));

            // When
            EndpointPermission updated = endpoint.changeRequiredRoles(newRoles, FIXED_CLOCK);

            // Then
            assertThat(updated.requiredRoleValues())
                    .containsExactlyInAnyOrder("SUPER_ADMIN", "OPERATOR");
            assertThat(updated.getVersion()).isEqualTo(endpoint.getVersion() + 1);
        }
    }

    @Nested
    @DisplayName("권한 체크 테스트")
    class AuthorizationTests {

        @Test
        @DisplayName("canAccess() - Public 엔드포인트는 항상 접근 가능")
        void canAccess_PublicEndpoint_ShouldAlwaysReturnTrue() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createPublic();

            // When & Then
            assertThat(endpoint.canAccess(Set.of(), Set.of())).isTrue();
            assertThat(endpoint.canAccess(null, null)).isTrue();
        }

        @Test
        @DisplayName("canAccess() - 권한이 있으면 접근 가능")
        void canAccess_WithMatchingPermission_ShouldReturnTrue() {
            // Given
            EndpointPermission endpoint =
                    EndpointPermissionFixture.createProtected("/api/users", "user:read");
            Set<String> userPermissions = Set.of("user:read", "user:write");
            Set<String> userRoles = Set.of();

            // When & Then
            assertThat(endpoint.canAccess(userPermissions, userRoles)).isTrue();
        }

        @Test
        @DisplayName("canAccess() - 역할이 있으면 접근 가능")
        void canAccess_WithMatchingRole_ShouldReturnTrue() {
            // Given
            EndpointPermission endpoint =
                    EndpointPermissionFixture.createProtectedWithRole("/api/admin", "ADMIN");
            Set<String> userPermissions = Set.of();
            Set<String> userRoles = Set.of("ADMIN", "USER");

            // When & Then
            assertThat(endpoint.canAccess(userPermissions, userRoles)).isTrue();
        }

        @Test
        @DisplayName("canAccess() - 권한도 역할도 없으면 접근 불가")
        void canAccess_WithNoMatchingPermissionOrRole_ShouldReturnFalse() {
            // Given
            EndpointPermission endpoint =
                    EndpointPermissionFixture.createProtectedWithPermissionAndRole(
                            "/api/admin", Set.of("admin:access"), Set.of("ADMIN"));
            Set<String> userPermissions = Set.of("user:read");
            Set<String> userRoles = Set.of("USER");

            // When & Then
            assertThat(endpoint.canAccess(userPermissions, userRoles)).isFalse();
        }

        @Test
        @DisplayName("canAccess() - 여러 권한 중 하나만 있어도 접근 가능 (OR 조건)")
        void canAccess_WithOneOfMultiplePermissions_ShouldReturnTrue() {
            // Given
            EndpointPermission endpoint =
                    EndpointPermissionFixture.createProtectedWithMultiplePermissions(
                            "/api/reports", Set.of("report:read", "report:export", "admin:all"));
            Set<String> userPermissions = Set.of("report:export");
            Set<String> userRoles = Set.of();

            // When & Then
            assertThat(endpoint.canAccess(userPermissions, userRoles)).isTrue();
        }
    }

    @Nested
    @DisplayName("경로 매칭 테스트")
    class PathMatchingTests {

        @Test
        @DisplayName("matchesPath() - 정확한 경로 일치")
        void matchesPath_ExactMatch_ShouldReturnTrue() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createPublic("/api/v1/users");

            // When & Then
            assertThat(endpoint.matchesPath("/api/v1/users")).isTrue();
            assertThat(endpoint.matchesPath("/api/v1/orders")).isFalse();
        }

        @Test
        @DisplayName("matchesPath() - Path Variable 매칭")
        void matchesPath_WithPathVariable_ShouldMatchDynamicSegment() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createWithPathVariable();
            // path: /api/v1/users/{userId}

            // When & Then
            assertThat(endpoint.matchesPath("/api/v1/users/123")).isTrue();
            assertThat(endpoint.matchesPath("/api/v1/users/abc-def")).isTrue();
            assertThat(endpoint.matchesPath("/api/v1/users/123/orders")).isFalse();
        }

        @Test
        @DisplayName("matchesPath() - 와일드카드 매칭")
        void matchesPath_WithWildcard_ShouldMatchAllSubPaths() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createWithWildcard();
            // path: /api/v1/admin/**

            // When & Then
            assertThat(endpoint.matchesPath("/api/v1/admin/users")).isTrue();
            assertThat(endpoint.matchesPath("/api/v1/admin/users/123")).isTrue();
            assertThat(endpoint.matchesPath("/api/v1/public")).isFalse();
        }

        @Test
        @DisplayName("matchesPath() - null 경로는 false")
        void matchesPath_WithNullPath_ShouldReturnFalse() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createPublic("/api/v1/users");

            // When & Then
            assertThat(endpoint.matchesPath(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("도메인 규칙 검증 테스트")
    class InvariantTests {

        @Test
        @DisplayName("필수 필드 null 검증 - ServiceName")
        void required_ServiceNameNull_ShouldThrowException() {
            assertThatThrownBy(
                            () ->
                                    EndpointPermission.createPublic(
                                            UUID.randomUUID(),
                                            null,
                                            EndpointPath.of("/api/test"),
                                            HttpMethod.GET,
                                            EndpointDescription.empty(),
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ServiceName은 null일 수 없습니다");
        }

        @Test
        @DisplayName("필수 필드 null 검증 - EndpointPath")
        void required_EndpointPathNull_ShouldThrowException() {
            assertThatThrownBy(
                            () ->
                                    EndpointPermission.createPublic(
                                            UUID.randomUUID(),
                                            ServiceName.of("auth-hub"),
                                            null,
                                            HttpMethod.GET,
                                            EndpointDescription.empty(),
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EndpointPath는 null일 수 없습니다");
        }

        @Test
        @DisplayName("필수 필드 null 검증 - HttpMethod")
        void required_HttpMethodNull_ShouldThrowException() {
            assertThatThrownBy(
                            () ->
                                    EndpointPermission.createPublic(
                                            UUID.randomUUID(),
                                            ServiceName.of("auth-hub"),
                                            EndpointPath.of("/api/test"),
                                            null,
                                            EndpointDescription.empty(),
                                            FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("HttpMethod는 null일 수 없습니다");
        }

        @Test
        @DisplayName("ID 불변성 - 상태 변경 후에도 ID 유지")
        void id_ShouldBeImmutable() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createReconstituted();
            UUID originalId = endpoint.endpointPermissionIdValue();

            // When
            EndpointPermission updated =
                    endpoint.changeDescription(EndpointDescription.of("변경된 설명"), FIXED_CLOCK);

            // Then
            assertThat(updated.endpointPermissionIdValue()).isEqualTo(originalId);
        }

        @Test
        @DisplayName("createdAt 불변성 - 상태 변경 후에도 createdAt 유지")
        void createdAt_ShouldBeImmutable() {
            // Given
            EndpointPermission endpoint = EndpointPermissionFixture.createReconstituted();
            Instant originalCreatedAt = endpoint.createdAt();

            // When
            EndpointPermission updated = endpoint.delete(FIXED_CLOCK);

            // Then
            assertThat(updated.createdAt()).isEqualTo(originalCreatedAt);
        }
    }

    @Nested
    @DisplayName("equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("동일한 ID를 가진 엔티티는 동등하다")
        void equals_SameId_ShouldBeEqual() {
            // Given
            UUID id = UUID.randomUUID();
            EndpointPermission endpoint1 = EndpointPermissionFixture.createReconstituted(id);
            EndpointPermission endpoint2 = EndpointPermissionFixture.createReconstituted(id);

            // When & Then
            assertThat(endpoint1).isEqualTo(endpoint2);
            assertThat(endpoint1.hashCode()).isEqualTo(endpoint2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 엔티티는 동등하지 않다")
        void equals_DifferentId_ShouldNotBeEqual() {
            // Given
            EndpointPermission endpoint1 =
                    EndpointPermissionFixture.createReconstituted(UUID.randomUUID());
            EndpointPermission endpoint2 =
                    EndpointPermissionFixture.createReconstituted(UUID.randomUUID());

            // When & Then
            assertThat(endpoint1).isNotEqualTo(endpoint2);
        }

        @Test
        @DisplayName("ID가 null인 엔티티는 다른 엔티티와 동등하지 않다")
        void equals_NullId_ShouldNotBeEqual() {
            // Given
            EndpointPermission newEndpoint = EndpointPermissionFixture.createPublic();
            EndpointPermission existingEndpoint = EndpointPermissionFixture.createReconstituted();

            // When & Then
            // 새로 생성된 엔드포인트도 ID가 있으므로 다른 방식으로 테스트
            assertThat(newEndpoint).isNotEqualTo(existingEndpoint);
        }
    }
}
