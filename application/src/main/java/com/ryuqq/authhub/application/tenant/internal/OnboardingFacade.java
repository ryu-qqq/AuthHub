package com.ryuqq.authhub.application.tenant.internal;

import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OnboardingFacade - 온보딩 Facade
 *
 * <p>테넌트와 조직 생성을 하나의 트랜잭션으로 처리합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>검증 (테넌트 이름 중복)
 *   <li>Factory로 번들 생성
 *   <li>트랜잭션 내에서 테넌트 → 조직 순차 저장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingFacade {

    private final TenantCommandManager tenantCommandManager;
    private final OrganizationCommandManager organizationCommandManager;

    public OnboardingFacade(
            TenantCommandManager tenantCommandManager,
            OrganizationCommandManager organizationCommandManager) {
        this.tenantCommandManager = tenantCommandManager;
        this.organizationCommandManager = organizationCommandManager;
    }

    /**
     * 온보딩 번들 저장 (테넌트 + 조직 한 번에 저장)
     *
     * <p>메서드 단위로 트랜잭션을 묶어 테넌트와 조직을 원자적으로 저장합니다.
     *
     * @param bundle 온보딩 번들
     * @return 온보딩 결과 (테넌트 ID, 조직 ID)
     */
    @Transactional
    public OnboardingResult persist(OnboardingBundle bundle) {

        String tenantId = tenantCommandManager.persist(bundle.tenant());
        String organizationId = organizationCommandManager.persist(bundle.organization());

        return new OnboardingResult(tenantId, organizationId);
    }
}
