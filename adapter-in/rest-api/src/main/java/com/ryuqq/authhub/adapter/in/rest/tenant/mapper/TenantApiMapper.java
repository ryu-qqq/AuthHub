package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantOrganizationSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantSummaryApiResponse;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * TenantApiMapper - 테넌트 API DTO 변환기
 *
 * <p>REST API DTO와 Application DTO 간의 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 *   <li>단순 변환만 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantApiMapper {

    /**
     * CreateTenantApiRequest → CreateTenantCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateTenantCommand toCommand(CreateTenantApiRequest request) {
        return new CreateTenantCommand(request.name());
    }

    /**
     * SearchTenantsApiRequest → SearchTenantsQuery 변환
     *
     * @param request API 요청 DTO
     * @return Application Query DTO
     */
    public SearchTenantsQuery toQuery(SearchTenantsApiRequest request) {
        return new SearchTenantsQuery(
                request.name(), request.status(), request.page(), request.size());
    }

    /**
     * TenantResponse → CreateTenantApiResponse 변환
     *
     * @param response 생성된 테넌트 Response
     * @return API 응답 DTO
     */
    public CreateTenantApiResponse toCreateResponse(TenantResponse response) {
        return new CreateTenantApiResponse(response.tenantId().toString());
    }

    /**
     * TenantResponse → TenantApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public TenantApiResponse toApiResponse(TenantResponse response) {
        return new TenantApiResponse(
                response.tenantId().toString(),
                response.name(),
                response.status(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * TenantResponse 목록 → TenantApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<TenantApiResponse> toApiResponseList(List<TenantResponse> responses) {
        return responses.stream().map(this::toApiResponse).toList();
    }

    /**
     * UpdateTenantNameApiRequest → UpdateTenantNameCommand 변환
     *
     * @param tenantId 테넌트 ID
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateTenantNameCommand toCommand(UUID tenantId, UpdateTenantNameApiRequest request) {
        return new UpdateTenantNameCommand(tenantId, request.name());
    }

    /**
     * UpdateTenantStatusApiRequest → UpdateTenantStatusCommand 변환
     *
     * @param tenantId 테넌트 ID
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateTenantStatusCommand toCommand(
            UUID tenantId, UpdateTenantStatusApiRequest request) {
        return new UpdateTenantStatusCommand(tenantId, request.status());
    }

    /**
     * DeleteTenantCommand 생성
     *
     * @param tenantId 테넌트 ID
     * @return Application Command DTO
     */
    public DeleteTenantCommand toDeleteCommand(UUID tenantId) {
        return new DeleteTenantCommand(tenantId);
    }

    // ==================== Admin Query 변환 ====================

    /**
     * SearchTenantsAdminApiRequest → SearchTenantsQuery 변환 (Admin용)
     *
     * <p>확장 필터(날짜 범위, 정렬)가 포함된 Query로 변환합니다.
     *
     * @param request Admin API 요청 DTO
     * @return Application Query DTO
     */
    public SearchTenantsQuery toAdminQuery(SearchTenantsAdminApiRequest request) {
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : SearchTenantsQuery.DEFAULT_PAGE_SIZE;
        String sortBy =
                request.sortBy() != null ? request.sortBy() : SearchTenantsQuery.DEFAULT_SORT_BY;
        String sortDirection =
                request.sortDirection() != null
                        ? request.sortDirection()
                        : SearchTenantsQuery.DEFAULT_SORT_DIRECTION;

        return new SearchTenantsQuery(
                request.name(),
                request.status(),
                request.createdFrom(),
                request.createdTo(),
                sortBy,
                sortDirection,
                page,
                size);
    }

    /**
     * TenantSummaryResponse → TenantSummaryApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public TenantSummaryApiResponse toSummaryApiResponse(TenantSummaryResponse response) {
        return new TenantSummaryApiResponse(
                response.tenantId().toString(),
                response.name(),
                response.status(),
                response.organizationCount(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * PageResponse<TenantSummaryResponse> → PageResponse<TenantSummaryApiResponse> 변환
     *
     * @param pageResponse Application PageResponse
     * @return API PageResponse
     */
    public PageResponse<TenantSummaryApiResponse> toSummaryApiPageResponse(
            PageResponse<TenantSummaryResponse> pageResponse) {
        List<TenantSummaryApiResponse> content =
                pageResponse.content().stream().map(this::toSummaryApiResponse).toList();

        return PageResponse.of(
                content,
                pageResponse.page(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                pageResponse.first(),
                pageResponse.last());
    }

    /**
     * TenantDetailResponse → TenantDetailApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public TenantDetailApiResponse toDetailApiResponse(TenantDetailResponse response) {
        List<TenantOrganizationSummaryApiResponse> organizations =
                response.organizations().stream()
                        .map(this::toOrganizationSummaryApiResponse)
                        .toList();

        return new TenantDetailApiResponse(
                response.tenantId().toString(),
                response.name(),
                response.status(),
                organizations,
                response.organizationCount(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * TenantOrganizationSummary → TenantOrganizationSummaryApiResponse 변환
     *
     * @param summary Application DTO
     * @return API 응답 DTO
     */
    private TenantOrganizationSummaryApiResponse toOrganizationSummaryApiResponse(
            TenantDetailResponse.TenantOrganizationSummary summary) {
        return new TenantOrganizationSummaryApiResponse(
                summary.organizationId().toString(),
                summary.name(),
                summary.status(),
                summary.createdAt());
    }
}
