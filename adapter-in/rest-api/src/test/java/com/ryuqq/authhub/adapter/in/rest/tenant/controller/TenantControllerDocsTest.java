package com.ryuqq.authhub.adapter.in.rest.tenant.controller;

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
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantOrganizationSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.mapper.TenantApiMapper;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.DeleteTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantNameUseCase;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantStatusUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantDetailUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.GetTenantUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsAdminUseCase;
import com.ryuqq.authhub.application.tenant.port.in.query.SearchTenantsUseCase;
import java.time.Instant;
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
 * TenantController REST API 문서화 테스트
 *
 * <p>Spring REST Docs를 사용하여 Tenant API의 문서를 생성합니다.
 *
 * <p>생성되는 문서:
 *
 * <ul>
 *   <li>tenant-create: POST /api/v1/tenants - 테넌트 생성
 *   <li>tenant-update-name: PUT /api/v1/tenants/{tenantId}/name - 이름 수정
 *   <li>tenant-update-status: PATCH /api/v1/tenants/{tenantId}/status - 상태 변경
 *   <li>tenant-delete: PATCH /api/v1/tenants/{tenantId}/delete - 삭제
 *   <li>tenant-get: GET /api/v1/tenants/{tenantId} - 단건 조회
 *   <li>tenant-search: GET /api/v1/tenants - 목록 조회
 *   <li>tenant-admin-search: GET /api/v1/auth/tenants/admin/search - 테넌트 목록 검색 (Admin)
 *   <li>tenant-admin-detail: GET /api/v1/auth/tenants/{tenantId}/admin/detail - 테넌트 상세 조회 (Admin)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest({TenantCommandController.class, TenantQueryController.class})
@Import(ControllerTestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TenantController REST Docs")
@Tag("docs")
class TenantControllerDocsTest extends RestDocsTestSupport {

    @MockBean private CreateTenantUseCase createTenantUseCase;

    @MockBean private UpdateTenantNameUseCase updateTenantNameUseCase;

    @MockBean private UpdateTenantStatusUseCase updateTenantStatusUseCase;

    @MockBean private DeleteTenantUseCase deleteTenantUseCase;

    @MockBean private GetTenantUseCase getTenantUseCase;

    @MockBean private GetTenantDetailUseCase getTenantDetailUseCase;

    @MockBean private SearchTenantsUseCase searchTenantsUseCase;

    @MockBean private SearchTenantsAdminUseCase searchTenantsAdminUseCase;

    @MockBean private TenantApiMapper mapper;

    @MockBean private ErrorMapperRegistry errorMapperRegistry;

    @Test
    @DisplayName("POST /api/v1/tenants - 테넌트 생성 API 문서")
    void createTenant() throws Exception {
        // Given
        CreateTenantApiRequest request = new CreateTenantApiRequest("TestTenant");
        UUID tenantId = UUID.randomUUID();
        TenantResponse useCaseResponse =
                new TenantResponse(tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
        CreateTenantApiResponse apiResponse = new CreateTenantApiResponse(tenantId.toString());

        given(mapper.toCommand(any(CreateTenantApiRequest.class)))
                .willReturn(new CreateTenantCommand("TestTenant"));
        given(createTenantUseCase.execute(any(CreateTenantCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toCreateResponse(any(TenantResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        post("/api/v1/auth/tenants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "tenant-create",
                                requestFields(
                                        fieldWithPath("name")
                                                .description("테넌트 이름 (2-100자)")
                                                .attributes()),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.tenantId").description("생성된 테넌트 ID"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PUT /api/v1/tenants/{tenantId}/name - 테넌트 이름 변경 API 문서")
    void updateTenantName() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        UpdateTenantNameApiRequest request = new UpdateTenantNameApiRequest("NewTenantName");
        TenantResponse useCaseResponse =
                new TenantResponse(
                        tenantId, "NewTenantName", "ACTIVE", Instant.now(), Instant.now());
        TenantApiResponse apiResponse =
                new TenantApiResponse(
                        tenantId.toString(),
                        "NewTenantName",
                        "ACTIVE",
                        Instant.now(),
                        Instant.now());

        given(mapper.toCommand(eq(tenantId), any(UpdateTenantNameApiRequest.class)))
                .willReturn(new UpdateTenantNameCommand(tenantId, "NewTenantName"));
        given(updateTenantNameUseCase.execute(any(UpdateTenantNameCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        put("/api/v1/auth/tenants/{tenantId}/name", tenantId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "tenant-update-name",
                                pathParameters(
                                        parameterWithName("tenantId").description("테넌트 ID (UUID)")),
                                requestFields(
                                        fieldWithPath("name")
                                                .description("새 테넌트 이름 (2-100자)")
                                                .attributes()),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.name").description("변경된 테넌트 이름"),
                                        fieldWithPath("data.status").description("테넌트 상태"),
                                        fieldWithPath("data.createdAt").description("생성 일시"),
                                        fieldWithPath("data.updatedAt").description("수정 일시"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/tenants/{tenantId}/status - 테넌트 상태 변경 API 문서")
    void updateTenantStatus() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        UpdateTenantStatusApiRequest request = new UpdateTenantStatusApiRequest("INACTIVE");
        TenantResponse useCaseResponse =
                new TenantResponse(
                        tenantId, "TestTenant", "INACTIVE", Instant.now(), Instant.now());
        TenantApiResponse apiResponse =
                new TenantApiResponse(
                        tenantId.toString(),
                        "TestTenant",
                        "INACTIVE",
                        Instant.now(),
                        Instant.now());

        given(mapper.toCommand(eq(tenantId), any(UpdateTenantStatusApiRequest.class)))
                .willReturn(new UpdateTenantStatusCommand(tenantId, "INACTIVE"));
        given(updateTenantStatusUseCase.execute(any(UpdateTenantStatusCommand.class)))
                .willReturn(useCaseResponse);
        given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        patch("/api/v1/auth/tenants/{tenantId}/status", tenantId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "tenant-update-status",
                                pathParameters(
                                        parameterWithName("tenantId").description("테넌트 ID (UUID)")),
                                requestFields(
                                        fieldWithPath("status")
                                                .description("변경할 상태 (ACTIVE, INACTIVE)")
                                                .attributes()),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.name").description("테넌트 이름"),
                                        fieldWithPath("data.status").description("변경된 테넌트 상태"),
                                        fieldWithPath("data.createdAt").description("생성 일시"),
                                        fieldWithPath("data.updatedAt").description("수정 일시"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("PATCH /api/v1/tenants/{tenantId}/delete - 테넌트 삭제 API 문서")
    void deleteTenant() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        DeleteTenantCommand command = new DeleteTenantCommand(tenantId);

        given(mapper.toDeleteCommand(any(UUID.class))).willReturn(command);
        willDoNothing().given(deleteTenantUseCase).execute(any(DeleteTenantCommand.class));

        // When & Then
        mockMvc.perform(patch("/api/v1/auth/tenants/{tenantId}/delete", tenantId))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "tenant-delete",
                                pathParameters(
                                        parameterWithName("tenantId")
                                                .description("테넌트 ID (UUID)"))));
    }

    @Test
    @DisplayName("GET /api/v1/tenants/{tenantId} - 테넌트 단건 조회 API 문서")
    void getTenant() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        TenantResponse useCaseResponse =
                new TenantResponse(tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
        TenantApiResponse apiResponse =
                new TenantApiResponse(
                        tenantId.toString(), "TestTenant", "ACTIVE", Instant.now(), Instant.now());

        given(getTenantUseCase.execute(any(GetTenantQuery.class))).willReturn(useCaseResponse);
        given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/auth/tenants/{tenantId}", tenantId)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "tenant-get",
                                pathParameters(
                                        parameterWithName("tenantId").description("테넌트 ID (UUID)")),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.name").description("테넌트 이름"),
                                        fieldWithPath("data.status").description("테넌트 상태"),
                                        fieldWithPath("data.createdAt").description("생성 일시"),
                                        fieldWithPath("data.updatedAt").description("수정 일시"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("GET /api/v1/tenants - 테넌트 목록 조회 API 문서")
    void searchTenants() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        TenantResponse response =
                new TenantResponse(tenantId, "TestTenant", "ACTIVE", Instant.now(), Instant.now());
        PageResponse<TenantResponse> pageResponse =
                PageResponse.of(List.of(response), 0, 20, 1, 1, true, true);
        TenantApiResponse apiResponse =
                new TenantApiResponse(
                        tenantId.toString(), "TestTenant", "ACTIVE", Instant.now(), Instant.now());

        given(mapper.toQuery(any())).willReturn(new SearchTenantsQuery("Test", "ACTIVE", 0, 10));
        given(searchTenantsUseCase.execute(any(SearchTenantsQuery.class))).willReturn(pageResponse);
        given(mapper.toApiResponse(any(TenantResponse.class))).willReturn(apiResponse);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/auth/tenants")
                                .param("name", "Test")
                                .param("status", "ACTIVE")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "tenant-search",
                                queryParameters(
                                        parameterWithName("name")
                                                .description("테넌트 이름 필터 (부분 일치)")
                                                .optional(),
                                        parameterWithName("status")
                                                .description("상태 필터 (ACTIVE, INACTIVE)")
                                                .optional(),
                                        parameterWithName("page")
                                                .description("페이지 번호 (0부터 시작, 기본값: 0)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (1-100, 기본값: 20)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("data").description("응답 데이터"),
                                        fieldWithPath("data.content[]")
                                                .description("현재 페이지 데이터 목록"),
                                        fieldWithPath("data.content[].tenantId")
                                                .description("테넌트 ID"),
                                        fieldWithPath("data.content[].name").description("테넌트 이름"),
                                        fieldWithPath("data.content[].status")
                                                .description("테넌트 상태"),
                                        fieldWithPath("data.content[].createdAt")
                                                .description("생성 일시"),
                                        fieldWithPath("data.content[].updatedAt")
                                                .description("수정 일시"),
                                        fieldWithPath("data.page")
                                                .description("현재 페이지 번호 (0부터 시작)"),
                                        fieldWithPath("data.size").description("페이지 크기"),
                                        fieldWithPath("data.totalElements")
                                                .description("전체 데이터 개수"),
                                        fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("data.first").description("첫 페이지 여부"),
                                        fieldWithPath("data.last").description("마지막 페이지 여부"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }

    @Test
    @DisplayName("GET /api/v1/auth/tenants/admin/search - Admin 테넌트 목록 검색 API 문서")
    void searchTenantsAdmin() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        Instant now = Instant.now();

        TenantSummaryResponse summaryResponse =
                new TenantSummaryResponse(tenantId, "TestTenant", "ACTIVE", 5, now, now);
        PageResponse<TenantSummaryResponse> pageResponse =
                PageResponse.of(List.of(summaryResponse), 0, 20, 1L, 1, true, true);

        TenantSummaryApiResponse apiSummaryResponse =
                new TenantSummaryApiResponse(
                        tenantId.toString(), "TestTenant", "ACTIVE", 5, now, now);
        PageResponse<TenantSummaryApiResponse> apiPageResponse =
                PageResponse.of(List.of(apiSummaryResponse), 0, 20, 1L, 1, true, true);

        given(mapper.toAdminQuery(any(SearchTenantsAdminApiRequest.class)))
                .willReturn(new SearchTenantsQuery("Test", "ACTIVE", 0, 20));
        given(searchTenantsAdminUseCase.execute(any(SearchTenantsQuery.class)))
                .willReturn(pageResponse);
        given(mapper.toSummaryApiPageResponse(any())).willReturn(apiPageResponse);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/auth/tenants/admin/search")
                                .param("name", "Test")
                                .param("status", "ACTIVE")
                                .param("page", "0")
                                .param("size", "20"))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "tenant-admin-search",
                                queryParameters(
                                        parameterWithName("name")
                                                .description("테넌트 이름 필터 (선택)")
                                                .optional(),
                                        parameterWithName("status")
                                                .description("테넌트 상태 필터 (선택, ACTIVE/INACTIVE)")
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
                                        fieldWithPath("data").description("페이징된 테넌트 목록"),
                                        fieldWithPath("data.content").description("테넌트 목록"),
                                        fieldWithPath("data.content[].tenantId")
                                                .description("테넌트 ID"),
                                        fieldWithPath("data.content[].name").description("테넌트 이름"),
                                        fieldWithPath("data.content[].status")
                                                .description("테넌트 상태 (ACTIVE/INACTIVE)"),
                                        fieldWithPath("data.content[].organizationCount")
                                                .description("테넌트에 소속된 조직 수"),
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
    @DisplayName("GET /api/v1/auth/tenants/{tenantId}/admin/detail - Admin 테넌트 상세 조회 API 문서")
    void getTenantDetail() throws Exception {
        // Given
        UUID tenantId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        Instant now = Instant.now();

        TenantDetailResponse.TenantOrganizationSummary orgSummary =
                new TenantDetailResponse.TenantOrganizationSummary(
                        organizationId, "TestOrg", "ACTIVE", now);
        TenantDetailResponse detailResponse =
                new TenantDetailResponse(
                        tenantId, "TestTenant", "ACTIVE", List.of(orgSummary), 1, now, now);

        TenantOrganizationSummaryApiResponse apiOrgSummary =
                new TenantOrganizationSummaryApiResponse(
                        organizationId.toString(), "TestOrg", "ACTIVE", now);
        TenantDetailApiResponse apiDetailResponse =
                new TenantDetailApiResponse(
                        tenantId.toString(),
                        "TestTenant",
                        "ACTIVE",
                        List.of(apiOrgSummary),
                        1,
                        now,
                        now);

        given(getTenantDetailUseCase.execute(any(GetTenantQuery.class))).willReturn(detailResponse);
        given(mapper.toDetailApiResponse(any(TenantDetailResponse.class)))
                .willReturn(apiDetailResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/tenants/{tenantId}/admin/detail", tenantId))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "tenant-admin-detail",
                                pathParameters(parameterWithName("tenantId").description("테넌트 ID")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("data").description("테넌트 상세 정보"),
                                        fieldWithPath("data.tenantId").description("테넌트 ID"),
                                        fieldWithPath("data.name").description("테넌트 이름"),
                                        fieldWithPath("data.status")
                                                .description("테넌트 상태 (ACTIVE/INACTIVE)"),
                                        fieldWithPath("data.organizations")
                                                .description("소속 조직 목록 (최근 N개)"),
                                        fieldWithPath("data.organizations[].organizationId")
                                                .description("조직 ID"),
                                        fieldWithPath("data.organizations[].name")
                                                .description("조직 이름"),
                                        fieldWithPath("data.organizations[].status")
                                                .description("조직 상태 (ACTIVE/INACTIVE/SUSPENDED)"),
                                        fieldWithPath("data.organizations[].createdAt")
                                                .description("생성 일시"),
                                        fieldWithPath("data.organizationCount")
                                                .description("테넌트에 소속된 전체 조직 수"),
                                        fieldWithPath("data.createdAt").description("생성 시각"),
                                        fieldWithPath("data.updatedAt").description("수정 시각"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("requestId").description("요청 ID"))));
    }
}
