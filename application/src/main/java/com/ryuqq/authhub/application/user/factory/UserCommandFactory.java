package com.ryuqq.authhub.application.user.factory;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.aggregate.UserUpdateData;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.springframework.stereotype.Component;

/**
 * UserCommandFactory - User Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;
    private final PasswordEncoderClient passwordEncoderClient;

    public UserCommandFactory(
            TimeProvider timeProvider,
            IdGeneratorPort idGeneratorPort,
            PasswordEncoderClient passwordEncoderClient) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
        this.passwordEncoderClient = passwordEncoderClient;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateUserCommand로부터 User 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return User 도메인 객체
     */
    public User create(CreateUserCommand command) {
        UserId userId = UserId.of(idGeneratorPort.generate());
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Identifier identifier = Identifier.of(command.identifier());
        PhoneNumber phoneNumber = PhoneNumber.fromNullable(command.phoneNumber());
        HashedPassword hashedPassword =
                HashedPassword.of(passwordEncoderClient.hash(command.rawPassword()));

        return User.create(
                userId,
                organizationId,
                identifier,
                phoneNumber,
                hashedPassword,
                timeProvider.now());
    }

    // ==================== Update Context 생성 ====================

    /**
     * UpdateUserCommand로부터 UpdateContext 생성
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<UserId, UserUpdateData> createUpdateContext(UpdateUserCommand command) {
        UserId id = UserId.of(command.userId());
        UserUpdateData updateData =
                UserUpdateData.of(PhoneNumber.fromNullable(command.phoneNumber()));
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }

    // ==================== Password Change Context 생성 ====================

    /**
     * ChangePasswordCommand로부터 PasswordChangeContext 생성
     *
     * @param command 비밀번호 변경 Command
     * @return UpdateContext (id, newHashedPassword, changedAt)
     */
    public UpdateContext<UserId, HashedPassword> createPasswordChangeContext(
            ChangePasswordCommand command) {
        UserId id = UserId.of(command.userId());
        HashedPassword newHashedPassword =
                HashedPassword.of(passwordEncoderClient.hash(command.newPassword()));
        return new UpdateContext<>(id, newHashedPassword, timeProvider.now());
    }
}
