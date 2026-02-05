package com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SearchTenantServicesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response.TenantServiceApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.fixture.TenantServiceApiFixture;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServiceResult;
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
 * TenantServiceQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantServiceQueryApiMapper 단위 테스트")
class TenantServiceQueryApiMapperTest {

    private TenantServiceQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantServiceQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchTenantServicesOffsetApiRequest) 메서드는")
    class ToSearchParams {

        @Test
        @DisplayName("SearchTenantServicesOffsetApiRequest를 TenantServiceSearchParams로 변환한다")
        void shouldConvertToTenantServiceSearchParams() {
            // Given
            SearchTenantServicesOffsetApiRequest request =
                    new SearchTenantServicesOffsetApiRequest(
                            TenantServiceApiFixture.defaultTenantId(),
                            TenantServiceApiFixture.defaultServiceId(),
                            List.of("ACTIVE", "INACTIVE"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            0,
                            20);

            // When
            TenantServiceSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(TenantServiceApiFixture.defaultTenantId());
            assertThat(result.serviceId()).isEqualTo(TenantServiceApiFixture.defaultServiceId());
            assertThat(result.statuses()).containsExactly("ACTIVE", "INACTIVE");
            assertThat(result.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("subscribedAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필드가 null인 SearchTenantServicesOffsetApiRequest를 변환한다")
        void shouldConvertWhenAllFieldsAreNull() {
            // Given
            SearchTenantServicesOffsetApiRequest request =
                    new SearchTenantServicesOffsetApiRequest(null, null, null, null, null, 0, 20);

            // When
            TenantServiceSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.statuses()).isNull();
            assertThat(result.startDate()).isNull();
            assertThat(result.endDate()).isNull();
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
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
    @DisplayName("toResponse(TenantServiceResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("TenantServiceResult를 TenantServiceApiResponse로 변환한다")
        void shouldConvertToTenantServiceApiResponse() {
            // Given
            Instant fixedTime = TenantServiceApiFixture.fixedTime();
            TenantServiceResult result =
                    new TenantServiceResult(
                            TenantServiceApiFixture.defaultTenantServiceId(),
                            TenantServiceApiFixture.defaultTenantId(),
                            TenantServiceApiFixture.defaultServiceId(),
                            TenantServiceApiFixture.defaultStatus(),
                            fixedTime,
                            fixedTime,
                            fixedTime);

            // When
            TenantServiceApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantServiceId())
                    .isEqualTo(TenantServiceApiFixture.defaultTenantServiceId());
            assertThat(response.tenantId()).isEqualTo(TenantServiceApiFixture.defaultTenantId());
            assertThat(response.serviceId()).isEqualTo(TenantServiceApiFixture.defaultServiceId());
            assertThat(response.status()).isEqualTo(TenantServiceApiFixture.defaultStatus());
            assertThat(response.subscribedAt()).isNotNull();
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
            // DateTimeFormatUtils uses Asia/Seoul timezone, so 2025-01-01T00:00:00Z becomes
            // 2025-01-01T09:00:00+09:00
            assertThat(response.subscribedAt()).startsWith("2025-01-01");
            assertThat(response.createdAt()).startsWith("2025-01-01");
            assertThat(response.updatedAt()).startsWith("2025-01-01");
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
    @DisplayName("toResponses(List<TenantServiceResult>) 메서드는")
    class ToResponses {

        @Test
        @DisplayName("TenantServiceResult 목록을 TenantServiceApiResponse 목록으로 변환한다")
        void shouldConvertListToResponseList() {
            // Given
            Instant fixedTime = TenantServiceApiFixture.fixedTime();
            TenantServiceResult result1 =
                    new TenantServiceResult(
                            1L,
                            TenantServiceApiFixture.defaultTenantId(),
                            1L,
                            "ACTIVE",
                            fixedTime,
                            fixedTime,
                            fixedTime);
            TenantServiceResult result2 =
                    new TenantServiceResult(
                            2L,
                            TenantServiceApiFixture.defaultTenantId(),
                            2L,
                            "INACTIVE",
                            fixedTime,
                            fixedTime,
                            fixedTime);
            List<TenantServiceResult> results = List.of(result1, result2);

            // When
            List<TenantServiceApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isNotNull();
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).tenantServiceId()).isEqualTo(1L);
            assertThat(responses.get(0).status()).isEqualTo("ACTIVE");
            assertThat(responses.get(1).tenantServiceId()).isEqualTo(2L);
            assertThat(responses.get(1).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 리스트를 빈 응답 리스트로 변환한다")
        void shouldConvertEmptyList() {
            // Given
            List<TenantServiceResult> results = List.of();

            // When
            List<TenantServiceApiResponse> responses = mapper.toResponses(results);

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
    @DisplayName("toPageResponse(TenantServicePageResult) 메서드는")
    class ToPageResponse {

        @Test
        @DisplayName("TenantServicePageResult를 PageApiResponse로 변환한다")
        void shouldConvertToPageApiResponse() {
            // Given
            Instant fixedTime = TenantServiceApiFixture.fixedTime();
            TenantServiceResult result =
                    new TenantServiceResult(
                            TenantServiceApiFixture.defaultTenantServiceId(),
                            TenantServiceApiFixture.defaultTenantId(),
                            TenantServiceApiFixture.defaultServiceId(),
                            TenantServiceApiFixture.defaultStatus(),
                            fixedTime,
                            fixedTime,
                            fixedTime);
            TenantServicePageResult pageResult =
                    TenantServicePageResult.of(List.of(result), 0, 20, 1L);

            // When
            PageApiResponse<TenantServiceApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).tenantServiceId())
                    .isEqualTo(TenantServiceApiFixture.defaultTenantServiceId());
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1L);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("빈 결과를 가진 TenantServicePageResult를 변환한다")
        void shouldConvertEmptyPageResult() {
            // Given
            TenantServicePageResult pageResult = TenantServicePageResult.empty(20);

            // When
            PageApiResponse<TenantServiceApiResponse> response = mapper.toPageResponse(pageResult);

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
        @DisplayName("여러 페이지를 가진 TenantServicePageResult를 변환한다")
        void shouldConvertMultiPageResult() {
            // Given
            Instant fixedTime = TenantServiceApiFixture.fixedTime();
            List<TenantServiceResult> results =
                    List.of(
                            new TenantServiceResult(
                                    1L,
                                    TenantServiceApiFixture.defaultTenantId(),
                                    1L,
                                    "ACTIVE",
                                    fixedTime,
                                    fixedTime,
                                    fixedTime),
                            new TenantServiceResult(
                                    2L,
                                    TenantServiceApiFixture.defaultTenantId(),
                                    2L,
                                    "INACTIVE",
                                    fixedTime,
                                    fixedTime,
                                    fixedTime));
            TenantServicePageResult pageResult = TenantServicePageResult.of(results, 1, 2, 5L);

            // When
            PageApiResponse<TenantServiceApiResponse> response = mapper.toPageResponse(pageResult);

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
