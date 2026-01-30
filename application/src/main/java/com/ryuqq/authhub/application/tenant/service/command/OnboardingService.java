package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.factory.OnboardingFactory;
import com.ryuqq.authhub.application.tenant.internal.OnboardingFacade;
import com.ryuqq.authhub.application.tenant.manager.OnboardingIdempotencyCommandManager;
import com.ryuqq.authhub.application.tenant.manager.OnboardingIdempotencyQueryManager;
import com.ryuqq.authhub.application.tenant.port.in.command.OnboardingUseCase;
import com.ryuqq.authhub.application.tenant.validator.OnboardingValidator;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * OnboardingService - 온보딩 Service
 *
 * <p>OnboardingUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → Facade에 위임.
 *
 * <p><strong>멱등키 처리:</strong>
 *
 * <ul>
 *   <li>X-Idempotency-Key 헤더를 통해 전달받은 멱등키로 Redis 캐시 조회
 *   <li>캐시 히트: 저장된 응답 즉시 반환
 *   <li>캐시 미스: 온보딩 실행 후 결과 캐싱 (TTL: 24시간)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class OnboardingService implements OnboardingUseCase {

    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);

    private final OnboardingFacade onboardingFacade;
    private final OnboardingFactory onboardingFactory;
    private final OnboardingValidator validator;
    private final OnboardingIdempotencyQueryManager idempotencyQueryManager;
    private final OnboardingIdempotencyCommandManager idempotencyCommandManager;

    public OnboardingService(
            OnboardingFacade onboardingFacade,
            OnboardingFactory onboardingFactory,
            OnboardingValidator validator,
            OnboardingIdempotencyQueryManager idempotencyQueryManager,
            OnboardingIdempotencyCommandManager idempotencyCommandManager) {
        this.onboardingFacade = onboardingFacade;
        this.onboardingFactory = onboardingFactory;
        this.validator = validator;
        this.idempotencyQueryManager = idempotencyQueryManager;
        this.idempotencyCommandManager = idempotencyCommandManager;
    }

    @Override
    public OnboardingResult execute(OnboardingCommand command) {
        String idempotencyKey = command.idempotencyKey();

        // 멱등키로 캐시 조회
        Optional<OnboardingResult> cached =
                idempotencyQueryManager.findByIdempotencyKey(idempotencyKey);
        if (cached.isPresent()) {
            log.info("Idempotency cache hit for key: {}", idempotencyKey);
            return cached.get();
        }

        // 실제 온보딩 실행
        validator.validateNameNotDuplicated(TenantName.of(command.tenantName()));
        OnboardingBundle bundle = onboardingFactory.create(command);
        OnboardingResult result = onboardingFacade.persist(bundle);

        // 결과 캐싱 (Manager 내부에서 best effort 처리)
        idempotencyCommandManager.save(idempotencyKey, result);

        return result;
    }
}
