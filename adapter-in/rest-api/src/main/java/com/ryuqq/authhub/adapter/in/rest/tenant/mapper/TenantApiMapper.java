package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import org.springframework.stereotype.Component;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;

/**
 * Tenant REST API ↔ Application Layer 변환 Mapper
 *
 * <p>REST API Layer와 Application Layer 간의 DTO 변환을 담당합니다.
 *
 * <p><strong>변환 방향:</strong>
 * <ul>
 *   <li>API Request → Command (Controller → Application)</li>
 * </ul>
 *
 * <p><strong>Response 변환:</strong>
 * <ul>
 *   <li>Application Response → API Response 변환은 각 Response DTO의 from() 메서드 사용</li>
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
     * @param request REST API 테넌트 생성 요청
     * @return Application Layer 테넌트 생성 명령
     */
    public CreateTenantCommand toCreateTenantCommand(CreateTenantApiRequest request) {
        return new CreateTenantCommand(request.name());
    }
}
