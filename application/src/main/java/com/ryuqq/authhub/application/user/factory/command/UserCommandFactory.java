package com.ryuqq.authhub.application.user.factory.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * UserCommandFactory - 사용자 생성 팩토리
 *
 * <p>Command DTO → Domain 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>DTO → Domain 변환만 수행
 *   <li>비즈니스 로직 금지 (Domain 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandFactory {

    private final Clock clock;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserCommandFactory(Clock clock, PasswordEncoderPort passwordEncoderPort) {
        this.clock = clock;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    /**
     * CreateUserCommand → User 변환
     *
     * @param command 사용자 생성 Command
     * @return User 도메인 객체
     */
    public User create(CreateUserCommand command) {
        UserId userId = UserId.of(UUID.randomUUID());
        TenantId tenantId = TenantId.of(command.tenantId());
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        String hashedPassword = passwordEncoderPort.hash(command.password());

        return User.create(
                userId, tenantId, organizationId, command.identifier(), hashedPassword, clock);
    }
}
