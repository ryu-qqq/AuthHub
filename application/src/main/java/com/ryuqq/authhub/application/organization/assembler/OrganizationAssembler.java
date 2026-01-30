package com.ryuqq.authhub.application.organization.assembler;

import com.ryuqq.authhub.application.organization.dto.response.OrganizationPageResult;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResult;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OrganizationAssembler - Domain → Result 변환
 *
 * <p>Domain Aggregate를 Result DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Result 변환만 (toDomain 금지!)
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
     * Domain → Result 변환 (단건)
     *
     * @param organization Organization Domain
     * @return OrganizationResult DTO
     */
    public OrganizationResult toResult(Organization organization) {
        return new OrganizationResult(
                organization.organizationIdValue(),
                organization.tenantIdValue(),
                organization.nameValue(),
                organization.statusValue(),
                organization.createdAt(),
                organization.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param organizations Organization Domain 목록
     * @return OrganizationResult DTO 목록
     */
    public List<OrganizationResult> toResultList(List<Organization> organizations) {
        if (organizations == null || organizations.isEmpty()) {
            return List.of();
        }
        return organizations.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → OrganizationPageResult 변환
     *
     * @param organizations Organization Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return OrganizationPageResult
     */
    public OrganizationPageResult toPageResult(
            List<Organization> organizations, int page, int size, long totalElements) {
        List<OrganizationResult> content = toResultList(organizations);
        return OrganizationPageResult.of(content, page, size, totalElements);
    }
}
