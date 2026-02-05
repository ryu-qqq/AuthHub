package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.SearchTenantsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.fixture.TenantApiFixture;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantQueryApiMapper 단위 테스트")
class TenantQueryApiMapperTest {

    private TenantQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchTenantsOffsetApiRequest) 메서드는")
    class ToSearchParams {

        @Test
        @DisplayName("SearchTenantsOffsetApiRequest를 TenantSearchParams로 변환한다")
        void shouldConvertToTenantSearchParams() {
            // Given
            SearchTenantsOffsetApiRequest request =
                    new SearchTenantsOffsetApiRequest(
                            "테넌트",
                            "NAME",
                            List.of("ACTIVE", "INACTIVE"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            0,
                            20);

            // When
            TenantSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("테넌트");
            assertThat(result.searchField()).isEqualTo("NAME");
            assertThat(result.statuses()).containsExactly("ACTIVE", "INACTIVE");
            assertThat(result.searchParams().startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.searchParams().endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(result.searchParams().page()).isEqualTo(0);
            assertThat(result.searchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 값이 포함된 요청도 처리한다")
        void shouldHandleNullValues() {
            // Given
            SearchTenantsOffsetApiRequest request =
                    new SearchTenantsOffsetApiRequest(null, null, null, null, null, null, null);

            // When
            TenantSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.statuses()).isNull();
            assertThat(result.searchParams().startDate()).isNull();
            assertThat(result.searchParams().endDate()).isNull();
            // CommonSearchParams에서 기본값 처리
            assertThat(result.searchParams().page()).isNotNull();
            assertThat(result.searchParams().size()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResponse(TenantResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("TenantResult를 TenantApiResponse로 변환한다")
        void shouldConvertToTenantApiResponse() {
            // Given
            TenantResult result = TenantApiFixture.tenantResult();

            // When
            TenantApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantId()).isEqualTo(TenantApiFixture.defaultTenantIdString());
            assertThat(response.name()).isEqualTo(TenantApiFixture.defaultTenantName());
            assertThat(response.status()).isEqualTo(TenantApiFixture.defaultStatus());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
            // DateTimeFormatUtils를 사용하여 ISO 8601 형식으로 변환되었는지 확인
            assertThat(response.createdAt()).contains("2025-01-01");
            assertThat(response.updatedAt()).contains("2025-01-01");
        }
    }

    @Nested
    @DisplayName("toResponses(List<TenantResult>) 메서드는")
    class ToResponses {

        @Test
        @DisplayName("TenantResult 리스트를 TenantApiResponse 리스트로 변환한다")
        void shouldConvertListToResponseList() {
            // Given
            TenantResult result1 = TenantApiFixture.tenantResult();
            TenantResult result2 =
                    TenantApiFixture.tenantResult(
                            "550e8400-e29b-41d4-a716-446655440001", "테넌트2", "INACTIVE");
            List<TenantResult> results = List.of(result1, result2);

            // When
            List<TenantApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).tenantId())
                    .isEqualTo(TenantApiFixture.defaultTenantIdString());
            assertThat(responses.get(0).name()).isEqualTo(TenantApiFixture.defaultTenantName());
            assertThat(responses.get(1).tenantId())
                    .isEqualTo("550e8400-e29b-41d4-a716-446655440001");
            assertThat(responses.get(1).name()).isEqualTo("테넌트2");
            assertThat(responses.get(1).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 리스트를 처리한다")
        void shouldHandleEmptyList() {
            // Given
            List<TenantResult> results = Collections.emptyList();

            // When
            List<TenantApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse(TenantPageResult) 메서드는")
    class ToPageResponse {

        @Test
        @DisplayName("TenantPageResult를 PageApiResponse로 변환한다")
        void shouldConvertToPageResponse() {
            // Given
            TenantResult result = TenantApiFixture.tenantResult();
            TenantPageResult pageResult = TenantPageResult.of(List.of(result), 0, 20, 1L);

            // When
            PageApiResponse<TenantApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).tenantId())
                    .isEqualTo(TenantApiFixture.defaultTenantIdString());
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1L);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("빈 페이지 결과를 처리한다")
        void shouldHandleEmptyPageResult() {
            // Given
            TenantPageResult pageResult = TenantPageResult.empty(20);

            // When
            PageApiResponse<TenantApiResponse> response = mapper.toPageResponse(pageResult);

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
        @DisplayName("여러 페이지 중간 페이지를 처리한다")
        void shouldHandleMiddlePage() {
            // Given
            TenantResult result1 = TenantApiFixture.tenantResult();
            TenantResult result2 =
                    TenantApiFixture.tenantResult(
                            "550e8400-e29b-41d4-a716-446655440001", "테넌트2", "INACTIVE");
            TenantPageResult pageResult =
                    TenantPageResult.of(List.of(result1, result2), 1, 20, 50L);

            // When
            PageApiResponse<TenantApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(50L);
            assertThat(response.totalPages()).isEqualTo(3);
            assertThat(response.first()).isFalse();
            assertThat(response.last()).isFalse();
        }
    }
}
