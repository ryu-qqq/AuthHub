package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.query.SearchTenantsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
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
}
