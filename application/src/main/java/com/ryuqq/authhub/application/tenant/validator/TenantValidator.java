package com.ryuqq.authhub.application.tenant.validator;

import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Component;

/**
 * TenantValidator - 테넌트 비즈니스 규칙 검증
 *
 * <p>테넌트 관련 비즈니스 규칙 검증을 담당합니다. 조회가 필요한 검증 로직을 Service에서 분리합니다.
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
public class TenantValidator {

    private final TenantReadManager readManager;

    public TenantValidator(TenantReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * Tenant 존재 여부 검증
     *
     * @param id Tenant ID (VO)
     * @throws TenantNotFoundException 존재하지 않는 경우
     */
    public void validateExists(TenantId id) {
        if (!readManager.existsById(id)) {
            throw new TenantNotFoundException(id);
        }
    }

    /**
     * Tenant 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Tenant ID (VO)
     * @return Tenant 조회된 도메인 객체
     * @throws TenantNotFoundException 존재하지 않는 경우
     */
    public Tenant findExistingOrThrow(TenantId id) {
        return readManager.findById(id);
    }

    /**
     * 이름 중복 검증 (신규 생성용)
     *
     * @param name 검증할 테넌트 이름 (VO)
     * @throws DuplicateTenantNameException 동일 이름 테넌트가 존재하는 경우
     */
    public void validateNameNotDuplicated(TenantName name) {
        if (readManager.existsByName(name)) {
            throw new DuplicateTenantNameException(name);
        }
    }

    /**
     * 이름 중복 검증 (수정 시 자기 자신 제외)
     *
     * @param newName 변경할 새 이름 (VO)
     * @param excludeId 제외할 ID (VO)
     * @throws DuplicateTenantNameException 다른 Tenant에서 이미 사용 중인 경우
     */
    public void validateNameNotDuplicatedExcluding(TenantName newName, TenantId excludeId) {
        if (readManager.existsByNameAndIdNot(newName, excludeId)) {
            throw new DuplicateTenantNameException(newName);
        }
    }
}
