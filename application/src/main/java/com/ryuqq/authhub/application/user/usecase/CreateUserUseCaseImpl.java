package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.common.port.out.security.PasswordHasherPort;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.common.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateUserUseCaseImpl - 사용자 생성 UseCase 구현체
 *
 * <p>사용자 생성 비즈니스 로직을 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 *   <li>Domain 검증 후 영속화</li>
 *   <li>Response DTO로 변환 후 반환</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final TenantQueryPort tenantQueryPort;
    private final OrganizationQueryPort organizationQueryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final Clock clock;

    public CreateUserUseCaseImpl(
            UserPersistencePort userPersistencePort,
            TenantQueryPort tenantQueryPort,
            OrganizationQueryPort organizationQueryPort,
            PasswordHasherPort passwordHasherPort,
            Clock clock) {
        this.userPersistencePort = userPersistencePort;
        this.tenantQueryPort = tenantQueryPort;
        this.organizationQueryPort = organizationQueryPort;
        this.passwordHasherPort = passwordHasherPort;
        this.clock = clock;
    }

    @Override
    public CreateUserResponse execute(CreateUserCommand command) {
        // TODO: Red Phase - 테스트 실패 확인용 최소 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
