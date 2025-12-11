package com.ryuqq.authhub.application.organization.assembler;

import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OrganizationAssembler - Domain → Response 변환
 *
 * <p>Domain Aggregate를 Response DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Response 변환만 (toDomain 금지!)
 *   <li>Port/Repository 의존 금지
 *   <li>비즈니스 로직 금지
 *   <li>Getter 체이닝 금지 (Law of Demeter)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationAssembler {

    /**
     * Domain → Response 변환 (단건)
     *
     * @param organization Organization Domain
     * @return OrganizationResponse DTO
     */
    public OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.organizationIdValue(),
                organization.tenantIdValue(),
                organization.nameValue(),
                organization.statusValue(),
                organization.createdAt(),
                organization.updatedAt());
    }

    /**
     * Domain → Response 변환 (목록)
     *
     * @param organizations Organization Domain 목록
     * @return OrganizationResponse DTO 목록
     */
    public List<OrganizationResponse> toResponseList(List<Organization> organizations) {
        if (organizations == null || organizations.isEmpty()) {
            return List.of();
        }
        return organizations.stream().map(this::toResponse).toList();
    }
}
