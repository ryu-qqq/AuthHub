package com.ryuqq.authhub.application.tenant.validator;

import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Component;

/**
 * OnboardingValidator - 온보딩 비즈니스 규칙 검증
 *
 * <p>온보딩 관련 검증을 담당합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingValidator {

    private final TenantReadManager readManager;

    public OnboardingValidator(TenantReadManager readManager) {
        this.readManager = readManager;
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
}
