package com.ryuqq.authhub.adapter.in.rest.permission.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.SearchPermissionsOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.fixture.PermissionApiFixture;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResult;
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
 * PermissionQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionQueryApiMapper 단위 테스트")
class PermissionQueryApiMapperTest {

    private PermissionQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchPermissionsOffsetApiRequest) 메서드는")
    class ToSearchParams {

        @Test
        @DisplayName("SearchPermissionsOffsetApiRequest를 PermissionSearchParams로 변환한다")
        void shouldConvertToPermissionSearchParams() {
            // Given
            SearchPermissionsOffsetApiRequest request =
                    new SearchPermissionsOffsetApiRequest(
                            null,
                            "user",
                            "RESOURCE",
                            List.of("CUSTOM"),
                            List.of("user", "role"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            0,
                            20);

            // When
            PermissionSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.searchWord()).isEqualTo("user");
            assertThat(result.searchField()).isEqualTo("RESOURCE");
            assertThat(result.types()).containsExactly("CUSTOM");
            assertThat(result.resources()).containsExactly("user", "role");
            assertThat(result.startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필드가 null인 SearchPermissionsOffsetApiRequest를 변환한다")
        void shouldConvertWhenAllFieldsAreNull() {
            // Given
            SearchPermissionsOffsetApiRequest request =
                    new SearchPermissionsOffsetApiRequest(
                            null, null, null, null, null, null, null, 0, 20);

            // When
            PermissionSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.types()).isNull();
            assertThat(result.resources()).isNull();
            assertThat(result.startDate()).isNull();
            assertThat(result.endDate()).isNull();
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("serviceId가 있는 SearchPermissionsOffsetApiRequest를 변환한다")
        void shouldConvertWhenServiceIdIsPresent() {
            // Given
            Long serviceId = 1L;
            SearchPermissionsOffsetApiRequest request =
                    new SearchPermissionsOffsetApiRequest(
                            serviceId, null, null, null, null, null, null, 0, 20);

            // When
            PermissionSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isEqualTo(serviceId);
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
    @DisplayName("toResponse(PermissionResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("PermissionResult를 PermissionApiResponse로 변환한다")
        void shouldConvertToPermissionApiResponse() {
            // Given
            Instant fixedTime = PermissionApiFixture.fixedTime();
            PermissionResult result =
                    new PermissionResult(
                            PermissionApiFixture.defaultPermissionId(),
                            null,
                            PermissionApiFixture.defaultPermissionKey(),
                            PermissionApiFixture.defaultResource(),
                            PermissionApiFixture.defaultAction(),
                            PermissionApiFixture.defaultDescription(),
                            PermissionApiFixture.defaultType(),
                            fixedTime,
                            fixedTime);

            // When
            PermissionApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.permissionId())
                    .isEqualTo(PermissionApiFixture.defaultPermissionId());
            assertThat(response.serviceId()).isNull();
            assertThat(response.permissionKey())
                    .isEqualTo(PermissionApiFixture.defaultPermissionKey());
            assertThat(response.resource()).isEqualTo(PermissionApiFixture.defaultResource());
            assertThat(response.action()).isEqualTo(PermissionApiFixture.defaultAction());
            assertThat(response.description()).isEqualTo(PermissionApiFixture.defaultDescription());
            assertThat(response.type()).isEqualTo(PermissionApiFixture.defaultType());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
            // DateTimeFormatUtils uses Asia/Seoul timezone, so 2025-01-01T00:00:00Z becomes
            // 2025-01-01T09:00:00+09:00
            assertThat(response.createdAt()).startsWith("2025-01-01");
            assertThat(response.updatedAt()).startsWith("2025-01-01");
        }

        @Test
        @DisplayName("serviceId가 있는 PermissionResult를 변환한다")
        void shouldConvertWhenServiceIdIsPresent() {
            // Given
            Long serviceId = 1L;
            Instant fixedTime = PermissionApiFixture.fixedTime();
            PermissionResult result =
                    new PermissionResult(
                            PermissionApiFixture.defaultPermissionId(),
                            serviceId,
                            PermissionApiFixture.defaultPermissionKey(),
                            PermissionApiFixture.defaultResource(),
                            PermissionApiFixture.defaultAction(),
                            PermissionApiFixture.defaultDescription(),
                            PermissionApiFixture.defaultType(),
                            fixedTime,
                            fixedTime);

            // When
            PermissionApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.serviceId()).isEqualTo(serviceId);
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
    @DisplayName("toResponses(List<PermissionResult>) 메서드는")
    class ToResponses {

        @Test
        @DisplayName("PermissionResult 목록을 PermissionApiResponse 목록으로 변환한다")
        void shouldConvertListToResponseList() {
            // Given
            Instant fixedTime = PermissionApiFixture.fixedTime();
            PermissionResult result1 =
                    new PermissionResult(
                            1L,
                            null,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            "CUSTOM",
                            fixedTime,
                            fixedTime);
            PermissionResult result2 =
                    new PermissionResult(
                            2L,
                            null,
                            "user:write",
                            "user",
                            "write",
                            "사용자 수정 권한",
                            "CUSTOM",
                            fixedTime,
                            fixedTime);
            List<PermissionResult> results = List.of(result1, result2);

            // When
            List<PermissionApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isNotNull();
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).permissionId()).isEqualTo(1L);
            assertThat(responses.get(0).permissionKey()).isEqualTo("user:read");
            assertThat(responses.get(1).permissionId()).isEqualTo(2L);
            assertThat(responses.get(1).permissionKey()).isEqualTo("user:write");
        }

        @Test
        @DisplayName("빈 리스트를 빈 응답 리스트로 변환한다")
        void shouldConvertEmptyList() {
            // Given
            List<PermissionResult> results = List.of();

            // When
            List<PermissionApiResponse> responses = mapper.toResponses(results);

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
    @DisplayName("toPageResponse(PermissionPageResult) 메서드는")
    class ToPageResponse {

        @Test
        @DisplayName("PermissionPageResult를 PageApiResponse로 변환한다")
        void shouldConvertToPageApiResponse() {
            // Given
            Instant fixedTime = PermissionApiFixture.fixedTime();
            PermissionResult result =
                    new PermissionResult(
                            PermissionApiFixture.defaultPermissionId(),
                            null,
                            PermissionApiFixture.defaultPermissionKey(),
                            PermissionApiFixture.defaultResource(),
                            PermissionApiFixture.defaultAction(),
                            PermissionApiFixture.defaultDescription(),
                            PermissionApiFixture.defaultType(),
                            fixedTime,
                            fixedTime);
            PermissionPageResult pageResult = PermissionPageResult.of(List.of(result), 0, 20, 1L);

            // When
            PageApiResponse<PermissionApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).permissionId())
                    .isEqualTo(PermissionApiFixture.defaultPermissionId());
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1L);
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("빈 결과를 가진 PermissionPageResult를 변환한다")
        void shouldConvertEmptyPageResult() {
            // Given
            PermissionPageResult pageResult = PermissionPageResult.empty(20);

            // When
            PageApiResponse<PermissionApiResponse> response = mapper.toPageResponse(pageResult);

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
        @DisplayName("여러 페이지를 가진 PermissionPageResult를 변환한다")
        void shouldConvertMultiPageResult() {
            // Given
            Instant fixedTime = PermissionApiFixture.fixedTime();
            List<PermissionResult> results =
                    List.of(
                            new PermissionResult(
                                    1L,
                                    null,
                                    "user:read",
                                    "user",
                                    "read",
                                    "설명1",
                                    "CUSTOM",
                                    fixedTime,
                                    fixedTime),
                            new PermissionResult(
                                    2L,
                                    null,
                                    "user:write",
                                    "user",
                                    "write",
                                    "설명2",
                                    "CUSTOM",
                                    fixedTime,
                                    fixedTime));
            PermissionPageResult pageResult = PermissionPageResult.of(results, 1, 2, 5L);

            // When
            PageApiResponse<PermissionApiResponse> response = mapper.toPageResponse(pageResult);

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
