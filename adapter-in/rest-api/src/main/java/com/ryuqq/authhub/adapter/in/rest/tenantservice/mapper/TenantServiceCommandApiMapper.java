package com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper;

import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SubscribeTenantServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.UpdateTenantServiceStatusApiRequest;
import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import org.springframework.stereotype.Component;

/**
 * TenantServiceCommandApiMapper - TenantService Command API 변환 매퍼
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
public class TenantServiceCommandApiMapper {

    /**
     * SubscribeTenantServiceApiRequest -> SubscribeTenantServiceCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public SubscribeTenantServiceCommand toCommand(SubscribeTenantServiceApiRequest request) {
        return new SubscribeTenantServiceCommand(request.tenantId(), request.serviceId());
    }

    /**
     * UpdateTenantServiceStatusApiRequest + PathVariable ID -> UpdateTenantServiceStatusCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param tenantServiceId TenantService ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateTenantServiceStatusCommand toCommand(
            Long tenantServiceId, UpdateTenantServiceStatusApiRequest request) {
        return new UpdateTenantServiceStatusCommand(tenantServiceId, request.status());
    }
}
