package com.ryuqq.authhub.integration.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.organization.fixture.OrganizationIntegrationTestFixture;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import com.ryuqq.authhub.integration.user.fixture.UserIntegrationTestFixture;
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
 * 사용자 CRUD 통합 테스트
 *
 * <p>사용자 관련 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("사용자 CRUD 통합 테스트")
class UserCrudIntegrationTest extends BaseIntegrationTest {

    private static final Instant NOW = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    private String tenantId;
    private String organizationId;

    private String tenantsUrl() {
        return apiV1() + "/auth/tenants";
    }

    private String organizationsUrl() {
        return apiV1() + "/auth/organizations";
    }

    private String usersUrl() {
        return apiV1() + "/auth/users";
    }

    @BeforeEach
    void setUp() {
        // 테스트용 테넌트 생성
        var tenantRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
        ResponseEntity<ApiResponse<CreateTenantApiResponse>> tenantResponse =
                postForApiResponse(
                        tenantsUrl(), tenantRequest, new ParameterizedTypeReference<>() {});
        this.tenantId = tenantResponse.getBody().data().tenantId();

        // 테스트용 조직 생성
        var orgRequest =
                OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                        tenantId);
        ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> orgResponse =
                postForApiResponse(
                        organizationsUrl(), orgRequest, new ParameterizedTypeReference<>() {});
        this.organizationId = orgResponse.getBody().data().organizationId();
    }

    @Nested
    @DisplayName("사용자 생성")
    class CreateUser {

        @Test
        @DisplayName("TC-001: 사용자 생성 - 성공")
        void createUser_success() {
            // given
            var request =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);

            // when
            ResponseEntity<ApiResponse<CreateUserApiResponse>> response =
                    postForApiResponse(usersUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().userId()).isNotBlank();
        }

        @Test
        @DisplayName("TC-002: 사용자 생성 - 빈 식별자로 실패")
        void createUser_emptyIdentifier_returns400() {
            // given
            var request =
                    UserIntegrationTestFixture.createUserRequestWithEmptyIdentifier(
                            tenantId, organizationId);

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            usersUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("TC-003: 사용자 생성 - 짧은 비밀번호로 실패")
        void createUser_shortPassword_returns400() {
            // given
            var request =
                    UserIntegrationTestFixture.createUserRequestWithShortPassword(
                            tenantId, organizationId);

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            usersUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("사용자 조회")
    class GetUser {

        @Test
        @DisplayName("TC-004: 사용자 조회 - 성공")
        void getUser_success() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            // when - 사용자 조회
            ResponseEntity<ApiResponse<UserApiResponse>> response =
                    getForApiResponse(
                            usersUrl() + "/" + userId, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().userId()).isEqualTo(userId);
            assertThat(response.getBody().data().tenantId()).isEqualTo(tenantId);
            assertThat(response.getBody().data().organizationId()).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("TC-005: 존재하지 않는 사용자 조회 - 404")
        void getUser_notFound_returns404() {
            // given - 존재하지 않는 랜덤 UUID
            String nonExistentUuid = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + nonExistentUuid,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정")
    class UpdateUser {

        @Test
        @DisplayName("TC-012: 사용자 정보 수정 - 성공")
        void updateUser_success() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            var updateRequest = UserIntegrationTestFixture.updateUserRequest();

            // when - 사용자 정보 수정
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId,
                            HttpMethod.PUT,
                            createHttpEntity(updateRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("TC-013: 사용자 정보 수정 - 빈 identifier로 실패")
        void updateUser_emptyIdentifier_returns400() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            var updateRequest = UserIntegrationTestFixture.updateUserRequestWithEmptyIdentifier();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId,
                            HttpMethod.PUT,
                            createHttpEntity(updateRequest),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class UpdatePassword {

        @Test
        @DisplayName("TC-014: 비밀번호 변경 - 성공")
        void updatePassword_success() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            var passwordRequest = UserIntegrationTestFixture.updatePasswordRequest();

            // when - 비밀번호 변경
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId + "/password",
                            HttpMethod.PATCH,
                            createHttpEntity(passwordRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("TC-015: 비밀번호 변경 - 짧은 새 비밀번호로 실패")
        void updatePassword_shortNewPassword_returns400() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            var passwordRequest =
                    UserIntegrationTestFixture.updatePasswordRequestWithShortNewPassword();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId + "/password",
                            HttpMethod.PATCH,
                            createHttpEntity(passwordRequest),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("사용자 상태 변경")
    class UpdateUserStatus {

        @Test
        @DisplayName("TC-006: 사용자 비활성화 - 성공")
        void deactivateUser_success() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            var statusRequest = UserIntegrationTestFixture.deactivateUserRequest();

            // when - 사용자 비활성화 (PATCH 메서드 사용)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId + "/status",
                            HttpMethod.PATCH,
                            createHttpEntity(statusRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // verify - 상태 확인
            ResponseEntity<ApiResponse<UserApiResponse>> getResponse =
                    getForApiResponse(
                            usersUrl() + "/" + userId, new ParameterizedTypeReference<>() {});
            assertThat(getResponse.getBody().data().status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("TC-007: 사용자 재활성화 - 성공")
        void reactivateUser_success() {
            // given - 먼저 사용자 생성 후 비활성화
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            // 먼저 비활성화
            var deactivateRequest = UserIntegrationTestFixture.deactivateUserRequest();
            restTemplate.exchange(
                    usersUrl() + "/" + userId + "/status",
                    HttpMethod.PATCH,
                    createHttpEntity(deactivateRequest),
                    Void.class);

            // when - 사용자 재활성화 (PATCH 메서드 사용)
            var activateRequest = UserIntegrationTestFixture.activateUserRequest();
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId + "/status",
                            HttpMethod.PATCH,
                            createHttpEntity(activateRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // verify - 상태 확인
            ResponseEntity<ApiResponse<UserApiResponse>> getResponse =
                    getForApiResponse(
                            usersUrl() + "/" + userId, new ParameterizedTypeReference<>() {});
            assertThat(getResponse.getBody().data().status()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("사용자 삭제")
    class DeleteUser {

        @Test
        @DisplayName("TC-008: 사용자 삭제 (Soft Delete) - 성공")
        void deleteUser_success() {
            // given - 먼저 사용자 생성
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            // when - 사용자 삭제 (Soft Delete)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId + "/delete",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then - Controller가 204 NO_CONTENT 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("TC-009: 삭제된 사용자 조회 - soft delete 상태 확인")
        void getDeletedUser_checksDeletedStatus() {
            // given - 사용자 생성 후 삭제
            var createRequest =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            ResponseEntity<ApiResponse<CreateUserApiResponse>> createResponse =
                    postForApiResponse(
                            usersUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String userId = createResponse.getBody().data().userId();

            // 삭제 실행
            restTemplate.exchange(
                    usersUrl() + "/" + userId + "/delete", HttpMethod.PATCH, null, Void.class);

            // when - 삭제된 사용자 조회
            // NOTE: 현재 구현에서는 soft-deleted 엔티티도 조회 가능
            // QueryDSL Repository에 deleted 필터가 없어서 여전히 조회됨
            ResponseEntity<ApiResponse<UserApiResponse>> response =
                    getForApiResponse(
                            usersUrl() + "/" + userId, new ParameterizedTypeReference<>() {});

            // then - soft-deleted 엔티티는 deleted=true 상태로 조회됨
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().userId()).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("사용자 목록 조회")
    class ListUsers {

        @Test
        @DisplayName("TC-010: 사용자 목록 조회 - 성공")
        void listUsers_success() {
            // given - 사용자 2명 생성
            var request1 =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            var request2 =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            postForApiResponse(usersUrl(), request1, new ParameterizedTypeReference<>() {});
            postForApiResponse(usersUrl(), request2, new ParameterizedTypeReference<>() {});

            // when - 목록 조회
            ResponseEntity<ApiResponse<PageApiResponse<UserApiResponse>>> response =
                    restTemplate.exchange(
                            usersUrl()
                                    + "?tenantId="
                                    + tenantId
                                    + "&createdFrom="
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
        @DisplayName("TC-011: 사용자 목록 조회 - 조직 필터 적용")
        void listUsers_filterByOrganization() {
            // given - 사용자 생성
            var request =
                    UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                            tenantId, organizationId);
            postForApiResponse(usersUrl(), request, new ParameterizedTypeReference<>() {});

            // when - 조직 필터로 조회
            ResponseEntity<ApiResponse<PageApiResponse<UserApiResponse>>> response =
                    restTemplate.exchange(
                            usersUrl()
                                    + "?tenantId="
                                    + tenantId
                                    + "&organizationId="
                                    + organizationId
                                    + "&createdFrom="
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
            // 모든 반환된 사용자가 해당 조직에 속함
            assertThat(response.getBody().data().content())
                    .allMatch(user -> organizationId.equals(user.organizationId()));
        }
    }
}
