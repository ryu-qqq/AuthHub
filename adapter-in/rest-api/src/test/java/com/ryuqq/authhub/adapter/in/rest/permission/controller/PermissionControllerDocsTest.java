package com.ryuqq.authhub.adapter.in.rest.permission.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import com.ryuqq.authhub.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.query.SearchPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.EndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionSpecApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.mapper.PermissionApiMapper;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.dto.response.PermissionSpecResponse;
import com.ryuqq.authhub.application.permission.port.in.command.CreatePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.DeletePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.UpdatePermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionSpecUseCase;
import com.ryuqq.authhub.application.permission.port.in.query.GetPermissionUseCase;
import com.ryuqq.authhub.application.permission.port.in.query.SearchPermissionsUseCase;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

/**
 * PermissionController REST Docs 문서화 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API 엔드포인트 문서화
 *   <li>Request/Response 필드 문서화
 *   <li>Path Parameter 문서화
 *   <li>Query Parameter 문서화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest({PermissionCommandController.class, PermissionQueryController.class})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("docs")
@DisplayName("Permission Controller REST Docs")
class PermissionControllerDocsTest extends RestDocsTestSupport {

    @MockBean private CreatePermissionUseCase createPermissionUseCase;

    @MockBean private UpdatePermissionUseCase updatePermissionUseCase;

    @MockBean private DeletePermissionUseCase deletePermissionUseCase;

    @MockBean private GetPermissionUseCase getPermissionUseCase;

    @MockBean private SearchPermissionsUseCase searchPermissionsUseCase;

    @MockBean private GetUserRolesUseCase getUserRolesUseCase;

    @MockBean private GetPermissionSpecUseCase getPermissionSpecUseCase;

    @MockBean private PermissionApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/permissions - 권한 생성")
    class CreatePermissionDocs {

        @Test
        @DisplayName("권한 생성 API 문서")
        void createPermission() throws Exception {
            // given
            String resource = "USER";
            String action = "READ";
            String description = "사용자 조회 권한";
            Boolean isSystem = false;
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest(resource, action, description, isSystem);

            UUID permissionId = UUID.randomUUID();
            String key = "USER:READ";
            String type = "CUSTOM";
            Instant now = Instant.now();
            CreatePermissionCommand command =
                    new CreatePermissionCommand(resource, action, description, isSystem);
            PermissionResponse useCaseResponse =
                    new PermissionResponse(
                            permissionId, key, resource, action, description, type, now, now);
            CreatePermissionApiResponse apiResponse =
                    new CreatePermissionApiResponse(permissionId.toString());

            given(mapper.toCommand(any(CreatePermissionApiRequest.class))).willReturn(command);
            given(createPermissionUseCase.execute(any(CreatePermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "permission-create",
                                    requestFields(
                                            fieldWithPath("resource")
                                                    .description("리소스명 (1~50자, 필수)"),
                                            fieldWithPath("action").description("액션명 (1~50자, 필수)"),
                                            fieldWithPath("description")
                                                    .description("권한 설명 (최대 500자, 선택)")
                                                    .optional(),
                                            fieldWithPath("isSystem")
                                                    .description("시스템 권한 여부 (선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.permissionId")
                                                    .description("생성된 권한 ID"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/permissions/{permissionId} - 권한 수정")
    class UpdatePermissionDocs {

        @Test
        @DisplayName("권한 수정 API 문서")
        void updatePermission() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            String description = "수정된 권한 설명";
            UpdatePermissionApiRequest request = new UpdatePermissionApiRequest(description);

            UpdatePermissionCommand command =
                    new UpdatePermissionCommand(UUID.fromString(permissionId), description);

            given(mapper.toCommand(any(String.class), any(UpdatePermissionApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put("/api/v1/permissions/{permissionId}", permissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "permission-update",
                                    pathParameters(
                                            parameterWithName("permissionId").description("권한 ID")),
                                    requestFields(
                                            fieldWithPath("description")
                                                    .description("권한 설명 (최대 500자, 선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터 (null)"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/permissions/{permissionId}/delete - 권한 삭제")
    class DeletePermissionDocs {

        @Test
        @DisplayName("권한 삭제 API 문서")
        void deletePermission() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            DeletePermissionCommand command =
                    new DeletePermissionCommand(UUID.fromString(permissionId));

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

            // when & then
            mockMvc.perform(patch("/api/v1/permissions/{permissionId}/delete", permissionId))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "permission-delete",
                                    pathParameters(
                                            parameterWithName("permissionId")
                                                    .description("권한 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/permissions/{permissionId} - 권한 단건 조회")
    class GetPermissionDocs {

        @Test
        @DisplayName("권한 단건 조회 API 문서")
        void getPermission() throws Exception {
            // given
            String permissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            PermissionResponse useCaseResponse =
                    new PermissionResponse(
                            UUID.fromString(permissionId),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);

            given(mapper.toGetQuery(permissionId))
                    .willReturn(new GetPermissionQuery(UUID.fromString(permissionId)));
            given(getPermissionUseCase.execute(any(GetPermissionQuery.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(PermissionResponse.class))).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            get("/api/v1/permissions/{permissionId}", permissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "permission-get",
                                    pathParameters(
                                            parameterWithName("permissionId").description("권한 ID")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.permissionId").description("권한 ID"),
                                            fieldWithPath("data.key").description("권한 키"),
                                            fieldWithPath("data.resource").description("리소스명"),
                                            fieldWithPath("data.action").description("액션명"),
                                            fieldWithPath("data.description").description("권한 설명"),
                                            fieldWithPath("data.type")
                                                    .description("권한 유형 (SYSTEM/CUSTOM)"),
                                            fieldWithPath("data.createdAt").description("생성 일시"),
                                            fieldWithPath("data.updatedAt").description("수정 일시"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/permissions - 권한 목록 검색")
    class SearchPermissionsDocs {

        @Test
        @DisplayName("권한 목록 검색 API 문서")
        void searchPermissions() throws Exception {
            // given
            UUID permissionId = UUID.randomUUID();
            Instant now = Instant.now();
            PermissionResponse response =
                    new PermissionResponse(
                            permissionId,
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);
            PermissionApiResponse apiResponse =
                    new PermissionApiResponse(
                            permissionId.toString(),
                            "tenant:read",
                            "tenant",
                            "read",
                            "테넌트 조회 권한",
                            "SYSTEM",
                            now,
                            now);

            given(mapper.toQuery(any(SearchPermissionsApiRequest.class)))
                    .willReturn(
                            new SearchPermissionsQuery(
                                    "tenant", "read", PermissionType.SYSTEM, 0, 20));
            given(searchPermissionsUseCase.execute(any(SearchPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // when & then
            mockMvc.perform(
                            get("/api/v1/permissions")
                                    .param("resource", "tenant")
                                    .param("action", "read")
                                    .param("type", "SYSTEM")
                                    .param("page", "0")
                                    .param("size", "20")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "permission-search",
                                    queryParameters(
                                            parameterWithName("resource")
                                                    .description("리소스 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("action")
                                                    .description("액션 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("type")
                                                    .description("권한 유형 필터 (SYSTEM/CUSTOM, 선택)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (기본값: 0)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기 (기본값: 20)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data[]").description("권한 목록"),
                                            fieldWithPath("data[].permissionId")
                                                    .description("권한 ID"),
                                            fieldWithPath("data[].key").description("권한 키"),
                                            fieldWithPath("data[].resource").description("리소스명"),
                                            fieldWithPath("data[].action").description("액션명"),
                                            fieldWithPath("data[].description")
                                                    .description("권한 설명"),
                                            fieldWithPath("data[].type")
                                                    .description("권한 유형 (SYSTEM/CUSTOM)"),
                                            fieldWithPath("data[].createdAt").description("생성 일시"),
                                            fieldWithPath("data[].updatedAt").description("수정 일시"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/permissions/users/{userId} - 사용자 권한 조회")
    class GetUserPermissionsDocs {

        @Test
        @DisplayName("사용자 권한 조회 API 문서")
        void getUserPermissions() throws Exception {
            // given
            UUID userId = UUID.randomUUID();
            UserRolesResponse useCaseResponse =
                    new UserRolesResponse(
                            userId,
                            Set.of("TENANT_ADMIN", "USER"),
                            Set.of("tenant:read", "user:read"));
            UserPermissionsApiResponse apiResponse =
                    new UserPermissionsApiResponse(
                            userId.toString(),
                            Set.of("TENANT_ADMIN", "USER"),
                            Set.of("tenant:read", "user:read"));

            given(getUserRolesUseCase.execute(userId)).willReturn(useCaseResponse);
            given(mapper.toUserPermissionsApiResponse(any(UserRolesResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            get("/api/v1/permissions/users/{userId}", userId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "permission-user",
                                    pathParameters(
                                            parameterWithName("userId").description("사용자 ID")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.userId").description("사용자 ID"),
                                            fieldWithPath("data.roles").description("역할 목록"),
                                            fieldWithPath("data.permissions")
                                                    .description("권한 키 목록"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/permissions/spec - 권한 명세 조회")
    class GetPermissionSpecDocs {

        @Test
        @DisplayName("권한 명세 조회 API 문서")
        void getPermissionSpec() throws Exception {
            // given
            Instant now = Instant.now();
            EndpointPermissionResponse endpointPermission =
                    new EndpointPermissionResponse(
                            "auth-service",
                            "/api/v1/tenants",
                            "GET",
                            List.of("tenant:read"),
                            List.of("ADMIN"),
                            false);
            PermissionSpecResponse useCaseResponse =
                    new PermissionSpecResponse(1, now, List.of(endpointPermission));
            EndpointPermissionApiResponse endpointApiResponse =
                    new EndpointPermissionApiResponse(
                            "auth-service",
                            "/api/v1/tenants",
                            "GET",
                            List.of("tenant:read"),
                            List.of("ADMIN"),
                            false);
            PermissionSpecApiResponse apiResponse =
                    new PermissionSpecApiResponse(1, now, List.of(endpointApiResponse));

            given(getPermissionSpecUseCase.execute()).willReturn(useCaseResponse);
            given(mapper.toPermissionSpecApiResponse(any(PermissionSpecResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(get("/api/v1/permissions/spec").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "permission-spec",
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.version").description("명세 버전"),
                                            fieldWithPath("data.updatedAt").description("갱신 시간"),
                                            fieldWithPath("data.permissions")
                                                    .description("엔드포인트별 권한 목록"),
                                            fieldWithPath("data.permissions[].serviceName")
                                                    .description("서비스명"),
                                            fieldWithPath("data.permissions[].path")
                                                    .description("엔드포인트 경로"),
                                            fieldWithPath("data.permissions[].method")
                                                    .description("HTTP 메서드"),
                                            fieldWithPath("data.permissions[].requiredPermissions")
                                                    .description("필요 권한 목록"),
                                            fieldWithPath("data.permissions[].requiredRoles")
                                                    .description("필요 역할 목록"),
                                            fieldWithPath("data.permissions[].isPublic")
                                                    .description("공개 엔드포인트 여부"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
