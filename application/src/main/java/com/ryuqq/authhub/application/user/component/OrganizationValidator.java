package com.ryuqq.authhub.application.user.component;

import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.springframework.stereotype.Component;

/**
 * OrganizationValidator - Organization 검증 컴포넌트
 *
 * <p>Organization의 존재 여부 및 상태를 검증합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationValidator {

    private final OrganizationQueryPort organizationQueryPort;

    public OrganizationValidator(OrganizationQueryPort organizationQueryPort) {
        this.organizationQueryPort = organizationQueryPort;
    }

    /**
     * Organization 검증 (존재 + 활성 상태)
     *
     * @param organizationId 검증할 OrganizationId
     * @return 검증된 Organization
     * @throws OrganizationNotFoundException Organization이 존재하지 않는 경우
     * @throws InvalidOrganizationStateException Organization이 비활성 상태인 경우
     */
    public Organization validate(OrganizationId organizationId) {
        Organization organization =
                organizationQueryPort
                        .findById(organizationId)
                        .orElseThrow(
                                () -> new OrganizationNotFoundException(organizationId.value()));

        if (!organization.isActive()) {
            throw new InvalidOrganizationStateException(
                    organizationId.value(), "Organization이 비활성 상태입니다");
        }

        return organization;
    }
}
