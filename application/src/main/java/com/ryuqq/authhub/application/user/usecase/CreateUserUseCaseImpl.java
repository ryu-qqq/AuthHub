package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.common.port.out.security.PasswordHasherPort;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.CredentialType;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
        Objects.requireNonNull(command, "CreateUserCommand는 null일 수 없습니다");

        // 1. Tenant 검증
        TenantId tenantId = TenantId.of(command.tenantId());
        Tenant tenant = validateTenant(tenantId);

        // 2. Organization 검증 (선택적)
        OrganizationId organizationId = null;
        if (command.organizationId() != null) {
            organizationId = OrganizationId.of(command.organizationId());
            validateOrganization(organizationId);
        }

        // 3. 비밀번호 해싱
        String hashedPassword = passwordHasherPort.hash(command.rawPassword());

        // 4. User Domain 생성
        User user = createUser(command, tenantId, organizationId, hashedPassword);

        // 5. 영속화
        UserId savedUserId = userPersistencePort.persist(user);

        // 6. Response 생성
        return new CreateUserResponse(savedUserId.value(), user.createdAt());
    }

    private Tenant validateTenant(TenantId tenantId) {
        Tenant tenant = tenantQueryPort.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant를 찾을 수 없습니다: " + tenantId));

        if (!tenant.isActive()) {
            throw new IllegalStateException("Tenant가 비활성 상태입니다: " + tenantId);
        }

        return tenant;
    }

    private void validateOrganization(OrganizationId organizationId) {
        Organization organization = organizationQueryPort.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization을 찾을 수 없습니다: " + organizationId));

        if (!organization.isActive()) {
            throw new IllegalStateException("Organization이 비활성 상태입니다: " + organizationId);
        }
    }

    private User createUser(
            CreateUserCommand command,
            TenantId tenantId,
            OrganizationId organizationId,
            String hashedPassword) {

        CredentialType credentialType = command.credentialType() != null
                ? CredentialType.valueOf(command.credentialType())
                : CredentialType.EMAIL;

        Credential credential = Credential.of(
                credentialType,
                command.identifier(),
                Password.ofHashed(hashedPassword)
        );

        UserProfile profile = UserProfile.of(
                command.name(),
                command.nickname(),
                command.profileImageUrl()
        );

        UserType userType = command.userType() != null
                ? UserType.valueOf(command.userType())
                : null;

        return User.forNew(
                tenantId,
                organizationId,
                userType,
                credential,
                profile,
                clock
        );
    }
}
