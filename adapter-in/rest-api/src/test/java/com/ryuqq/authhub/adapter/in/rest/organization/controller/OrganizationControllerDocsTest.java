package com.ryuqq.authhub.adapter.in.rest.organization.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationUserSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.mapper.OrganizationApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.DeleteOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationDetailUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.GetOrganizationUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationUsersUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsAdminUseCase;
import com.ryuqq.authhub.application.organization.port.in.query.SearchOrganizationsUseCase;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

/**
 * OrganizationController REST Docs 문서화 테스트
 *
 * <p>조직 관리 API의 Spring REST Docs 문서를 생성합니다.
 *
 * <p>문서화 대상 엔드포인트:
 *
 * <ul>
 *   <li>POST /api/v1/organizations - 조직 생성
 *   <li>PUT /api/v1/organizations/{organizationId} - 조직 수정
 *   <li>PATCH /api/v1/organizations/{organizationId}/status - 상태 변경
 *   <li>PATCH /api/v1/organizations/{organizationId}/delete - 조직 삭제
 *   <li>GET /api/v1/organizations/{organizationId} - 조직 단건 조회
 *   <li>GET /api/v1/organizations - 조직 목록 검색
 *   <li>GET /api/v1/auth/organizations/admin/search - 조직 목록 검색 (Admin)
 *   <li>GET /api/v1/auth/organizations/{organizationId}/admin/detail - 조직 상세 조회 (Admin)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest({OrganizationCommandController.class, OrganizationQueryController.class})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OrganizationController REST Docs")
@Tag("docs")
class OrganizationControllerDocsTest extends RestDocsTestSupport {

    @MockBean private CreateOrganizationUseCase createOrganizationUseCase;
    @MockBean private UpdateOrganizationUseCase updateOrganizationUseCase;
    @MockBean private UpdateOrganizationStatusUseCase updateOrganizationStatusUseCase;
    @MockBean private DeleteOrganizationUseCase deleteOrganizationUseCase;
    @MockBean private GetOrganizationUseCase getOrganizationUseCase;
    @MockBean private GetOrganizationDetailUseCase getOrganizationDetailUseCase;
    @MockBean private SearchOrganizationsUseCase searchOrganizationsUseCase;
    @MockBean private SearchOrganizationsAdminUseCase searchOrganizationsAdminUseCase;
    @MockBean private SearchOrganizationUsersUseCase searchOrganizationUsersUseCase;
    @MockBean private OrganizationApiMapper mapper;
    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Test
    @DisplayName("POST /api/v1/organizations - 조직 생성 API 문서")
    void createOrganization() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        CreateOrganizationApiRequest request =
                new CreateOrganizationApiRequest(tenantId.toString(), "TestOrganization");
        OrganizationResponse useCaseResponse =
                new OrganizationResponse(
                        organizationId,
                        tenantId,
                        "TestOrganization",
                        "ACTIVE",
                        Instant.now(),
                        Instant.now());
        CreateOrganizationApiResponse apiResponse =
                new CreateOrganizationApiResponse(organizationId.toString());

        given(mapper.toCommand(any(CreateOrganizationApiRequest.class)))
                .willReturn(new CreateOrganizationCommand(tenantId, "TestOrganization"));
        given(createOrganizationUseCase.execute(any(CreateOrganizationCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toCreateResponse(any(OrganizationResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        post("/api/v1/auth/organizations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "organization-create",
                                requestFields(
                                        fieldWithPath("tenantId").description("테넌트 ID (UUID 형식)"),
                                        fieldWithPath("name").description("조직 이름 (2-100자)")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.organizationId")
                                                .description("생성된 조직 ID"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PUT /api/v1/organizations/{organizationId} - 조직 수정 API 문서")
    void updateOrganization() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        UpdateOrganizationApiRequest request = new UpdateOrganizationApiRequest("NewOrgName");

        given(
                        mapper.toCommand(
                                eq(organizationId.toString()),
                                any(UpdateOrganizationApiRequest.class)))
                .willReturn(new UpdateOrganizationCommand(organizationId, "NewOrgName"));
        given(updateOrganizationUseCase.execute(any(UpdateOrganizationCommand.class)))
                .willReturn(null);

        // When & Then
        mockMvc.perform(
                        put("/api/v1/auth/organizations/{organizationId}", organizationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "organization-update",
                                pathParameters(
                                        parameterWithName("organizationId").description("조직 ID")),
                                requestFields(
                                        fieldWithPath("name").description("변경할 조직 이름 (2-100자)")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("응답 데이터 (수정 시 null)"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/organizations/{organizationId}/status - 조직 상태 변경 API 문서")
    void updateOrganizationStatus() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UpdateOrganizationStatusApiRequest request =
                new UpdateOrganizationStatusApiRequest("INACTIVE");
        OrganizationResponse useCaseResponse =
                new OrganizationResponse(
                        organizationId,
                        tenantId,
                        "TestOrg",
                        "INACTIVE",
                        Instant.now(),
                        Instant.now());
        OrganizationApiResponse apiResponse =
                new OrganizationApiResponse(
                        organizationId.toString(),
                        tenantId.toString(),
                        "TestOrg",
                        "INACTIVE",
                        Instant.now(),
                        Instant.now());

        given(
                        mapper.toStatusCommand(
                                eq(organizationId.toString()),
                                any(UpdateOrganizationStatusApiRequest.class)))
                .willReturn(new UpdateOrganizationStatusCommand(organizationId, "INACTIVE"));
        given(updateOrganizationStatusUseCase.execute(any(UpdateOrganizationStatusCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        patch("/api/v1/auth/organizations/{organizationId}/status", organizationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "organization-update-status",
                                pathParameters(
                                        parameterWithName("organizationId").description("조직 ID")),
                                requestFields(
                                        fieldWithPath("status")
                                                .description("변경할 상태 (ACTIVE 또는 INACTIVE)")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("변경된 조직 정보"),
                                        fieldWithPath("data.organizationId").description("조직 ID"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.name").description("조직 이름"),
                                        fieldWithPath("data.status").description("조직 상태"),
                                        fieldWithPath("data.createdAt").description("생성 시각"),
                                        fieldWithPath("data.updatedAt").description("수정 시각"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/organizations/{organizationId}/delete - 조직 삭제 API 문서")
    void deleteOrganization() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        DeleteOrganizationCommand command = new DeleteOrganizationCommand(organizationId);

        given(mapper.toDeleteCommand(any(String.class))).willReturn(command);
        willDoNothing()
                .given(deleteOrganizationUseCase)
                .execute(any(DeleteOrganizationCommand.class));

        // When & Then
        mockMvc.perform(patch("/api/v1/auth/organizations/{organizationId}/delete", organizationId))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "organization-delete",
                                pathParameters(
                                        parameterWithName("organizationId")
                                                .description("삭제할 조직 ID"))));
    }

    @Test
    @DisplayName("GET /api/v1/organizations/{organizationId} - 조직 단건 조회 API 문서")
    void getOrganization() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        Instant now = Instant.now();
        OrganizationResponse useCaseResponse =
                new OrganizationResponse(organizationId, tenantId, "TestOrg", "ACTIVE", now, now);
        OrganizationApiResponse apiResponse =
                new OrganizationApiResponse(
                        organizationId.toString(),
                        tenantId.toString(),
                        "TestOrg",
                        "ACTIVE",
                        now,
                        now);

        given(mapper.toGetQuery(organizationId.toString()))
                .willReturn(new GetOrganizationQuery(organizationId));
        given(getOrganizationUseCase.execute(any(GetOrganizationQuery.class)))
                .willReturn(useCaseResponse);
        given(mapper.toApiResponse(useCaseResponse)).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/organizations/{organizationId}", organizationId))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "organization-get",
                                pathParameters(
                                        parameterWithName("organizationId").description("조직 ID")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("조직 정보"),
                                        fieldWithPath("data.organizationId").description("조직 ID"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.name").description("조직 이름"),
                                        fieldWithPath("data.status").description("조직 상태"),
                                        fieldWithPath("data.createdAt").description("생성 시각"),
                                        fieldWithPath("data.updatedAt").description("수정 시각"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("GET /api/v1/organizations - 조직 목록 검색 API 문서")
    void searchOrganizations() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant oneMonthAgo = now.minus(30, ChronoUnit.DAYS);

        OrganizationResponse orgResponse =
                new OrganizationResponse(organizationId, tenantId, "TestOrg", "ACTIVE", now, now);
        PageResponse<OrganizationResponse> pageResponse =
                PageResponse.of(List.of(orgResponse), 0, 20, 1L, 1, true, true);

        OrganizationApiResponse apiOrgResponse =
                new OrganizationApiResponse(
                        organizationId.toString(),
                        tenantId.toString(),
                        "TestOrg",
                        "ACTIVE",
                        now,
                        now);

        given(mapper.toQuery(any()))
                .willReturn(
                        SearchOrganizationsQuery.of(tenantId, null, null, oneMonthAgo, now, 0, 20));
        given(searchOrganizationsUseCase.execute(any(SearchOrganizationsQuery.class)))
                .willReturn(pageResponse);
        given(mapper.toApiResponse(any(OrganizationResponse.class))).willReturn(apiOrgResponse);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/auth/organizations")
                                .param("tenantId", tenantId.toString())
                                .param("createdFrom", oneMonthAgo.toString())
                                .param("createdTo", now.toString()))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "organization-search",
                                queryParameters(
                                        parameterWithName("tenantId").description("테넌트 ID (필수)"),
                                        parameterWithName("name")
                                                .description("조직 이름 필터 (선택)")
                                                .optional(),
                                        parameterWithName("statuses")
                                                .description("상태 필터 (다중 선택 가능, ACTIVE/INACTIVE)")
                                                .optional(),
                                        parameterWithName("createdFrom")
                                                .description("생성일시 시작 (필수)"),
                                        parameterWithName("createdTo").description("생성일시 종료 (필수)"),
                                        parameterWithName("page")
                                                .description("페이지 번호 (기본값: 0)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (기본값: 20, 최대: 100)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("페이징된 조직 목록"),
                                        fieldWithPath("data.content").description("조직 목록"),
                                        fieldWithPath("data.content[].organizationId")
                                                .description("조직 ID"),
                                        fieldWithPath("data.content[].tenantId")
                                                .description("테넌트 ID"),
                                        fieldWithPath("data.content[].name").description("조직 이름"),
                                        fieldWithPath("data.content[].status").description("조직 상태"),
                                        fieldWithPath("data.content[].createdAt")
                                                .description("생성 시각"),
                                        fieldWithPath("data.content[].updatedAt")
                                                .description("수정 시각"),
                                        fieldWithPath("data.page").description("현재 페이지 번호"),
                                        fieldWithPath("data.size").description("페이지 크기"),
                                        fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                        fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("data.first").description("첫 페이지 여부"),
                                        fieldWithPath("data.last").description("마지막 페이지 여부"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("GET /api/v1/auth/organizations/admin/search - Admin 조직 목록 검색 API 문서")
    void searchOrganizationsAdmin() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant oneMonthAgo = now.minus(30, ChronoUnit.DAYS);

        OrganizationSummaryResponse summaryResponse =
                new OrganizationSummaryResponse(
                        organizationId, tenantId, "TestTenant", "TestOrg", "ACTIVE", 5, now, now);
        PageResponse<OrganizationSummaryResponse> pageResponse =
                PageResponse.of(List.of(summaryResponse), 0, 20, 1L, 1, true, true);

        OrganizationSummaryApiResponse apiSummaryResponse =
                new OrganizationSummaryApiResponse(
                        organizationId.toString(),
                        tenantId.toString(),
                        "TestTenant",
                        "TestOrg",
                        "ACTIVE",
                        5,
                        now,
                        now);

        given(mapper.toAdminQuery(any(SearchOrganizationsAdminApiRequest.class)))
                .willReturn(
                        SearchOrganizationsQuery.ofAdmin(
                                tenantId, null, null, null, oneMonthAgo, now, null, null, 0, 20));
        given(searchOrganizationsAdminUseCase.execute(any(SearchOrganizationsQuery.class)))
                .willReturn(pageResponse);
        given(mapper.toSummaryApiResponse(any(OrganizationSummaryResponse.class)))
                .willReturn(apiSummaryResponse);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/auth/organizations/admin/search")
                                .param("tenantId", tenantId.toString())
                                .param("createdFrom", oneMonthAgo.toString())
                                .param("createdTo", now.toString())
                                .param("page", "0")
                                .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "organization-admin-search",
                                queryParameters(
                                        parameterWithName("tenantId")
                                                .description("테넌트 ID 필터 (선택)")
                                                .optional(),
                                        parameterWithName("name")
                                                .description("조직 이름 필터 (선택)")
                                                .optional(),
                                        parameterWithName("searchType")
                                                .description(
                                                        "검색 타입 (CONTAINS_LIKE/PREFIX_LIKE, 선택)")
                                                .optional(),
                                        parameterWithName("statuses")
                                                .description(
                                                        "상태 필터 (다중 선택 가능,"
                                                                + " ACTIVE/INACTIVE/SUSPENDED)")
                                                .optional(),
                                        parameterWithName("createdFrom")
                                                .description("생성일시 시작 (선택)")
                                                .optional(),
                                        parameterWithName("createdTo")
                                                .description("생성일시 종료 (선택)")
                                                .optional(),
                                        parameterWithName("sortBy")
                                                .description(
                                                        "정렬 기준 (name, status, createdAt, updatedAt,"
                                                                + " 기본값: createdAt)")
                                                .optional(),
                                        parameterWithName("sortDirection")
                                                .description("정렬 방향 (ASC/DESC, 기본값: DESC)")
                                                .optional(),
                                        parameterWithName("page")
                                                .description("페이지 번호 (기본값: 0)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (기본값: 20, 최대: 100)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("페이징된 조직 목록"),
                                        fieldWithPath("data.content").description("조직 목록"),
                                        fieldWithPath("data.content[].organizationId")
                                                .description("조직 ID"),
                                        fieldWithPath("data.content[].tenantId")
                                                .description("테넌트 ID"),
                                        fieldWithPath("data.content[].tenantName")
                                                .description("테넌트 이름"),
                                        fieldWithPath("data.content[].name").description("조직 이름"),
                                        fieldWithPath("data.content[].status")
                                                .description("조직 상태 (ACTIVE/INACTIVE/SUSPENDED)"),
                                        fieldWithPath("data.content[].userCount")
                                                .description("조직에 소속된 사용자 수"),
                                        fieldWithPath("data.content[].createdAt")
                                                .description("생성 시각"),
                                        fieldWithPath("data.content[].updatedAt")
                                                .description("수정 시각"),
                                        fieldWithPath("data.page").description("현재 페이지 번호"),
                                        fieldWithPath("data.size").description("페이지 크기"),
                                        fieldWithPath("data.totalElements").description("전체 데이터 수"),
                                        fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("data.first").description("첫 페이지 여부"),
                                        fieldWithPath("data.last").description("마지막 페이지 여부"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName(
            "GET /api/v1/auth/organizations/{organizationId}/admin/detail - Admin 조직 상세 조회 API 문서")
    void getOrganizationDetail() throws Exception {
        // Given
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();

        OrganizationDetailResponse.OrganizationUserSummary userSummary =
                new OrganizationDetailResponse.OrganizationUserSummary(
                        userId, "user@example.com", now);
        OrganizationDetailResponse detailResponse =
                new OrganizationDetailResponse(
                        organizationId,
                        tenantId,
                        "TestTenant",
                        "TestOrg",
                        "ACTIVE",
                        List.of(userSummary),
                        1,
                        now,
                        now);

        OrganizationUserSummaryApiResponse apiUserSummary =
                new OrganizationUserSummaryApiResponse(userId.toString(), "user@example.com", now);
        OrganizationDetailApiResponse apiDetailResponse =
                new OrganizationDetailApiResponse(
                        organizationId.toString(),
                        tenantId.toString(),
                        "TestTenant",
                        "TestOrg",
                        "ACTIVE",
                        List.of(apiUserSummary),
                        1,
                        now,
                        now);

        given(mapper.toGetQuery(organizationId.toString()))
                .willReturn(new GetOrganizationQuery(organizationId));
        given(getOrganizationDetailUseCase.execute(any(GetOrganizationQuery.class)))
                .willReturn(detailResponse);
        given(mapper.toDetailApiResponse(any(OrganizationDetailResponse.class)))
                .willReturn(apiDetailResponse);

        // When & Then
        mockMvc.perform(
                        get(
                                "/api/v1/auth/organizations/{organizationId}/admin/detail",
                                organizationId))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "organization-admin-detail",
                                pathParameters(
                                        parameterWithName("organizationId").description("조직 ID")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("조직 상세 정보"),
                                        fieldWithPath("data.organizationId").description("조직 ID"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.tenantName").description("테넌트 이름"),
                                        fieldWithPath("data.name").description("조직 이름"),
                                        fieldWithPath("data.status")
                                                .description("조직 상태 (ACTIVE/INACTIVE/SUSPENDED)"),
                                        fieldWithPath("data.users")
                                                .description("소속 사용자 목록 (최근 N명)"),
                                        fieldWithPath("data.users[].userId").description("사용자 ID"),
                                        fieldWithPath("data.users[].email").description("사용자 이메일"),
                                        fieldWithPath("data.users[].createdAt")
                                                .description("소속 일시"),
                                        fieldWithPath("data.userCount")
                                                .description("조직에 소속된 전체 사용자 수"),
                                        fieldWithPath("data.createdAt").description("생성 시각"),
                                        fieldWithPath("data.updatedAt").description("수정 시각"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }
}
