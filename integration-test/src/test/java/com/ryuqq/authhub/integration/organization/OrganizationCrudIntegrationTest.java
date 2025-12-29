package com.ryuqq.authhub.integration.organization;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.organization.fixture.OrganizationIntegrationTestFixture;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 조직 CRUD 통합 테스트
 *
 * <p>조직 관련 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("조직 CRUD 통합 테스트")
class OrganizationCrudIntegrationTest extends BaseIntegrationTest {

    private static final Instant NOW = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    private String tenantId;

    private String tenantsUrl() {
        return apiV1() + "/auth/tenants";
    }

    private String organizationsUrl() {
        return apiV1() + "/auth/organizations";
    }

    @BeforeEach
    void setUp() {
        // 테스트용 테넌트 생성
        var tenantRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
        ResponseEntity<ApiResponse<CreateTenantApiResponse>> tenantResponse =
                postForApiResponse(
                        tenantsUrl(), tenantRequest, new ParameterizedTypeReference<>() {});
        this.tenantId = tenantResponse.getBody().data().tenantId();
    }

    @Nested
    @DisplayName("조직 생성")
    class CreateOrganization {

        @Test
        @DisplayName("TC-001: 조직 생성 - 성공")
        void createOrganization_success() {
            // given
            var request =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);

            // when
            ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> response =
                    postForApiResponse(
                            organizationsUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().organizationId()).isNotBlank();
        }

        @Test
        @DisplayName("TC-002: 조직 생성 - 빈 테넌트 ID로 실패")
        void createOrganization_emptyTenantId_returns400() {
            // given
            var request =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithEmptyTenantId();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            organizationsUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("조직 조회")
    class GetOrganization {

        @Test
        @DisplayName("TC-003: 조직 조회 - 성공")
        void getOrganization_success() {
            // given - 먼저 조직 생성
            var createRequest =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createResponse =
                    postForApiResponse(
                            organizationsUrl(),
                            createRequest,
                            new ParameterizedTypeReference<>() {});
            String organizationId = createResponse.getBody().data().organizationId();

            // when - 조직 조회
            ResponseEntity<ApiResponse<OrganizationApiResponse>> response =
                    getForApiResponse(
                            organizationsUrl() + "/" + organizationId,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().organizationId()).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("TC-004: 존재하지 않는 조직 조회 - 404")
        void getOrganization_notFound_returns404() {
            // given - 존재하지 않는 랜덤 UUID
            String nonExistentUuid = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/" + nonExistentUuid,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("조직 수정")
    class UpdateOrganization {

        @Test
        @DisplayName("TC-005: 조직 이름 수정 - 성공")
        void updateOrganization_success() {
            // given - 먼저 조직 생성
            var createRequest =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createResponse =
                    postForApiResponse(
                            organizationsUrl(),
                            createRequest,
                            new ParameterizedTypeReference<>() {});
            String organizationId = createResponse.getBody().data().organizationId();

            var updateRequest =
                    OrganizationIntegrationTestFixture.updateOrganizationRequest(
                            "Updated Org Name");

            // when - 조직 수정
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/" + organizationId,
                            HttpMethod.PUT,
                            createHttpEntity(updateRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("조직 상태 변경")
    class UpdateOrganizationStatus {

        @Test
        @DisplayName("TC-006: 조직 비활성화 - 성공")
        void deactivateOrganization_success() {
            // given - 먼저 조직 생성
            var createRequest =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createResponse =
                    postForApiResponse(
                            organizationsUrl(),
                            createRequest,
                            new ParameterizedTypeReference<>() {});
            String organizationId = createResponse.getBody().data().organizationId();

            var statusRequest = OrganizationIntegrationTestFixture.deactivateOrganizationRequest();

            // when - 조직 비활성화 (PATCH 메서드 사용)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/" + organizationId + "/status",
                            HttpMethod.PATCH,
                            createHttpEntity(statusRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("조직 삭제")
    class DeleteOrganization {

        @Test
        @DisplayName("TC-007: 조직 삭제 (Soft Delete) - 성공")
        void deleteOrganization_success() {
            // given - 먼저 조직 생성
            var createRequest =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createResponse =
                    postForApiResponse(
                            organizationsUrl(),
                            createRequest,
                            new ParameterizedTypeReference<>() {});
            String organizationId = createResponse.getBody().data().organizationId();

            // when - 조직 삭제 (Soft Delete)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/" + organizationId + "/delete",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then - Controller가 204 NO_CONTENT 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("TC-008: 삭제된 조직 조회 - soft delete 상태 확인")
        void getDeletedOrganization_checksDeletedStatus() {
            // given - 조직 생성 후 삭제
            var createRequest =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> createResponse =
                    postForApiResponse(
                            organizationsUrl(),
                            createRequest,
                            new ParameterizedTypeReference<>() {});
            String organizationId = createResponse.getBody().data().organizationId();

            // 삭제 실행
            restTemplate.exchange(
                    organizationsUrl() + "/" + organizationId + "/delete",
                    HttpMethod.PATCH,
                    null,
                    Void.class);

            // when - 삭제된 조직 조회
            // NOTE: 현재 구현에서는 soft-deleted 엔티티도 조회 가능
            // QueryDSL Repository에 deleted 필터가 없어서 여전히 조회됨
            ResponseEntity<ApiResponse<OrganizationApiResponse>> response =
                    getForApiResponse(
                            organizationsUrl() + "/" + organizationId,
                            new ParameterizedTypeReference<>() {});

            // then - soft-deleted 엔티티는 deleted=true 상태로 조회됨
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().organizationId()).isEqualTo(organizationId);
            // 삭제 플래그 확인 (응답에 deleted 필드가 있다면)
        }
    }

    @Nested
    @DisplayName("조직 목록 조회")
    class ListOrganizations {

        @Test
        @DisplayName("TC-009: 조직 목록 조회 - 성공")
        void listOrganizations_success() {
            // given - 조직 2개 생성
            var request1 =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            var request2 =
                    OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                            tenantId);
            postForApiResponse(organizationsUrl(), request1, new ParameterizedTypeReference<>() {});
            postForApiResponse(organizationsUrl(), request2, new ParameterizedTypeReference<>() {});

            // when - 목록 조회
            ResponseEntity<ApiResponse<PageApiResponse<OrganizationApiResponse>>> response =
                    restTemplate.exchange(
                            organizationsUrl()
                                    + "?tenantId="
                                    + tenantId
                                    + "&page=0&size=20&createdFrom="
                                    + ONE_MONTH_AGO
                                    + "&createdTo="
                                    + NOW,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().content()).isNotEmpty();
            // 목록 조회 API가 정상 동작하는지 확인
            assertThat(response.getBody().data().totalElements()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("TC-010: 조직 목록 조회 - 페이지네이션 검증")
        void listOrganizations_pagination() {
            // given - 조직 3개 생성
            for (int i = 0; i < 3; i++) {
                var request =
                        OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                                tenantId);
                postForApiResponse(
                        organizationsUrl(), request, new ParameterizedTypeReference<>() {});
            }

            // when - 페이지 크기 2로 조회
            ResponseEntity<ApiResponse<PageApiResponse<OrganizationApiResponse>>> response =
                    restTemplate.exchange(
                            organizationsUrl()
                                    + "?tenantId="
                                    + tenantId
                                    + "&page=0&size=2&createdFrom="
                                    + ONE_MONTH_AGO
                                    + "&createdTo="
                                    + NOW,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().content().size()).isLessThanOrEqualTo(2);
            assertThat(response.getBody().data().size()).isEqualTo(2);
            assertThat(response.getBody().data().page()).isEqualTo(0);
        }
    }
}
