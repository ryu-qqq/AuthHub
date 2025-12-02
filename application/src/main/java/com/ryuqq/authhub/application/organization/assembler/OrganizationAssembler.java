package com.ryuqq.authhub.application.organization.assembler;

import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * OrganizationAssembler - Organization Domain ↔ DTO 변환 담당
 *
 * <p>도메인 객체와 응답 DTO 간의 변환을 담당하는 어셈블러입니다.
 *
 * <p><strong>규칙:</strong>
 * <ul>
 *   <li>순수 변환 로직만 포함 (비즈니스 로직 금지)</li>
 *   <li>null-safe 변환</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationAssembler {

    /**
     * Organization Domain을 OrganizationResponse로 변환
     *
     * @param organization Organization 도메인 객체
     * @return OrganizationResponse DTO
     * @throws NullPointerException organization이 null인 경우
     */
    public OrganizationResponse toResponse(Organization organization) {
        Objects.requireNonNull(organization, "Organization은 null일 수 없습니다");

        return new OrganizationResponse(
                organization.organizationIdValue(),
                organization.tenantIdValue(),
                organization.organizationNameValue(),
                organization.statusValue(),
                organization.createdAt(),
                organization.updatedAt()
        );
    }
}
