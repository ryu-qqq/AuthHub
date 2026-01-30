package com.ryuqq.authhub.application.tenant.factory;

import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * OnboardingFactory - 온보딩 번들 생성 Factory
 *
 * <p>OnboardingCommand로부터 Tenant와 Organization 도메인 객체를 생성합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;

    public OnboardingFactory(TimeProvider timeProvider, IdGeneratorPort idGeneratorPort) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
    }

    /**
     * OnboardingCommand로부터 OnboardingBundle 생성
     *
     * <p>Tenant와 Organization 도메인 객체를 생성하여 번들로 반환합니다.
     *
     * @param command 온보딩 Command
     * @return OnboardingBundle (Tenant, Organization)
     */
    public OnboardingBundle create(OnboardingCommand command) {
        Instant now = timeProvider.now();

        TenantId tenantId = TenantId.forNew(idGeneratorPort.generate());
        Tenant tenant = Tenant.create(tenantId, TenantName.of(command.tenantName()), now);

        OrganizationId organizationId = OrganizationId.forNew(idGeneratorPort.generate());
        Organization organization =
                Organization.create(
                        organizationId,
                        tenant.getTenantId(),
                        OrganizationName.of(command.organizationName()),
                        now);

        return new OnboardingBundle(tenant, organization);
    }
}
