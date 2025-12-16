package com.ryuqq.authhub.application.onboarding.service;

import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import com.ryuqq.authhub.application.onboarding.facade.TenantOnboardingFacade;
import com.ryuqq.authhub.application.onboarding.facade.TenantOnboardingFacade.OnboardingPersistenceResult;
import com.ryuqq.authhub.application.onboarding.port.in.command.TenantOnboardingUseCase;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * TenantOnboardingService - 테넌트 온보딩 Service
 *
 * <p>TenantOnboardingUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Facade 책임)
 *   <li>ReadManager → Facade 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class TenantOnboardingService implements TenantOnboardingUseCase {

    private static final String ADMIN_ROLE_NAME = "ADMIN";

    private final RoleReadManager roleReadManager;
    private final TenantOnboardingFacade onboardingFacade;
    private final PasswordEncoderPort passwordEncoderPort;

    public TenantOnboardingService(
            RoleReadManager roleReadManager,
            TenantOnboardingFacade onboardingFacade,
            PasswordEncoderPort passwordEncoderPort) {
        this.roleReadManager = roleReadManager;
        this.onboardingFacade = onboardingFacade;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public TenantOnboardingResponse execute(TenantOnboardingCommand command) {
        // 1. GLOBAL ADMIN 역할 조회 (tenantId = null)
        Role adminRole = roleReadManager.getByTenantIdAndName(null, RoleName.of(ADMIN_ROLE_NAME));

        // 2. 임시 비밀번호 생성 및 해싱
        String temporaryPassword = generateTemporaryPassword();
        String hashedPassword = passwordEncoderPort.hash(temporaryPassword);

        // 3. Facade를 통해 모든 리소스 일괄 생성
        OnboardingPersistenceResult result =
                onboardingFacade.persistAll(
                        command.tenantName(),
                        command.defaultOrgName(),
                        command.masterEmail(),
                        hashedPassword,
                        adminRole);

        // 4. Response 생성 및 반환
        return TenantOnboardingResponse.of(
                result.tenant().getTenantId().value(),
                result.organization().getOrganizationId().value(),
                result.user().getUserId().value(),
                temporaryPassword);
    }

    /**
     * 임시 비밀번호 생성
     *
     * @return 12자리 임시 비밀번호
     */
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
