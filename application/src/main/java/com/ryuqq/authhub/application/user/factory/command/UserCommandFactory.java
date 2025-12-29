package com.ryuqq.authhub.application.user.factory.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import org.springframework.stereotype.Component;

/**
 * UserCommandFactory - Command → Domain 변환
 *
 * <p>Command DTO를 Domain Aggregate로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code create*()} 메서드 네이밍
 *   <li>순수 변환만 수행 (비즈니스 로직 금지)
 *   <li>Port 호출 금지 (조회 금지)
 *   <li>{@code @Transactional} 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandFactory {

    private final Clock clock;
    private final UuidHolder uuidHolder;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserCommandFactory(
            Clock clock, UuidHolder uuidHolder, PasswordEncoderPort passwordEncoderPort) {
        this.clock = clock;
        this.uuidHolder = uuidHolder;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    /**
     * CreateUserCommand → User 변환
     *
     * <p>UUIDv7 기반 식별자를 생성하여 User를 생성합니다.
     *
     * @param command 사용자 생성 Command
     * @return 새로운 User 인스턴스 (UUIDv7 ID 할당)
     */
    public User create(CreateUserCommand command) {
        UserId userId = UserId.of(uuidHolder.random());
        TenantId tenantId = TenantId.of(command.tenantId());
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        String hashedPassword = passwordEncoderPort.hash(command.password());

        return User.create(
                userId,
                tenantId,
                organizationId,
                command.identifier(),
                command.phoneNumber(),
                hashedPassword,
                clock);
    }
}
