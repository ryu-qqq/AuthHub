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
import java.security.SecureRandom;
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
    private static final int TEMPORARY_PASSWORD_LENGTH = 12;
    private static final String PASSWORD_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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

        // 4. Response 생성 및 반환 (Law of Demeter 준수 - 편의 메서드 사용)
        return TenantOnboardingResponse.of(
                result.tenantIdValue(),
                result.organizationIdValue(),
                result.userIdValue(),
                temporaryPassword);
    }

    /**
     * 임시 비밀번호 생성 (SecureRandom 사용)
     *
     * <p>보안을 위해 SecureRandom을 사용하여 암호학적으로 안전한 임시 비밀번호를 생성합니다.
     *
     * @return 12자리 임시 비밀번호 (대소문자 + 숫자)
     */
    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(TEMPORARY_PASSWORD_LENGTH);
        for (int i = 0; i < TEMPORARY_PASSWORD_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(PASSWORD_CHARACTERS.length());
            password.append(PASSWORD_CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
