package com.ryuqq.authhub.application.organization.validator;

import com.ryuqq.authhub.application.organization.manager.OrganizationReadManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Component;

/**
 * OrganizationValidator - 조직 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
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
     * Organization 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Organization ID (VO)
     * @return Organization 조회된 도메인 객체
     * @throws com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException 존재하지 않는
     *     경우
     */
    public Organization findExistingOrThrow(OrganizationId id) {
        return readManager.findById(id);
    }

    /**
     * 테넌트 내 조직 이름 중복 검증 (신규 생성용)
     *
     * @param tenantId Tenant ID (VO)
     * @param name 검증할 조직 이름 (VO)
     * @throws DuplicateOrganizationNameException 중복 시
     */
    public void validateNameNotDuplicated(TenantId tenantId, OrganizationName name) {
        if (readManager.existsByTenantIdAndName(tenantId, name)) {
            throw new DuplicateOrganizationNameException(tenantId, name);
        }
    }

    /**
     * 이름 중복 검증 (수정 시 자기 자신 제외)
     *
     * <p>조직의 tenantId를 기준으로 중복 검증합니다.
     *
     * @param newName 변경할 새 이름 (VO)
     * @param organization 기존 조직 (tenantId, 현재 이름 추출용)
     * @throws DuplicateOrganizationNameException 다른 Organization에서 이미 사용 중인 경우
     */
    public void validateNameNotDuplicatedExcluding(
            OrganizationName newName, Organization organization) {
        if (!newName.equals(organization.getName())
                && readManager.existsByTenantIdAndName(organization.getTenantId(), newName)) {
            throw new DuplicateOrganizationNameException(organization.getTenantId(), newName);
        }
    }
}
