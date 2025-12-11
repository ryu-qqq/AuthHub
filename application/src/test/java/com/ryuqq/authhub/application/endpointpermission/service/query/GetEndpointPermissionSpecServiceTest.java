package com.ryuqq.authhub.application.endpointpermission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetEndpointPermissionSpecService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetEndpointPermissionSpecService 단위 테스트")
class GetEndpointPermissionSpecServiceTest {

    @Mock private EndpointPermissionReadManager readManager;
    @Mock private EndpointPermissionAssembler assembler;

    private GetEndpointPermissionSpecService service;

    @BeforeEach
    void setUp() {
        service = new GetEndpointPermissionSpecService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("요청 경로와 매칭되는 Public 엔드포인트 스펙을 반환한다")
        void shouldReturnPublicEndpointSpec() {
            // given
            EndpointPermission ep = EndpointPermissionFixture.createPublic("/api/v1/health");
            GetEndpointPermissionSpecQuery query =
                    new GetEndpointPermissionSpecQuery("auth-hub", "/api/v1/health", "GET");
            EndpointPermissionSpecResponse expectedResponse =
                    new EndpointPermissionSpecResponse(true, Set.of(), Set.of());

            given(
                            readManager.findAllByServiceNameAndMethod(
                                    any(ServiceName.class), any(HttpMethod.class)))
                    .willReturn(List.of(ep));
            given(assembler.toSpecResponse(ep)).willReturn(expectedResponse);

            // when
            Optional<EndpointPermissionSpecResponse> result = service.execute(query);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().isPublic()).isTrue();
            assertThat(result.get().requiredPermissions()).isEmpty();
        }

        @Test
        @DisplayName("요청 경로와 매칭되는 Protected 엔드포인트 스펙을 반환한다")
        void shouldReturnProtectedEndpointSpec() {
            // given
            EndpointPermission ep =
                    EndpointPermissionFixture.createProtected("/api/v1/users", "user:read");
            GetEndpointPermissionSpecQuery query =
                    new GetEndpointPermissionSpecQuery("auth-hub", "/api/v1/users", "GET");
            EndpointPermissionSpecResponse expectedResponse =
                    new EndpointPermissionSpecResponse(false, Set.of("user:read"), Set.of());

            given(
                            readManager.findAllByServiceNameAndMethod(
                                    any(ServiceName.class), any(HttpMethod.class)))
                    .willReturn(List.of(ep));
            given(assembler.toSpecResponse(ep)).willReturn(expectedResponse);

            // when
            Optional<EndpointPermissionSpecResponse> result = service.execute(query);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().isPublic()).isFalse();
            assertThat(result.get().requiredPermissions()).containsExactly("user:read");
        }

        @Test
        @DisplayName("Path Variable이 있는 경로와 매칭한다")
        void shouldMatchPathWithPathVariable() {
            // given
            EndpointPermission ep = EndpointPermissionFixture.createWithPathVariable();
            GetEndpointPermissionSpecQuery query =
                    new GetEndpointPermissionSpecQuery("auth-hub", "/api/v1/users/123", "GET");
            EndpointPermissionSpecResponse expectedResponse =
                    new EndpointPermissionSpecResponse(false, Set.of("user:read"), Set.of());

            given(
                            readManager.findAllByServiceNameAndMethod(
                                    any(ServiceName.class), any(HttpMethod.class)))
                    .willReturn(List.of(ep));
            given(assembler.toSpecResponse(ep)).willReturn(expectedResponse);

            // when
            Optional<EndpointPermissionSpecResponse> result = service.execute(query);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("와일드카드 경로와 매칭한다")
        void shouldMatchPathWithWildcard() {
            // given
            EndpointPermission ep = EndpointPermissionFixture.createWithWildcard();
            GetEndpointPermissionSpecQuery query =
                    new GetEndpointPermissionSpecQuery(
                            "auth-hub", "/api/v1/admin/users/123/roles", "GET");
            EndpointPermissionSpecResponse expectedResponse =
                    new EndpointPermissionSpecResponse(false, Set.of(), Set.of("ADMIN"));

            given(
                            readManager.findAllByServiceNameAndMethod(
                                    any(ServiceName.class), any(HttpMethod.class)))
                    .willReturn(List.of(ep));
            given(assembler.toSpecResponse(ep)).willReturn(expectedResponse);

            // when
            Optional<EndpointPermissionSpecResponse> result = service.execute(query);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().requiredRoles()).containsExactly("ADMIN");
        }

        @Test
        @DisplayName("매칭되는 엔드포인트가 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNoMatch() {
            // given
            GetEndpointPermissionSpecQuery query =
                    new GetEndpointPermissionSpecQuery("auth-hub", "/api/v1/unknown", "GET");

            given(
                            readManager.findAllByServiceNameAndMethod(
                                    any(ServiceName.class), any(HttpMethod.class)))
                    .willReturn(List.of());

            // when
            Optional<EndpointPermissionSpecResponse> result = service.execute(query);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 후보 중 첫 번째 매칭되는 것을 반환한다")
        void shouldReturnFirstMatchingEndpoint() {
            // given
            EndpointPermission ep1 = EndpointPermissionFixture.createPublic("/api/v1/health");
            EndpointPermission ep2 = EndpointPermissionFixture.createPublic("/api/v1/users");
            GetEndpointPermissionSpecQuery query =
                    new GetEndpointPermissionSpecQuery("auth-hub", "/api/v1/users", "GET");
            EndpointPermissionSpecResponse expectedResponse =
                    new EndpointPermissionSpecResponse(true, Set.of(), Set.of());

            given(
                            readManager.findAllByServiceNameAndMethod(
                                    any(ServiceName.class), any(HttpMethod.class)))
                    .willReturn(List.of(ep1, ep2));
            given(assembler.toSpecResponse(ep2)).willReturn(expectedResponse);

            // when
            Optional<EndpointPermissionSpecResponse> result = service.execute(query);

            // then
            assertThat(result).isPresent();
        }
    }
}
