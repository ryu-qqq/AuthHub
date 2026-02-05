package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.SearchPermissionEndpointsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.response.PermissionEndpointApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.fixture.PermissionEndpointApiFixture;
import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointResult;
import com.ryuqq.authhub.domain.common.vo.PageMeta;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionEndpointQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionEndpointQueryApiMapper 단위 테스트")
class PermissionEndpointQueryApiMapperTest {

    private PermissionEndpointQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionEndpointQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchPermissionEndpointsApiRequest) 메서드는")
    class ToSearchParams {

        @Test
        @DisplayName("SearchPermissionEndpointsApiRequest를 PermissionEndpointSearchParams로 변환한다")
        void shouldConvertToPermissionEndpointSearchParams() {
            // Given
            SearchPermissionEndpointsApiRequest request =
                    new SearchPermissionEndpointsApiRequest(
                            List.of(1L, 2L),
                            "users",
                            "URL_PATTERN",
                            List.of("GET", "POST"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            0,
                            20);

            // When
            PermissionEndpointSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionIds()).containsExactly(1L, 2L);
            assertThat(result.searchWord()).isEqualTo("users");
            assertThat(result.searchField()).isEqualTo("URL_PATTERN");
            assertThat(result.httpMethods()).containsExactly("GET", "POST");
            assertThat(result.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필드가 null인 SearchPermissionEndpointsApiRequest를 변환한다")
        void shouldConvertWhenAllFieldsAreNull() {
            // Given
            SearchPermissionEndpointsApiRequest request =
                    new SearchPermissionEndpointsApiRequest(
                            null, null, null, null, null, null, 0, 20);

            // When
            PermissionEndpointSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionIds()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.httpMethods()).isNull();
            assertThat(result.startDate()).isNull();
            assertThat(result.endDate()).isNull();
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("permissionIds가 빈 리스트인 경우 null로 변환한다")
        void shouldConvertEmptyPermissionIds() {
            // Given
            SearchPermissionEndpointsApiRequest request =
                    new SearchPermissionEndpointsApiRequest(
                            List.of(), null, null, null, null, null, 0, 20);

            // When
            PermissionEndpointSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionIds()).isEmpty();
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toSearchParams(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toResponse(PermissionEndpointResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("PermissionEndpointResult를 PermissionEndpointApiResponse로 변환한다")
        void shouldConvertToPermissionEndpointApiResponse() {
            // Given
            Instant fixedTime = PermissionEndpointApiFixture.fixedTime();
            PermissionEndpointResult result =
                    new PermissionEndpointResult(
                            PermissionEndpointApiFixture.defaultPermissionEndpointId(),
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic(),
                            fixedTime,
                            fixedTime);

            // When
            PermissionEndpointApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.permissionEndpointId())
                    .isEqualTo(PermissionEndpointApiFixture.defaultPermissionEndpointId());
            assertThat(response.permissionId())
                    .isEqualTo(PermissionEndpointApiFixture.defaultPermissionId());
            assertThat(response.serviceName())
                    .isEqualTo(PermissionEndpointApiFixture.defaultServiceName());
            assertThat(response.urlPattern())
                    .isEqualTo(PermissionEndpointApiFixture.defaultUrlPattern());
            assertThat(response.httpMethod())
                    .isEqualTo(PermissionEndpointApiFixture.defaultHttpMethod());
            assertThat(response.description())
                    .isEqualTo(PermissionEndpointApiFixture.defaultDescription());
            assertThat(response.isPublic())
                    .isEqualTo(PermissionEndpointApiFixture.defaultIsPublic());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
            // DateTimeFormatUtils uses Asia/Seoul timezone, so 2025-01-01T00:00:00Z becomes
            // 2025-01-01T09:00:00+09:00
            assertThat(response.createdAt()).startsWith("2025-01-01");
            assertThat(response.updatedAt()).startsWith("2025-01-01");
        }

        @Test
        @DisplayName("description이 null인 PermissionEndpointResult를 변환한다")
        void shouldConvertWhenDescriptionIsNull() {
            // Given
            Instant fixedTime = PermissionEndpointApiFixture.fixedTime();
            PermissionEndpointResult result =
                    new PermissionEndpointResult(
                            PermissionEndpointApiFixture.defaultPermissionEndpointId(),
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            null,
                            PermissionEndpointApiFixture.defaultIsPublic(),
                            fixedTime,
                            fixedTime);

            // When
            PermissionEndpointApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.description()).isNull();
        }

        @Test
        @DisplayName("result가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResultIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toResponses(List<PermissionEndpointResult>) 메서드는")
    class ToResponses {

        @Test
        @DisplayName("PermissionEndpointResult 목록을 PermissionEndpointApiResponse 목록으로 변환한다")
        void shouldConvertListToResponseList() {
            // Given
            Instant fixedTime = PermissionEndpointApiFixture.fixedTime();
            PermissionEndpointResult result1 =
                    new PermissionEndpointResult(
                            1L,
                            1L,
                            "authhub",
                            "/api/v1/users/{id}",
                            "GET",
                            "사용자 조회",
                            false,
                            fixedTime,
                            fixedTime);
            PermissionEndpointResult result2 =
                    new PermissionEndpointResult(
                            2L,
                            2L,
                            "authhub",
                            "/api/v1/users",
                            "POST",
                            "사용자 생성",
                            false,
                            fixedTime,
                            fixedTime);
            List<PermissionEndpointResult> results = List.of(result1, result2);

            // When
            List<PermissionEndpointApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isNotNull();
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).permissionEndpointId()).isEqualTo(1L);
            assertThat(responses.get(0).urlPattern()).isEqualTo("/api/v1/users/{id}");
            assertThat(responses.get(1).permissionEndpointId()).isEqualTo(2L);
            assertThat(responses.get(1).urlPattern()).isEqualTo("/api/v1/users");
        }

        @Test
        @DisplayName("빈 리스트를 빈 응답 리스트로 변환한다")
        void shouldConvertEmptyList() {
            // Given
            List<PermissionEndpointResult> results = List.of();

            // When
            List<PermissionEndpointApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isNotNull();
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("results가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResultsIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toResponses(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toPageResponse(PermissionEndpointPageResult) 메서드는")
    class ToPageResponse {

        @Test
        @DisplayName("PermissionEndpointPageResult를 PageApiResponse로 변환한다")
        void shouldConvertToPageApiResponse() {
            // Given
            Instant fixedTime = PermissionEndpointApiFixture.fixedTime();
            PermissionEndpointResult result =
                    new PermissionEndpointResult(
                            PermissionEndpointApiFixture.defaultPermissionEndpointId(),
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            PermissionEndpointApiFixture.defaultIsPublic(),
                            fixedTime,
                            fixedTime);
            PageMeta pageMeta = new PageMeta(0, 20, 1L, 1);
            PermissionEndpointPageResult pageResult =
                    new PermissionEndpointPageResult(List.of(result), pageMeta);

            // When
            PageApiResponse<PermissionEndpointApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).permissionEndpointId())
                    .isEqualTo(PermissionEndpointApiFixture.defaultPermissionEndpointId());
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1L);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("빈 결과를 가진 PermissionEndpointPageResult를 변환한다")
        void shouldConvertEmptyPageResult() {
            // Given
            PageMeta pageMeta = new PageMeta(0, 20, 0L, 0);
            PermissionEndpointPageResult pageResult =
                    new PermissionEndpointPageResult(List.of(), pageMeta);

            // When
            PageApiResponse<PermissionEndpointApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(0L);
            assertThat(response.totalPages()).isEqualTo(0);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("여러 페이지를 가진 PermissionEndpointPageResult를 변환한다")
        void shouldConvertMultiPageResult() {
            // Given
            Instant fixedTime = PermissionEndpointApiFixture.fixedTime();
            List<PermissionEndpointResult> results =
                    List.of(
                            new PermissionEndpointResult(
                                    1L,
                                    1L,
                                    "authhub",
                                    "/api/v1/users/{id}",
                                    "GET",
                                    "설명1",
                                    false,
                                    fixedTime,
                                    fixedTime),
                            new PermissionEndpointResult(
                                    2L,
                                    2L,
                                    "authhub",
                                    "/api/v1/users",
                                    "POST",
                                    "설명2",
                                    false,
                                    fixedTime,
                                    fixedTime));
            PageMeta pageMeta = new PageMeta(1, 2, 5L, 3);
            PermissionEndpointPageResult pageResult =
                    new PermissionEndpointPageResult(results, pageMeta);

            // When
            PageApiResponse<PermissionEndpointApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(2);
            assertThat(response.totalElements()).isEqualTo(5L);
            assertThat(response.totalPages()).isEqualTo(3);
            assertThat(response.first()).isFalse();
            assertThat(response.last()).isFalse();
        }

        @Test
        @DisplayName("pageResult가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenPageResultIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toPageResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
