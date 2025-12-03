package com.ryuqq.authhub.application.user.service;

import com.ryuqq.authhub.application.user.assembler.UserCommandAssembler;
import com.ryuqq.authhub.application.user.component.OrganizationValidator;
import com.ryuqq.authhub.application.user.component.TenantValidator;
import com.ryuqq.authhub.application.user.component.UserValidator;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.CreateUserUseCase;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Service;

/**
 * CreateUserService - 사용자 생성 UseCase 구현체
 *
 * <p>사용자 생성을 orchestration합니다.
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port (트랜잭션은 Manager에서 관리)
 *   <li>Validator로 검증 로직 위임
 *   <li>Assembler로 변환 로직 위임
 *   <li>비즈니스 로직은 Domain 객체에 위임
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지
 *   <li>Command null 체크 금지 (외부 레이어에서 검증됨)
 *   <li>비즈니스 로직 Service 노출 금지 (Domain에 위임)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserManager userManager;
    private final TenantValidator tenantValidator;
    private final OrganizationValidator organizationValidator;
    private final UserValidator userValidator;
    private final UserCommandAssembler userCommandAssembler;

    public CreateUserService(
            UserManager userManager,
            TenantValidator tenantValidator,
            OrganizationValidator organizationValidator,
            UserValidator userValidator,
            UserCommandAssembler userCommandAssembler) {
        this.userManager = userManager;
        this.tenantValidator = tenantValidator;
        this.organizationValidator = organizationValidator;
        this.userValidator = userValidator;
        this.userCommandAssembler = userCommandAssembler;
    }

    @Override
    public CreateUserResponse execute(CreateUserCommand command) {
        TenantId tenantId = TenantId.of(command.tenantId());

        // 1. Tenant 검증, Organization 검증
        tenantValidator.validate(tenantId);
        organizationValidator.validate(OrganizationId.of(command.organizationId()));

        // 2. phoneNumber 중복 검증 (Validator에 위임)
        userValidator.validatePhoneNumberForCreate(tenantId, command.phoneNumber());

        // 3. User Domain 생성 (Assembler에서 비밀번호 해싱 포함)
        User user = userCommandAssembler.toUser(command);

        // 4. 영속화 (Manager 경유 - 트랜잭션 관리)
        UserId savedUserId = userManager.persist(user);

        // 5. Response 생성
        return new CreateUserResponse(savedUserId.value(), user.createdAt());
    }
}
