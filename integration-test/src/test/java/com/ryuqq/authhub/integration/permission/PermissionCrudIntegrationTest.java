package com.ryuqq.authhub.integration.permission;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.permission.fixture.PermissionIntegrationTestFixture;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 권한 CRUD 통합 테스트
 *
 * <p>권한 관련 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("권한 CRUD 통합 테스트")
class PermissionCrudIntegrationTest extends BaseIntegrationTest {

    private String permissionsUrl() {
        return apiV1() + "/auth/permissions";
    }

    @Nested
    @DisplayName("권한 생성")
    class CreatePermission {

        @Test
        @DisplayName("TC-001: 일반 권한 생성 - 성공")
        void createPermission_success() {
            // given
            var request =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();

            // when
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> response =
                    postForApiResponse(
                            permissionsUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().permissionId()).isNotNull();
        }

        @Test
        @DisplayName("TC-002: 시스템 권한 생성 - 성공")
        void createSystemPermission_success() {
            // given
            String uniqueResource = "system" + System.currentTimeMillis();
            var request =
                    PermissionIntegrationTestFixture.createSystemPermissionRequest(
                            uniqueResource, "admin", "System admin permission");

            // when
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> response =
                    postForApiResponse(
                            permissionsUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("TC-003: 권한 생성 - 빈 리소스로 실패")
        void createPermission_emptyResource_returns400() {
            // given
            var request =
                    PermissionIntegrationTestFixture.createPermissionRequestWithEmptyResource();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            permissionsUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("TC-004: 권한 생성 - 빈 액션으로 실패")
        void createPermission_emptyAction_returns400() {
            // given
            var request = PermissionIntegrationTestFixture.createPermissionRequestWithEmptyAction();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            permissionsUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("권한 조회")
    class GetPermission {

        @Test
        @DisplayName("TC-005: 권한 조회 - 성공")
        void getPermission_success() {
            // given - 먼저 권한 생성
            var createRequest =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> createResponse =
                    postForApiResponse(
                            permissionsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String permissionId = createResponse.getBody().data().permissionId();

            // when - 권한 조회
            ResponseEntity<ApiResponse<PermissionApiResponse>> response =
                    getForApiResponse(
                            permissionsUrl() + "/" + permissionId,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().permissionId()).isEqualTo(permissionId);
        }

        @Test
        @DisplayName("TC-006: 존재하지 않는 권한 조회 - 404")
        void getPermission_notFound_returns404() {
            // given - 존재하지 않는 랜덤 UUID
            String nonExistentUuid = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            permissionsUrl() + "/" + nonExistentUuid,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("권한 수정")
    class UpdatePermission {

        @Test
        @DisplayName("TC-007: 권한 설명 수정 - 성공")
        void updatePermission_success() {
            // given - 먼저 권한 생성
            var createRequest =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> createResponse =
                    postForApiResponse(
                            permissionsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String permissionId = createResponse.getBody().data().permissionId();

            var updateRequest =
                    PermissionIntegrationTestFixture.updatePermissionRequest("Updated description");

            // when - 권한 수정
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            permissionsUrl() + "/" + permissionId,
                            HttpMethod.PUT,
                            createHttpEntity(updateRequest),
                            Void.class);

            // then - Controller가 200 OK를 반환 (Swagger 문서와 일치)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // verify - 수정된 정보 확인
            ResponseEntity<ApiResponse<PermissionApiResponse>> getResponse =
                    getForApiResponse(
                            permissionsUrl() + "/" + permissionId,
                            new ParameterizedTypeReference<>() {});
            assertThat(getResponse.getBody().data().description()).isEqualTo("Updated description");
        }
    }

    @Nested
    @DisplayName("CRUD 권한 세트 생성")
    class CreateCrudPermissions {

        @Test
        @DisplayName("TC-008: Read, Write, Delete 권한 세트 생성 - 성공")
        void createCrudPermissions_success() {
            // given
            String resource = "orders" + System.currentTimeMillis();

            var readRequest =
                    PermissionIntegrationTestFixture.createReadPermissionRequest(resource);
            var writeRequest =
                    PermissionIntegrationTestFixture.createWritePermissionRequest(resource);
            var deleteRequest =
                    PermissionIntegrationTestFixture.createDeletePermissionRequest(resource);

            // when
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> readResponse =
                    postForApiResponse(
                            permissionsUrl(), readRequest, new ParameterizedTypeReference<>() {});
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> writeResponse =
                    postForApiResponse(
                            permissionsUrl(), writeRequest, new ParameterizedTypeReference<>() {});
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> deleteResponse =
                    postForApiResponse(
                            permissionsUrl(), deleteRequest, new ParameterizedTypeReference<>() {});

            // then
            assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(writeResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            assertThat(readResponse.getBody().data().permissionId()).isNotNull();
            assertThat(writeResponse.getBody().data().permissionId()).isNotNull();
            assertThat(deleteResponse.getBody().data().permissionId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("권한 삭제")
    class DeletePermission {

        @Test
        @DisplayName("TC-009: 권한 삭제 (Soft Delete) - 성공")
        void deletePermission_success() {
            // given - 먼저 권한 생성
            var createRequest =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> createResponse =
                    postForApiResponse(
                            permissionsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String permissionId = createResponse.getBody().data().permissionId();

            // when - 권한 삭제 (Soft Delete)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            permissionsUrl() + "/" + permissionId + "/delete",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then - Controller가 204 NO_CONTENT 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("TC-010: 삭제된 권한 조회 - 404")
        void getDeletedPermission_returns404() {
            // given - 권한 생성 후 삭제
            var createRequest =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> createResponse =
                    postForApiResponse(
                            permissionsUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String permissionId = createResponse.getBody().data().permissionId();

            // 삭제 실행
            restTemplate.exchange(
                    permissionsUrl() + "/" + permissionId + "/delete",
                    HttpMethod.PATCH,
                    null,
                    Void.class);

            // when - 삭제된 권한 조회
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            permissionsUrl() + "/" + permissionId,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then - 삭제된 엔티티는 조회 불가 (Permission은 deleted 필터 적용됨)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("권한 목록 조회")
    class ListPermissions {

        @Test
        @DisplayName("TC-011: 권한 목록 조회 - 성공")
        void listPermissions_success() {
            // given - 권한 2개 생성
            var request1 =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            var request2 =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            postForApiResponse(permissionsUrl(), request1, new ParameterizedTypeReference<>() {});
            postForApiResponse(permissionsUrl(), request2, new ParameterizedTypeReference<>() {});

            // when - 목록 조회
            ResponseEntity<ApiResponse<List<PermissionApiResponse>>> response =
                    restTemplate.exchange(
                            permissionsUrl(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotEmpty();
            // 목록 조회 API가 정상 동작하는지 확인
            assertThat(response.getBody().data().size()).isGreaterThanOrEqualTo(1);
        }
    }
}
