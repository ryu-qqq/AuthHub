package com.ryuqq.authhub.application.onboarding.service;

import com.ryuqq.authhub.application.onboarding.assembler.TenantOnboardingAssembler;
import com.ryuqq.authhub.application.onboarding.dto.bundle.TenantOnboardingPersistBundle;
import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import com.ryuqq.authhub.application.onboarding.facade.TenantOnboardingCommandFacade;
import com.ryuqq.authhub.application.onboarding.factory.TenantOnboardingFactory;
import com.ryuqq.authhub.application.onboarding.port.in.TenantOnboardingUseCase;
import org.springframework.stereotype.Service;

/**
 * TenantOnboardingService - 테넌트 온보딩 UseCase 구현
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성합니다.
 *
 * <p><strong>온보딩 프로세스:</strong>
 *
 * <ol>
 *   <li>Factory를 통해 Command → Bundle 변환 (ID 생성, 비밀번호 생성, 역할 조회 포함)
 *   <li>CommandFacade를 통해 Bundle 영속화
 *   <li>Assembler를 통해 Response 변환
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션 (UseCase 구현체)
 *   <li>Factory, Facade, Assembler 조합
 *   <li>{@code @Transactional} 금지 (CommandFacade에서 관리)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class TenantOnboardingService implements TenantOnboardingUseCase {

    private final TenantOnboardingFactory factory;
    private final TenantOnboardingCommandFacade commandFacade;
    private final TenantOnboardingAssembler assembler;

    public TenantOnboardingService(
            TenantOnboardingFactory factory,
            TenantOnboardingCommandFacade commandFacade,
            TenantOnboardingAssembler assembler) {
        this.factory = factory;
        this.commandFacade = commandFacade;
        this.assembler = assembler;
    }

    @Override
    public TenantOnboardingResponse execute(TenantOnboardingCommand command) {
        // 1. Command → Bundle 변환 (ID 생성, 비밀번호 생성, 역할 조회 포함)
        TenantOnboardingPersistBundle bundle = factory.createBundle(command);

        // 2. Bundle 영속화 (Tenant → Organization → User → UserRole)
        TenantOnboardingPersistBundle persistedBundle = commandFacade.persistOnboarding(bundle);

        // 3. Response 변환
        return assembler.toResponse(persistedBundle);
    }
}
