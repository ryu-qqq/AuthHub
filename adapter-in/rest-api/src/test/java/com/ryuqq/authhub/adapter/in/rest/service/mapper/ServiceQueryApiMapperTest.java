package com.ryuqq.authhub.adapter.in.rest.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.SearchServicesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.response.ServiceApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.fixture.ServiceApiFixture;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.dto.response.ServiceResult;
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
 * ServiceQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ServiceQueryApiMapper 단위 테스트")
class ServiceQueryApiMapperTest {

    private ServiceQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchServicesOffsetApiRequest) 메서드는")
    class ToSearchParams {

        @Test
        @DisplayName("SearchServicesOffsetApiRequest를 ServiceSearchParams로 변환한다")
        void shouldConvertToServiceSearchParams() {
            // Given
            SearchServicesOffsetApiRequest request =
                    new SearchServicesOffsetApiRequest(
                            "STORE",
                            "SERVICE_CODE",
                            List.of("ACTIVE", "INACTIVE"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            0,
                            20);

            // When
            ServiceSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("STORE");
            assertThat(result.searchField()).isEqualTo("SERVICE_CODE");
            assertThat(result.statuses()).containsExactly("ACTIVE", "INACTIVE");
            assertThat(result.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필드가 null인 SearchServicesOffsetApiRequest를 변환한다")
        void shouldConvertWhenAllFieldsAreNull() {
            // Given
            SearchServicesOffsetApiRequest request =
                    new SearchServicesOffsetApiRequest(null, null, null, null, null, 0, 20);

            // When
            ServiceSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isNull();
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
    @DisplayName("toResponse(ServiceResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("ServiceResult를 ServiceApiResponse로 변환한다")
        void shouldConvertToServiceApiResponse() {
            // Given
            Instant fixedTime = ServiceApiFixture.fixedTime();
            ServiceResult result =
                    new ServiceResult(
                            ServiceApiFixture.defaultServiceId(),
                            ServiceApiFixture.defaultServiceCode(),
                            ServiceApiFixture.defaultName(),
                            ServiceApiFixture.defaultDescription(),
                            ServiceApiFixture.defaultStatus(),
                            fixedTime,
                            fixedTime);

            // When
            ServiceApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.serviceId()).isEqualTo(ServiceApiFixture.defaultServiceId());
            assertThat(response.serviceCode()).isEqualTo(ServiceApiFixture.defaultServiceCode());
            assertThat(response.name()).isEqualTo(ServiceApiFixture.defaultName());
            assertThat(response.description()).isEqualTo(ServiceApiFixture.defaultDescription());
            assertThat(response.status()).isEqualTo(ServiceApiFixture.defaultStatus());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
            // DateTimeFormatUtils uses Asia/Seoul timezone, so 2025-01-01T00:00:00Z becomes
            // 2025-01-01T09:00:00+09:00
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
    @DisplayName("toResponses(List<ServiceResult>) 메서드는")
    class ToResponses {

        @Test
        @DisplayName("ServiceResult 목록을 ServiceApiResponse 목록으로 변환한다")
        void shouldConvertListToResponseList() {
            // Given
            Instant fixedTime = ServiceApiFixture.fixedTime();
            ServiceResult result1 =
                    new ServiceResult(
                            1L, "SVC_STORE", "자사몰", "자사몰 서비스", "ACTIVE", fixedTime, fixedTime);
            ServiceResult result2 =
                    new ServiceResult(
                            2L, "SVC_MARKET", "마켓", "마켓 서비스", "INACTIVE", fixedTime, fixedTime);
            List<ServiceResult> results = List.of(result1, result2);

            // When
            List<ServiceApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isNotNull();
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).serviceId()).isEqualTo(1L);
            assertThat(responses.get(0).serviceCode()).isEqualTo("SVC_STORE");
            assertThat(responses.get(0).status()).isEqualTo("ACTIVE");
            assertThat(responses.get(1).serviceId()).isEqualTo(2L);
            assertThat(responses.get(1).serviceCode()).isEqualTo("SVC_MARKET");
            assertThat(responses.get(1).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 리스트를 빈 응답 리스트로 변환한다")
        void shouldConvertEmptyList() {
            // Given
            List<ServiceResult> results = List.of();

            // When
            List<ServiceApiResponse> responses = mapper.toResponses(results);

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
    @DisplayName("toPageResponse(ServicePageResult) 메서드는")
    class ToPageResponse {

        @Test
        @DisplayName("ServicePageResult를 PageApiResponse로 변환한다")
        void shouldConvertToPageApiResponse() {
            // Given
            Instant fixedTime = ServiceApiFixture.fixedTime();
            ServiceResult result =
                    new ServiceResult(
                            ServiceApiFixture.defaultServiceId(),
                            ServiceApiFixture.defaultServiceCode(),
                            ServiceApiFixture.defaultName(),
                            ServiceApiFixture.defaultDescription(),
                            ServiceApiFixture.defaultStatus(),
                            fixedTime,
                            fixedTime);
            ServicePageResult pageResult = ServicePageResult.of(List.of(result), 0, 20, 1L);

            // When
            PageApiResponse<ServiceApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).serviceId())
                    .isEqualTo(ServiceApiFixture.defaultServiceId());
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1L);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("빈 결과를 가진 ServicePageResult를 변환한다")
        void shouldConvertEmptyPageResult() {
            // Given
            ServicePageResult pageResult = ServicePageResult.empty(20);

            // When
            PageApiResponse<ServiceApiResponse> response = mapper.toPageResponse(pageResult);

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
        @DisplayName("여러 페이지를 가진 ServicePageResult를 변환한다")
        void shouldConvertMultiPageResult() {
            // Given
            Instant fixedTime = ServiceApiFixture.fixedTime();
            List<ServiceResult> results =
                    List.of(
                            new ServiceResult(
                                    1L,
                                    "SVC_STORE",
                                    "자사몰",
                                    "자사몰 서비스",
                                    "ACTIVE",
                                    fixedTime,
                                    fixedTime),
                            new ServiceResult(
                                    2L,
                                    "SVC_MARKET",
                                    "마켓",
                                    "마켓 서비스",
                                    "INACTIVE",
                                    fixedTime,
                                    fixedTime));
            ServicePageResult pageResult = ServicePageResult.of(results, 1, 2, 5L);

            // When
            PageApiResponse<ServiceApiResponse> response = mapper.toPageResponse(pageResult);

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
