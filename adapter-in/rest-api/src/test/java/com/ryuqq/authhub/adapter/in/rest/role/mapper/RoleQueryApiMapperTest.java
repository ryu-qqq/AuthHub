package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.SearchRolesOffsetApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.fixture.RoleApiFixture;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.dto.response.RoleResult;
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
 * RoleQueryApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RoleQueryApiMapper 단위 테스트")
class RoleQueryApiMapperTest {

    private RoleQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RoleQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchRolesOffsetApiRequest) 메서드는")
    class ToSearchParams {

        @Test
        @DisplayName("SearchRolesOffsetApiRequest를 RoleSearchParams로 변환한다")
        void shouldConvertToRoleSearchParams() {
            // Given
            var request =
                    new SearchRolesOffsetApiRequest(
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            "USER",
                            "NAME",
                            List.of("CUSTOM"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            0,
                            20);

            // When
            RoleSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.searchParams()).isNotNull();
            assertThat(result.tenantId()).isEqualTo(RoleApiFixture.defaultTenantId());
            assertThat(result.serviceId()).isEqualTo(RoleApiFixture.defaultServiceId());
            assertThat(result.searchWord()).isEqualTo("USER");
            assertThat(result.searchField()).isEqualTo("NAME");
            assertThat(result.types()).containsExactly("CUSTOM");
            assertThat(result.searchParams().startDate()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(result.searchParams().endDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(result.searchParams().page()).isEqualTo(0);
            assertThat(result.searchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 필드를 포함한 요청도 정상 변환한다")
        void shouldConvertWithNullFields() {
            // Given
            var request =
                    new SearchRolesOffsetApiRequest(
                            null, null, null, null, null, null, null, null, null);

            // When
            RoleSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.types()).isNull();
            assertThat(result.searchParams()).isNotNull();
        }

        @Test
        @DisplayName("기본 정렬 및 페이징 값이 설정된다")
        void shouldSetDefaultSortAndPaging() {
            // Given
            var request =
                    new SearchRolesOffsetApiRequest(
                            null, null, null, null, null, null, null, 0, 20);

            // When
            RoleSearchParams result = mapper.toSearchParams(request);

            // Then
            assertThat(result.searchParams()).isNotNull();
            assertThat(result.searchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.searchParams().sortDirection()).isEqualTo("DESC");
            assertThat(result.searchParams().page()).isEqualTo(0);
            assertThat(result.searchParams().size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse(RoleResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("RoleResult를 RoleApiResponse로 변환한다")
        void shouldConvertToRoleApiResponse() {
            // Given
            Instant fixedTime = RoleApiFixture.fixedTime();
            var result =
                    new RoleResult(
                            RoleApiFixture.defaultRoleId(),
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            RoleApiFixture.defaultName(),
                            RoleApiFixture.defaultDisplayName(),
                            RoleApiFixture.defaultDescription(),
                            RoleApiFixture.defaultType(),
                            RoleApiFixture.defaultScope(),
                            fixedTime,
                            fixedTime);

            // When
            RoleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.roleId()).isEqualTo(RoleApiFixture.defaultRoleId());
            assertThat(response.tenantId()).isEqualTo(RoleApiFixture.defaultTenantId());
            assertThat(response.serviceId()).isEqualTo(RoleApiFixture.defaultServiceId());
            assertThat(response.name()).isEqualTo(RoleApiFixture.defaultName());
            assertThat(response.displayName()).isEqualTo(RoleApiFixture.defaultDisplayName());
            assertThat(response.description()).isEqualTo(RoleApiFixture.defaultDescription());
            assertThat(response.type()).isEqualTo(RoleApiFixture.defaultType());
            assertThat(response.scope()).isEqualTo(RoleApiFixture.defaultScope());
            assertThat(response.createdAt())
                    .isEqualTo(DateTimeFormatUtils.formatIso8601(fixedTime));
            assertThat(response.updatedAt())
                    .isEqualTo(DateTimeFormatUtils.formatIso8601(fixedTime));
        }

        @Test
        @DisplayName("null 필드를 포함한 RoleResult도 정상 변환한다")
        void shouldConvertWithNullFields() {
            // Given
            Instant fixedTime = RoleApiFixture.fixedTime();
            var result =
                    new RoleResult(
                            1L,
                            null,
                            null,
                            "TEST_ROLE",
                            null,
                            null,
                            "CUSTOM",
                            "GLOBAL",
                            fixedTime,
                            fixedTime);

            // When
            RoleApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.roleId()).isEqualTo(1L);
            assertThat(response.tenantId()).isNull();
            assertThat(response.serviceId()).isNull();
            assertThat(response.name()).isEqualTo("TEST_ROLE");
            assertThat(response.displayName()).isNull();
            assertThat(response.description()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponses(List<RoleResult>) 메서드는")
    class ToResponses {

        @Test
        @DisplayName("RoleResult 목록을 RoleApiResponse 목록으로 변환한다")
        void shouldConvertList() {
            // Given
            Instant fixedTime = RoleApiFixture.fixedTime();
            var result1 =
                    new RoleResult(
                            1L,
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            "ROLE_1",
                            "역할1",
                            "설명1",
                            "CUSTOM",
                            "TENANT_SERVICE",
                            fixedTime,
                            fixedTime);
            var result2 =
                    new RoleResult(
                            2L,
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            "ROLE_2",
                            "역할2",
                            "설명2",
                            "CUSTOM",
                            "TENANT_SERVICE",
                            fixedTime,
                            fixedTime);
            List<RoleResult> results = List.of(result1, result2);

            // When
            List<RoleApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).roleId()).isEqualTo(1L);
            assertThat(responses.get(0).name()).isEqualTo("ROLE_1");
            assertThat(responses.get(1).roleId()).isEqualTo(2L);
            assertThat(responses.get(1).name()).isEqualTo("ROLE_2");
        }

        @Test
        @DisplayName("빈 리스트도 정상 처리한다")
        void shouldConvertEmptyList() {
            // Given
            List<RoleResult> results = List.of();

            // When
            List<RoleApiResponse> responses = mapper.toResponses(results);

            // Then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse(RolePageResult) 메서드는")
    class ToPageResponse {

        @Test
        @DisplayName("RolePageResult를 PageApiResponse로 변환한다")
        void shouldConvertToPageApiResponse() {
            // Given
            Instant fixedTime = RoleApiFixture.fixedTime();
            var result =
                    new RoleResult(
                            RoleApiFixture.defaultRoleId(),
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            RoleApiFixture.defaultName(),
                            RoleApiFixture.defaultDisplayName(),
                            RoleApiFixture.defaultDescription(),
                            RoleApiFixture.defaultType(),
                            RoleApiFixture.defaultScope(),
                            fixedTime,
                            fixedTime);
            RolePageResult pageResult = RolePageResult.of(List.of(result), 0, 20, 1L);

            // When
            PageApiResponse<RoleApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).roleId())
                    .isEqualTo(RoleApiFixture.defaultRoleId());
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(1L);
        }

        @Test
        @DisplayName("빈 페이지 결과도 정상 변환한다")
        void shouldConvertEmptyPageResult() {
            // Given
            RolePageResult pageResult = RolePageResult.empty(20);

            // When
            PageApiResponse<RoleApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(0L);
        }

        @Test
        @DisplayName("다중 페이지 결과도 정상 변환한다")
        void shouldConvertMultiPageResult() {
            // Given
            Instant fixedTime = RoleApiFixture.fixedTime();
            var result1 =
                    new RoleResult(
                            1L,
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            "ROLE_1",
                            "역할1",
                            "설명1",
                            "CUSTOM",
                            "TENANT_SERVICE",
                            fixedTime,
                            fixedTime);
            var result2 =
                    new RoleResult(
                            2L,
                            RoleApiFixture.defaultTenantId(),
                            RoleApiFixture.defaultServiceId(),
                            "ROLE_2",
                            "역할2",
                            "설명2",
                            "CUSTOM",
                            "TENANT_SERVICE",
                            fixedTime,
                            fixedTime);
            RolePageResult pageResult = RolePageResult.of(List.of(result1, result2), 1, 10, 25L);

            // When
            PageApiResponse<RoleApiResponse> response = mapper.toPageResponse(pageResult);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(25L);
        }
    }
}
