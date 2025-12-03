package com.ryuqq.authhub.application.organization.assembler;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.CreateOrganizationResponse;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Component;

/**
 * OrganizationCommandAssembler - Organization Command DTO → Domain 변환 담당
 *
 * <p>Command DTO를 도메인 객체로 변환합니다.
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Command → Domain 변환 로직만 포함
 *   <li>순수 변환 로직만 포함 (비즈니스 로직 금지)
 *   <li>null-safe 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationCommandAssembler {

    private final ClockHolder clockHolder;

    public OrganizationCommandAssembler(ClockHolder clockHolder) {
        this.clockHolder = clockHolder;
    }

    /**
     * CreateOrganizationCommand를 Organization Domain으로 변환
     *
     * @param command CreateOrganizationCommand DTO
     * @return Organization 도메인 객체
     */
    public Organization toOrganization(CreateOrganizationCommand command) {

        return Organization.forNew(
                OrganizationName.of(command.name()),
                TenantId.of(command.tenantId()),
                clockHolder.clock());
    }

    /**
     * Organization Domain을 CreateOrganizationResponse로 변환
     *
     * @param organizationId Organization 도메인 객체
     * @return CreateOrganizationResponse DTO
     * @throws NullPointerException organization이 null인 경우
     */
    public CreateOrganizationResponse toCreateResponse(OrganizationId organizationId) {
        return new CreateOrganizationResponse(organizationId.value());
    }
}
