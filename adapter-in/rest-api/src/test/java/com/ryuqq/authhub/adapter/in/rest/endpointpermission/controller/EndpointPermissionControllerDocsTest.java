package com.ryuqq.authhub.adapter.in.rest.endpointpermission.controller;

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
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.CreateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.UpdateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.query.SearchEndpointPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.CreateEndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.EndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper.EndpointPermissionApiMapper;
import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.UpdateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetServiceEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecItemResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecListResponse;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.CreateEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.DeleteEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.command.UpdateEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetAllEndpointPermissionSpecUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.GetEndpointPermissionUseCase;
import com.ryuqq.authhub.application.endpointpermission.port.in.query.SearchEndpointPermissionsUseCase;
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
 * EndpointPermissionController REST Docs 문서화 테스트
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
@WebMvcTest({EndpointPermissionCommandController.class, EndpointPermissionQueryController.class})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@Tag("docs")
@DisplayName("EndpointPermission Controller REST Docs")
class EndpointPermissionControllerDocsTest extends RestDocsTestSupport {

    @MockBean private CreateEndpointPermissionUseCase createEndpointPermissionUseCase;

    @MockBean private UpdateEndpointPermissionUseCase updateEndpointPermissionUseCase;

    @MockBean private DeleteEndpointPermissionUseCase deleteEndpointPermissionUseCase;

    @MockBean private GetEndpointPermissionUseCase getEndpointPermissionUseCase;

    @MockBean private GetAllEndpointPermissionSpecUseCase getAllEndpointPermissionSpecUseCase;

    @MockBean private SearchEndpointPermissionsUseCase searchEndpointPermissionsUseCase;

    @MockBean private EndpointPermissionApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/endpoint-permissions - 엔드포인트 권한 생성")
    class CreateEndpointPermissionDocs {

        @Test
        @DisplayName("엔드포인트 권한 생성 API 문서")
        void createEndpointPermission() throws Exception {
            // given
            String serviceName = "auth-hub";
            String path = "/api/v1/users/{userId}";
            String method = "GET";
            String description = "사용자 상세 조회 엔드포인트";
            Boolean isPublic = false;
            Set<String> requiredPermissions = Set.of("user:read");
            Set<String> requiredRoles = Set.of("ADMIN", "USER_MANAGER");

            CreateEndpointPermissionApiRequest request =
                    new CreateEndpointPermissionApiRequest(
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);

            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();

            CreateEndpointPermissionCommand command =
                    new CreateEndpointPermissionCommand(
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);

            EndpointPermissionResponse useCaseResponse =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            serviceName,
                            path,
                            method,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles,
                            0L,
                            now,
                            now);

            CreateEndpointPermissionApiResponse apiResponse =
                    new CreateEndpointPermissionApiResponse(endpointPermissionId);

            given(mapper.toCommand(any(CreateEndpointPermissionApiRequest.class)))
                    .willReturn(command);
            given(
                            createEndpointPermissionUseCase.execute(
                                    any(CreateEndpointPermissionCommand.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toCreateResponse(any(EndpointPermissionResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            post("/api/v1/endpoint-permissions")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "endpoint-permission-create",
                                    requestFields(
                                            fieldWithPath("serviceName")
                                                    .description("서비스 이름 (1~100자, 필수)"),
                                            fieldWithPath("path")
                                                    .description("엔드포인트 경로 (1~500자, 필수)"),
                                            fieldWithPath("method").description("HTTP 메서드 (필수)"),
                                            fieldWithPath("description")
                                                    .description("설명 (최대 500자, 선택)")
                                                    .optional(),
                                            fieldWithPath("isPublic")
                                                    .description("공개 여부 (true: 인증 불필요, 필수)"),
                                            fieldWithPath("requiredPermissions")
                                                    .description("필요 권한 목록 (OR 조건, 선택)")
                                                    .optional(),
                                            fieldWithPath("requiredRoles")
                                                    .description("필요 역할 목록 (OR 조건, 선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.id").description("생성된 엔드포인트 권한 ID"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/endpoint-permissions/{endpointPermissionId} - 엔드포인트 권한 수정")
    class UpdateEndpointPermissionDocs {

        @Test
        @DisplayName("엔드포인트 권한 수정 API 문서")
        void updateEndpointPermission() throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            String description = "수정된 설명";
            Boolean isPublic = true;
            Set<String> requiredPermissions = Set.of("user:write");
            Set<String> requiredRoles = Set.of("SUPER_ADMIN");

            UpdateEndpointPermissionApiRequest request =
                    new UpdateEndpointPermissionApiRequest(
                            description, isPublic, requiredPermissions, requiredRoles);

            UpdateEndpointPermissionCommand command =
                    new UpdateEndpointPermissionCommand(
                            endpointPermissionId,
                            description,
                            isPublic,
                            requiredPermissions,
                            requiredRoles);

            given(
                            mapper.toCommand(
                                    any(String.class),
                                    any(UpdateEndpointPermissionApiRequest.class)))
                    .willReturn(command);

            // when & then
            mockMvc.perform(
                            put(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "endpoint-permission-update",
                                    pathParameters(
                                            parameterWithName("endpointPermissionId")
                                                    .description("엔드포인트 권한 ID")),
                                    requestFields(
                                            fieldWithPath("description")
                                                    .description("설명 (최대 500자, 선택)")
                                                    .optional(),
                                            fieldWithPath("isPublic")
                                                    .description("공개 여부 (선택)")
                                                    .optional(),
                                            fieldWithPath("requiredPermissions")
                                                    .description("필요 권한 목록 (OR 조건, 선택)")
                                                    .optional(),
                                            fieldWithPath("requiredRoles")
                                                    .description("필요 역할 목록 (OR 조건, 선택)")
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
    @DisplayName("PATCH /api/v1/endpoint-permissions/{endpointPermissionId}/delete - 엔드포인트 권한 삭제")
    class DeleteEndpointPermissionDocs {

        @Test
        @DisplayName("엔드포인트 권한 삭제 API 문서")
        void deleteEndpointPermission() throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            DeleteEndpointPermissionCommand command =
                    new DeleteEndpointPermissionCommand(endpointPermissionId);

            given(mapper.toDeleteCommand(any(String.class))).willReturn(command);

            // when & then
            mockMvc.perform(
                            patch(
                                    "/api/v1/endpoint-permissions/{endpointPermissionId}/delete",
                                    endpointPermissionId))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "endpoint-permission-delete",
                                    pathParameters(
                                            parameterWithName("endpointPermissionId")
                                                    .description("엔드포인트 권한 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/endpoint-permissions/spec - 서비스별 엔드포인트 권한 스펙 조회")
    class GetEndpointPermissionSpecDocs {

        @Test
        @DisplayName("서비스별 엔드포인트 권한 스펙 조회 API 문서")
        void getEndpointPermissionSpec() throws Exception {
            // given
            String serviceName = "auth-hub";

            EndpointPermissionSpecItemResponse specItem1 =
                    new EndpointPermissionSpecItemResponse(
                            "POST",
                            "/api/v1/auth/login",
                            serviceName,
                            Set.of(),
                            Set.of(),
                            true,
                            false);

            EndpointPermissionSpecItemResponse specItem2 =
                    new EndpointPermissionSpecItemResponse(
                            "GET",
                            "/api/v1/users/{userId}",
                            serviceName,
                            Set.of("ADMIN", "USER_MANAGER"),
                            Set.of("user:read"),
                            false,
                            false);

            EndpointPermissionSpecListResponse useCaseResponse =
                    EndpointPermissionSpecListResponse.of(List.of(specItem1, specItem2));

            given(
                            getAllEndpointPermissionSpecUseCase.execute(
                                    any(GetServiceEndpointPermissionSpecQuery.class)))
                    .willReturn(useCaseResponse);

            // when & then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions/spec")
                                    .param("serviceName", serviceName)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "endpoint-permission-spec",
                                    queryParameters(
                                            parameterWithName("serviceName")
                                                    .description("서비스 이름 (필수)")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.endpoints")
                                                    .description("엔드포인트 권한 스펙 목록"),
                                            fieldWithPath("data.endpoints[].method")
                                                    .description("HTTP 메서드"),
                                            fieldWithPath("data.endpoints[].pattern")
                                                    .description("경로 패턴"),
                                            fieldWithPath("data.endpoints[].service")
                                                    .description("서비스 이름"),
                                            fieldWithPath("data.endpoints[].requiredRoles")
                                                    .description("필요 역할 목록 (OR 조건)"),
                                            fieldWithPath("data.endpoints[].requiredPermissions")
                                                    .description("필요 권한 목록 (OR 조건)"),
                                            fieldWithPath("data.endpoints[].isPublic")
                                                    .description("공개 여부 (true: 인증 불필요)"),
                                            fieldWithPath("data.endpoints[].requireMfa")
                                                    .description("MFA 필수 여부"),
                                            fieldWithPath("data.version")
                                                    .description("스펙 버전 (마지막 업데이트 시간, ISO-8601)"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/endpoint-permissions/{endpointPermissionId} - 엔드포인트 권한 단건 조회")
    class GetEndpointPermissionDocs {

        @Test
        @DisplayName("엔드포인트 권한 단건 조회 API 문서")
        void getEndpointPermission() throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();

            EndpointPermissionResponse useCaseResponse =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회 엔드포인트",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            1L,
                            now,
                            now);

            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회 엔드포인트",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            1L,
                            now,
                            now);

            given(mapper.toGetQuery(endpointPermissionId))
                    .willReturn(new GetEndpointPermissionQuery(endpointPermissionId));
            given(getEndpointPermissionUseCase.execute(any(GetEndpointPermissionQuery.class)))
                    .willReturn(useCaseResponse);
            given(mapper.toApiResponse(any(EndpointPermissionResponse.class)))
                    .willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            get(
                                            "/api/v1/endpoint-permissions/{endpointPermissionId}",
                                            endpointPermissionId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "endpoint-permission-get",
                                    pathParameters(
                                            parameterWithName("endpointPermissionId")
                                                    .description("엔드포인트 권한 ID")),
                                    responseFields(
                                            fieldWithPath("success").description("성공 여부"),
                                            fieldWithPath("data").description("응답 데이터"),
                                            fieldWithPath("data.id").description("엔드포인트 권한 ID"),
                                            fieldWithPath("data.serviceName").description("서비스 이름"),
                                            fieldWithPath("data.path").description("엔드포인트 경로"),
                                            fieldWithPath("data.method").description("HTTP 메서드"),
                                            fieldWithPath("data.description").description("설명"),
                                            fieldWithPath("data.isPublic")
                                                    .description("공개 여부 (true: 인증 불필요)"),
                                            fieldWithPath("data.requiredPermissions")
                                                    .description("필요 권한 목록 (OR 조건)"),
                                            fieldWithPath("data.requiredRoles")
                                                    .description("필요 역할 목록 (OR 조건)"),
                                            fieldWithPath("data.version").description("버전 (낙관적 락)"),
                                            fieldWithPath("data.createdAt").description("생성 일시"),
                                            fieldWithPath("data.updatedAt").description("수정 일시"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/endpoint-permissions - 엔드포인트 권한 목록 검색")
    class SearchEndpointPermissionsDocs {

        @Test
        @DisplayName("엔드포인트 권한 목록 검색 API 문서")
        void searchEndpointPermissions() throws Exception {
            // given
            String endpointPermissionId = UUID.randomUUID().toString();
            Instant now = Instant.now();

            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회 엔드포인트",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            1L,
                            now,
                            now);

            EndpointPermissionApiResponse apiResponse =
                    new EndpointPermissionApiResponse(
                            endpointPermissionId,
                            "auth-hub",
                            "/api/v1/users/{userId}",
                            "GET",
                            "사용자 상세 조회 엔드포인트",
                            false,
                            Set.of("user:read"),
                            Set.of("ADMIN"),
                            1L,
                            now,
                            now);

            given(mapper.toQuery(any(SearchEndpointPermissionsApiRequest.class)))
                    .willReturn(
                            new SearchEndpointPermissionsQuery(
                                    "auth-hub", "/api/v1", "GET", false, 0, 20));
            given(
                            searchEndpointPermissionsUseCase.execute(
                                    any(SearchEndpointPermissionsQuery.class)))
                    .willReturn(List.of(response));
            given(mapper.toApiResponseList(any())).willReturn(List.of(apiResponse));

            // when & then
            mockMvc.perform(
                            get("/api/v1/endpoint-permissions")
                                    .param("serviceName", "auth-hub")
                                    .param("pathPattern", "/api/v1")
                                    .param("method", "GET")
                                    .param("isPublic", "false")
                                    .param("page", "0")
                                    .param("size", "20")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "endpoint-permission-search",
                                    queryParameters(
                                            parameterWithName("serviceName")
                                                    .description("서비스 이름 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("pathPattern")
                                                    .description("경로 패턴 필터 (부분 일치, 선택)")
                                                    .optional(),
                                            parameterWithName("method")
                                                    .description("HTTP 메서드 필터 (선택)")
                                                    .optional(),
                                            parameterWithName("isPublic")
                                                    .description("공개 여부 필터 (선택)")
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
                                            fieldWithPath("data[]").description("엔드포인트 권한 목록"),
                                            fieldWithPath("data[].id").description("엔드포인트 권한 ID"),
                                            fieldWithPath("data[].serviceName")
                                                    .description("서비스 이름"),
                                            fieldWithPath("data[].path").description("엔드포인트 경로"),
                                            fieldWithPath("data[].method").description("HTTP 메서드"),
                                            fieldWithPath("data[].description").description("설명"),
                                            fieldWithPath("data[].isPublic")
                                                    .description("공개 여부 (true: 인증 불필요)"),
                                            fieldWithPath("data[].requiredPermissions")
                                                    .description("필요 권한 목록 (OR 조건)"),
                                            fieldWithPath("data[].requiredRoles")
                                                    .description("필요 역할 목록 (OR 조건)"),
                                            fieldWithPath("data[].version")
                                                    .description("버전 (낙관적 락)"),
                                            fieldWithPath("data[].createdAt").description("생성 일시"),
                                            fieldWithPath("data[].updatedAt").description("수정 일시"),
                                            fieldWithPath("error").description("에러 정보 (성공 시 null)"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
