package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import com.ryuqq.authhub.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * TenantCommandApiMapper - Tenant Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-002: API Request -> Application Command 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantCommandApiMapper {

    /**
     * CreateTenantApiRequest -> CreateTenantCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateTenantCommand toCommand(CreateTenantApiRequest request) {
        return new CreateTenantCommand(request.name());
    }

    /**
     * UpdateTenantNameApiRequest + PathVariable ID -> UpdateTenantNameCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param tenantId Tenant ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateTenantNameCommand toCommand(UUID tenantId, UpdateTenantNameApiRequest request) {
        return new UpdateTenantNameCommand(tenantId.toString(), request.name());
    }

    /**
     * UpdateTenantStatusApiRequest + PathVariable ID -> UpdateTenantStatusCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param tenantId Tenant ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateTenantStatusCommand toCommand(
            UUID tenantId, UpdateTenantStatusApiRequest request) {
        return new UpdateTenantStatusCommand(tenantId.toString(), request.status());
    }

    /**
     * TenantResult -> TenantApiResponse 변환
     *
     * <p>CFG-002: DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application Result DTO
     * @return API 응답 DTO
     */
    public TenantApiResponse toResponse(TenantResult result) {
        return new TenantApiResponse(
                result.tenantId().toString(),
                result.name(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }
}
