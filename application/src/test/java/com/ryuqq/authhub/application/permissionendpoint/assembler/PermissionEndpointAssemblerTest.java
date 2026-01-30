package com.ryuqq.authhub.application.permissionendpoint.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointResult;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointAssembler 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointAssembler 단위 테스트")
class PermissionEndpointAssemblerTest {

    private PermissionEndpointAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공: Domain의 모든 필드가 Result로 올바르게 매핑됨")
        void shouldMapAllFields_FromDomainToResult() {
            // given
            PermissionEndpoint permissionEndpoint = PermissionEndpointFixture.create();

            // when
            PermissionEndpointResult result = sut.toResult(permissionEndpoint);

            // then
            assertThat(result.permissionEndpointId())
                    .isEqualTo(permissionEndpoint.permissionEndpointIdValue());
            assertThat(result.permissionId()).isEqualTo(permissionEndpoint.permissionIdValue());
            assertThat(result.urlPattern()).isEqualTo(permissionEndpoint.urlPatternValue());
            assertThat(result.httpMethod()).isEqualTo(permissionEndpoint.httpMethodValue());
            assertThat(result.description()).isEqualTo(permissionEndpoint.descriptionValue());
            assertThat(result.createdAt()).isEqualTo(permissionEndpoint.createdAt());
            assertThat(result.updatedAt()).isEqualTo(permissionEndpoint.updatedAt());
        }
    }

    @Nested
    @DisplayName("toResultList 메서드")
    class ToResultList {

        @Test
        @DisplayName("성공: Domain 목록이 Result 목록으로 올바르게 변환됨")
        void shouldMapAllPermissionEndpoints_ToResultList() {
            // given
            PermissionEndpoint ep1 = PermissionEndpointFixture.createGetEndpoint();
            PermissionEndpoint ep2 = PermissionEndpointFixture.createPostEndpoint();
            List<PermissionEndpoint> permissionEndpoints = List.of(ep1, ep2);

            // when
            List<PermissionEndpointResult> results = sut.toResultList(permissionEndpoints);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).httpMethod()).isEqualTo("GET");
            assertThat(results.get(1).httpMethod()).isEqualTo("POST");
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // when
            List<PermissionEndpointResult> results = sut.toResultList(Collections.emptyList());

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult 메서드")
    class ToPageResult {

        @Test
        @DisplayName("성공: Domain 목록과 페이징 정보가 PageResult로 올바르게 변환됨")
        void shouldCreatePageResult_WithCorrectPagination() {
            // given
            PermissionEndpoint ep = PermissionEndpointFixture.create();
            List<PermissionEndpoint> permissionEndpoints = List.of(ep);
            int page = 0;
            int size = 10;
            long totalElements = 25L;

            // when
            PermissionEndpointPageResult result =
                    sut.toPageResult(permissionEndpoints, page, size, totalElements);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }

    @Nested
    @DisplayName("toSpecListResult 메서드")
    class ToSpecListResult {

        @Test
        @DisplayName("성공: EndpointPermissionSpecResult 목록을 EndpointPermissionSpecListResult로 변환")
        void shouldCreateSpecListResult() {
            // given
            List<EndpointPermissionSpecResult> specs =
                    List.of(
                            new EndpointPermissionSpecResult(
                                    1L, 1L, "user:read", "/api/v1/users", "GET"));

            // when
            EndpointPermissionSpecListResult result = sut.toSpecListResult(specs);

            // then
            assertThat(result.endpoints()).hasSize(1);
            assertThat(result.endpoints().get(0).urlPattern()).isEqualTo("/api/v1/users");
        }
    }
}
