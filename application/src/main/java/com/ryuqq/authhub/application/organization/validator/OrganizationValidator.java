package com.ryuqq.authhub.application.organization.validator;

import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Component;

/**
 * OrganizationValidator - 조직 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code validate*()} 메서드 네이밍
 *   <li>ReadManager만 의존 (Port 직접 호출 금지)
 *   <li>검증 실패 시 Domain Exception throw
 *   <li>{@code @Transactional} 금지 (ReadManager 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationValidator {

    private final OrganizationReadManager readManager;

    public OrganizationValidator(OrganizationReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 테넌트 내 조직 이름 중복 검증 (신규 생성용)
     *
     * @param tenantId Tenant ID
     * @param name 검증할 조직 이름
     * @throws DuplicateOrganizationNameException 중복 시
     */
    public void validateNameNotDuplicated(TenantId tenantId, OrganizationName name) {
        if (readManager.existsByTenantIdAndName(tenantId, name)) {
            throw new DuplicateOrganizationNameException(tenantId, name);
        }
    }

    /**
     * 테넌트 내 조직 이름 중복 검증 (수정용 - 자기 자신 제외)
     *
     * <p>현재 이름과 새 이름이 같으면 검증을 건너뜁니다.
     *
     * @param tenantId Tenant ID
     * @param newName 새로운 조직 이름
     * @param currentName 현재 조직 이름
     * @throws DuplicateOrganizationNameException 중복 시
     */
    public void validateNameNotDuplicatedExcluding(
            TenantId tenantId, OrganizationName newName, OrganizationName currentName) {
        if (!newName.equals(currentName)
                && readManager.existsByTenantIdAndName(tenantId, newName)) {
            throw new DuplicateOrganizationNameException(tenantId, newName);
        }
    }
}
