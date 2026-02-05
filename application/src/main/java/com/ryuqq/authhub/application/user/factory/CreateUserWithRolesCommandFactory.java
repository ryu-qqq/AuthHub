package com.ryuqq.authhub.application.user.factory;

import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.internal.CreateUserWithRolesBundle;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.springframework.stereotype.Component;

/**
 * CreateUserWithRolesCommandFactory - CreateUserWithRolesCommand → Domain 변환 Factory
 *
 * <p>CreateUserWithRolesCommand를 직접 User 도메인 객체로 변환하고, 빈 UserRole 목록을 가진 Bundle을 생성합니다.
 *
 * <p>UserRole은 Coordinator에서 resolve 후 채워집니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class CreateUserWithRolesCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;
    private final PasswordEncoderClient passwordEncoderClient;

    public CreateUserWithRolesCommandFactory(
            TimeProvider timeProvider,
            IdGeneratorPort idGeneratorPort,
            PasswordEncoderClient passwordEncoderClient) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
        this.passwordEncoderClient = passwordEncoderClient;
    }

    /**
     * CreateUserWithRolesCommand로부터 Bundle 생성
     *
     * <p>User 도메인 객체를 생성하고 빈 UserRole 목록을 가진 Bundle을 반환합니다. UserRole은 Coordinator에서 resolve 후
     * {@code withUserRoles()}로 채워집니다.
     *
     * @param command 생성 Command
     * @return CreateUserWithRolesBundle (User + 빈 UserRole 목록)
     */
    public CreateUserWithRolesBundle create(CreateUserWithRolesCommand command) {
        UserId userId = UserId.of(idGeneratorPort.generate());
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Identifier identifier = Identifier.of(command.identifier());
        PhoneNumber phoneNumber = PhoneNumber.fromNullable(command.phoneNumber());
        HashedPassword hashedPassword =
                HashedPassword.of(passwordEncoderClient.hash(command.rawPassword()));

        User user =
                User.create(
                        userId,
                        organizationId,
                        identifier,
                        phoneNumber,
                        hashedPassword,
                        timeProvider.now());

        return CreateUserWithRolesBundle.of(user, command.serviceCode(), command.roleNames());
    }
}
