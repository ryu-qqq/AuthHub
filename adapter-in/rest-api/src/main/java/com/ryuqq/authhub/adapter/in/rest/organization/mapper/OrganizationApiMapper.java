package com.ryuqq.authhub.adapter.in.rest.organization.mapper;

import org.springframework.stereotype.Component;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;

/**
 * Organization REST API ↔ Application Layer 변환 Mapper
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
public class OrganizationApiMapper {

    /**
     * CreateOrganizationApiRequest → CreateOrganizationCommand 변환
     *
     * @param request REST API 조직 생성 요청
     * @return Application Layer 조직 생성 명령
     */
    public CreateOrganizationCommand toCreateOrganizationCommand(CreateOrganizationApiRequest request) {
        return new CreateOrganizationCommand(
                request.tenantId(),
                request.name()
        );
    }
}
