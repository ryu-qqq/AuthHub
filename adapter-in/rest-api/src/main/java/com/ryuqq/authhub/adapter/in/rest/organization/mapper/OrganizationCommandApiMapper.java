package com.ryuqq.authhub.adapter.in.rest.organization.mapper;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationNameCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import org.springframework.stereotype.Component;

/**
 * OrganizationCommandApiMapper - Organization Command API 변환 매퍼
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
public class OrganizationCommandApiMapper {

    /**
     * CreateOrganizationApiRequest -> CreateOrganizationCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateOrganizationCommand toCommand(CreateOrganizationApiRequest request) {
        return new CreateOrganizationCommand(request.tenantId(), request.name());
    }

    /**
     * UpdateOrganizationNameApiRequest + PathVariable ID -> UpdateOrganizationNameCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param organizationId Organization ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateOrganizationNameCommand toCommand(
            String organizationId, UpdateOrganizationNameApiRequest request) {
        return new UpdateOrganizationNameCommand(organizationId, request.name());
    }

    /**
     * UpdateOrganizationStatusApiRequest + PathVariable ID -> UpdateOrganizationStatusCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param organizationId Organization ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateOrganizationStatusCommand toCommand(
            String organizationId, UpdateOrganizationStatusApiRequest request) {
        return new UpdateOrganizationStatusCommand(organizationId, request.status());
    }
}
