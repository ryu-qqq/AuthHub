package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API Request DTO → Application Command/Query DTO 변환
 *   <li>Application Response DTO → API Response DTO 변환
 *   <li>목록 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TenantApiMapper 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class TenantApiMapperTest {

    private TenantApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateTenantApiRequest) 테스트")
    class ToCommandTest {

        @Test
        @DisplayName("[toCommand] CreateTenantApiRequest를 CreateTenantCommand로 변환")
        void toCommand_shouldConvertRequestToCommand() {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("TestTenant");

            // When
            CreateTenantCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("TestTenant");
        }
    }

    @Nested
    @DisplayName("toQuery(SearchTenantsApiRequest) 테스트")
    class ToQueryTest {

        @Test
        @DisplayName("[toQuery] 모든 필드가 있는 요청을 Query로 변환")
        void toQuery_shouldConvertRequestWithAllFields() {
            // Given
            SearchTenantsApiRequest request = new SearchTenantsApiRequest("Test", "ACTIVE", 0, 10);

            // When
            SearchTenantsQuery result = mapper.toQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Test");
            assertThat(result.status()).isEqualTo("ACTIVE");
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("[toQuery] null 필드가 있는 요청을 Query로 변환")
        void toQuery_shouldConvertRequestWithNullFields() {
            // Given
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, null, null);

            // When
            SearchTenantsQuery result = mapper.toQuery(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isNull();
            assertThat(result.status()).isNull();
            assertThat(result.page()).isNull();
            assertThat(result.size()).isNull();
        }
    }

    @Nested
    @DisplayName("toCreateResponse(TenantResponse) 테스트")
    class ToCreateResponseTest {

        @Test
        @DisplayName("[toCreateResponse] TenantResponse를 CreateTenantApiResponse로 변환")
        void toCreateResponse_shouldConvertToApiResponse() {
            // Given
            UUID tenantId = UUID.randomUUID();
            TenantResponse response =
                    new TenantResponse(
                            tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());

            // When
            CreateTenantApiResponse result = mapper.toCreateResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(tenantId.toString());
        }
    }

    @Nested
    @DisplayName("toApiResponse(TenantResponse) 테스트")
    class ToApiResponseTest {

        @Test
        @DisplayName("[toApiResponse] TenantResponse를 TenantApiResponse로 변환")
        void toApiResponse_shouldConvertToApiResponse() {
            // Given
            UUID tenantId = UUID.randomUUID();
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();
            TenantResponse response =
                    new TenantResponse(tenantId, "TestTenant", "ACTIVE", createdAt, updatedAt);

            // When
            TenantApiResponse result = mapper.toApiResponse(response);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(tenantId.toString());
            assertThat(result.name()).isEqualTo("TestTenant");
            assertThat(result.status()).isEqualTo("ACTIVE");
            assertThat(result.createdAt()).isEqualTo(createdAt);
            assertThat(result.updatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("toApiResponseList(List<TenantResponse>) 테스트")
    class ToApiResponseListTest {

        @Test
        @DisplayName("[toApiResponseList] 여러 TenantResponse를 목록으로 변환")
        void toApiResponseList_shouldConvertListToApiResponseList() {
            // Given
            UUID tenantId1 = UUID.randomUUID();
            UUID tenantId2 = UUID.randomUUID();
            TenantResponse response1 =
                    new TenantResponse(
                            tenantId1, "Tenant1", "ACTIVE", Instant.now(), Instant.now());
            TenantResponse response2 =
                    new TenantResponse(
                            tenantId2, "Tenant2", "INACTIVE", Instant.now(), Instant.now());

            // When
            List<TenantApiResponse> results =
                    mapper.toApiResponseList(List.of(response1, response2));

            // Then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).tenantId()).isEqualTo(tenantId1.toString());
            assertThat(results.get(0).name()).isEqualTo("Tenant1");
            assertThat(results.get(1).tenantId()).isEqualTo(tenantId2.toString());
            assertThat(results.get(1).name()).isEqualTo("Tenant2");
        }

        @Test
        @DisplayName("[toApiResponseList] 빈 목록을 변환하면 빈 결과 반환")
        void toApiResponseList_shouldReturnEmptyListForEmptyInput() {
            // Given
            List<TenantResponse> emptyList = List.of();

            // When
            List<TenantApiResponse> results = mapper.toApiResponseList(emptyList);

            // Then
            assertThat(results).isEmpty();
        }
    }
}
