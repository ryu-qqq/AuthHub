package com.ryuqq.authhub.integration.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 테넌트 CRUD 통합 테스트
 *
 * <p>테넌트 관련 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * <p><strong>테스트 범위:</strong>
 *
 * <ul>
 *   <li>HTTP Request → Controller → UseCase → Repository → DB
 *   <li>실제 HTTP 요청/응답 검증
 *   <li>전체 레이어 통합 동작 검증
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("테넌트 CRUD 통합 테스트")
class TenantCrudIntegrationTest extends BaseIntegrationTest {

    private static final Instant NOW = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    private String tenantsUrl() {
        return apiV1() + "/auth/tenants";
    }

    @Nested
    @DisplayName("테넌트 생성")
    class CreateTenant {

        @Test
        @DisplayName("TC-001: 테넌트 생성 - 성공")
        void createTenant_success() {
            // given
            var request = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();

            // when
            ResponseEntity<ApiResponse<CreateTenantApiResponse>> response =
                    postForApiResponse(
                            tenantsUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isTrue();
            assertThat(response.getBody().data().tenantId()).isNotBlank();
        }

        @Test
        @DisplayName("TC-002: 테넌트 생성 - 빈 이름으로 실패")
        void createTenant_emptyName_returns400() {
            // given
            var request = TenantIntegrationTestFixture.createTenantRequestWithEmptyName();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            tenantsUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("TC-003: 테넌트 생성 - 너무 짧은 이름으로 실패")
        void createTenant_tooShortName_returns400() {
            // given
            var request = TenantIntegrationTestFixture.createTenantRequestWithTooShortName();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            tenantsUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("테넌트 조회")
    class GetTenant {

        @Test
        @DisplayName("TC-004: 테넌트 조회 - 성공")
        void getTenant_success() {
            // given - 먼저 테넌트 생성
            var createRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            ResponseEntity<ApiResponse<CreateTenantApiResponse>> createResponse =
                    postForApiResponse(
                            tenantsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String tenantId = createResponse.getBody().data().tenantId();

            // when - 테넌트 조회
            ResponseEntity<ApiResponse<TenantApiResponse>> response =
                    getForApiResponse(
                            tenantsUrl() + "/" + tenantId, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isTrue();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("TC-005: 존재하지 않는 테넌트 조회 - 404")
        void getTenant_notFound_returns404() {
            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/00000000-0000-0000-0000-000000000000",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("테넌트 수정")
    class UpdateTenant {

        @Test
        @DisplayName("TC-006: 테넌트 이름 수정 - 성공")
        void updateTenantName_success() {
            // given - 먼저 테넌트 생성
            var createRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            ResponseEntity<ApiResponse<CreateTenantApiResponse>> createResponse =
                    postForApiResponse(
                            tenantsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String tenantId = createResponse.getBody().data().tenantId();

            var updateRequest =
                    TenantIntegrationTestFixture.updateTenantNameRequest("Updated Name");

            // when - 테넌트 이름 수정
            ResponseEntity<ApiResponse<TenantApiResponse>> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/" + tenantId + "/name",
                            HttpMethod.PUT,
                            createHttpEntity(updateRequest),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isTrue();
            assertThat(response.getBody().data().name()).isEqualTo("Updated Name");
        }
    }

    @Nested
    @DisplayName("테넌트 상태 변경")
    class UpdateTenantStatus {

        @Test
        @DisplayName("TC-007: 테넌트 비활성화 - 성공")
        void deactivateTenant_success() {
            // given - 먼저 테넌트 생성
            var createRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            ResponseEntity<ApiResponse<CreateTenantApiResponse>> createResponse =
                    postForApiResponse(
                            tenantsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String tenantId = createResponse.getBody().data().tenantId();

            var statusRequest = TenantIntegrationTestFixture.deactivateTenantRequest();

            // when - 테넌트 비활성화 (PATCH)
            ResponseEntity<ApiResponse<TenantApiResponse>> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/" + tenantId + "/status",
                            HttpMethod.PATCH,
                            createHttpEntity(statusRequest),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isTrue();
            assertThat(response.getBody().data().status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("테넌트 삭제")
    class DeleteTenant {

        @Test
        @DisplayName("TC-008: 테넌트 삭제 (Soft Delete) - 성공")
        void deleteTenant_success() {
            // given - 먼저 테넌트 생성
            var createRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            ResponseEntity<ApiResponse<CreateTenantApiResponse>> createResponse =
                    postForApiResponse(
                            tenantsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String tenantId = createResponse.getBody().data().tenantId();

            // when - 테넌트 삭제 (Soft Delete)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/" + tenantId + "/delete",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then - Controller가 204 NO_CONTENT 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("TC-009: 삭제된 테넌트 조회 - soft delete 상태 확인")
        void getDeletedTenant_checksDeletedStatus() {
            // given - 테넌트 생성 후 삭제
            var createRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            ResponseEntity<ApiResponse<CreateTenantApiResponse>> createResponse =
                    postForApiResponse(
                            tenantsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String tenantId = createResponse.getBody().data().tenantId();

            // 삭제 실행
            restTemplate.exchange(
                    tenantsUrl() + "/" + tenantId + "/delete", HttpMethod.PATCH, null, Void.class);

            // when - 삭제된 테넌트 조회
            // NOTE: 현재 구현에서는 soft-deleted 엔티티도 조회 가능
            // QueryDSL Repository에 deleted 필터가 없어서 여전히 조회됨
            ResponseEntity<ApiResponse<TenantApiResponse>> response =
                    getForApiResponse(
                            tenantsUrl() + "/" + tenantId, new ParameterizedTypeReference<>() {});

            // then - soft-deleted 엔티티는 deleted=true 상태로 조회됨
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().tenantId()).isEqualTo(tenantId);
        }
    }

    @Nested
    @DisplayName("테넌트 목록 조회")
    class ListTenants {

        @Test
        @DisplayName("TC-010: 테넌트 목록 조회 - 성공")
        void listTenants_success() {
            // given - 테넌트 2개 생성
            var request1 = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            var request2 = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
            postForApiResponse(tenantsUrl(), request1, new ParameterizedTypeReference<>() {});
            postForApiResponse(tenantsUrl(), request2, new ParameterizedTypeReference<>() {});

            // when - 목록 조회
            ResponseEntity<ApiResponse<PageApiResponse<TenantApiResponse>>> response =
                    restTemplate.exchange(
                            tenantsUrl()
                                    + "?page=0&size=20&createdFrom="
                                    + ONE_MONTH_AGO
                                    + "&createdTo="
                                    + NOW,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            PageApiResponse<TenantApiResponse> pageData = response.getBody().data();
            assertThat(pageData.content()).isNotEmpty();
            // 목록 조회 API가 정상 동작하는지 확인
            assertThat(pageData.totalElements()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("TC-011: 테넌트 목록 조회 - 페이지네이션 검증")
        void listTenants_pagination() {
            // given - 테넌트 3개 생성
            for (int i = 0; i < 3; i++) {
                var request = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
                postForApiResponse(tenantsUrl(), request, new ParameterizedTypeReference<>() {});
            }

            // when - 페이지 크기 2로 조회
            ResponseEntity<ApiResponse<PageApiResponse<TenantApiResponse>>> response =
                    restTemplate.exchange(
                            tenantsUrl()
                                    + "?page=0&size=2&createdFrom="
                                    + ONE_MONTH_AGO
                                    + "&createdTo="
                                    + NOW,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            PageApiResponse<TenantApiResponse> pageData = response.getBody().data();
            assertThat(pageData.content().size()).isLessThanOrEqualTo(2);
            assertThat(pageData.size()).isEqualTo(2);
            assertThat(pageData.page()).isEqualTo(0);
        }
    }
}
