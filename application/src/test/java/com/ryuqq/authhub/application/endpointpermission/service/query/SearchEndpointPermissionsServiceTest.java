package com.ryuqq.authhub.application.endpointpermission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.endpointpermission.assembler.EndpointPermissionAssembler;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.manager.query.EndpointPermissionReadManager;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchEndpointPermissionsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchEndpointPermissionsService 단위 테스트")
class SearchEndpointPermissionsServiceTest {

    @Mock private EndpointPermissionReadManager readManager;
    @Mock private EndpointPermissionAssembler assembler;

    private SearchEndpointPermissionsService service;

    @BeforeEach
    void setUp() {
        service = new SearchEndpointPermissionsService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("엔드포인트 권한 목록을 성공적으로 검색한다")
        void shouldSearchEndpointPermissionsSuccessfully() {
            // given
            EndpointPermission ep1 = EndpointPermissionFixture.createPublic("/api/v1/health");
            EndpointPermission ep2 =
                    EndpointPermissionFixture.createProtected("/api/v1/users", "user:read");
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("auth-hub", null, null, null, 0, 20);
            EndpointPermissionResponse response1 = createResponse(ep1);
            EndpointPermissionResponse response2 = createResponse(ep2);

            given(readManager.search(query)).willReturn(List.of(ep1, ep2));
            given(assembler.toResponse(ep1)).willReturn(response1);
            given(assembler.toResponse(ep2)).willReturn(response2);

            // when
            List<EndpointPermissionResponse> result = service.execute(query);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).path()).isEqualTo("/api/v1/health");
            assertThat(result.get(1).path()).isEqualTo("/api/v1/users");
        }

        @Test
        @DisplayName("조건에 맞는 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery(
                            "non-existing-service", null, null, null, 0, 20);

            given(readManager.search(query)).willReturn(List.of());

            // when
            List<EndpointPermissionResponse> result = service.execute(query);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("HTTP 메서드로 필터링하여 검색한다")
        void shouldSearchByHttpMethod() {
            // given
            EndpointPermission ep =
                    EndpointPermissionFixture.createPublic("/api/v1/test", HttpMethod.POST);
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery(null, null, "POST", null, 0, 20);
            EndpointPermissionResponse response = createResponse(ep);

            given(readManager.search(query)).willReturn(List.of(ep));
            given(assembler.toResponse(ep)).willReturn(response);

            // when
            List<EndpointPermissionResponse> result = service.execute(query);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).method()).isEqualTo("POST");
        }
    }

    @Nested
    @DisplayName("count 메서드")
    class CountTest {

        @Test
        @DisplayName("검색 조건에 맞는 총 개수를 반환한다")
        void shouldReturnTotalCount() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery("auth-hub", null, null, null, 0, 20);

            given(readManager.count(query)).willReturn(100L);

            // when
            long count = service.count(query);

            // then
            assertThat(count).isEqualTo(100L);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환한다")
        void shouldReturnZeroWhenNoResults() {
            // given
            SearchEndpointPermissionsQuery query =
                    new SearchEndpointPermissionsQuery(
                            "non-existing-service", null, null, null, 0, 20);

            given(readManager.count(query)).willReturn(0L);

            // when
            long count = service.count(query);

            // then
            assertThat(count).isZero();
        }
    }

    private EndpointPermissionResponse createResponse(EndpointPermission ep) {
        return new EndpointPermissionResponse(
                ep.endpointPermissionIdValue().toString(),
                ep.serviceNameValue(),
                ep.pathValue(),
                ep.methodValue(),
                ep.descriptionValue(),
                ep.isPublic(),
                ep.requiredPermissionValues(),
                ep.requiredRoleValues(),
                ep.getVersion(),
                ep.createdAt(),
                ep.updatedAt());
    }
}
