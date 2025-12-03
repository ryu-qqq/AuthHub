package com.ryuqq.authhub.application.user.assembler;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * UserCommandAssembler - User Command DTO → Domain 변환 담당
 *
 * <p>Command DTO를 도메인 객체로 변환합니다.
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Command → Domain 변환 로직만 포함
 *   <li>비밀번호 해싱은 PasswordHasher 사용
 *   <li>순수 변환 로직만 포함 (비즈니스 로직 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandAssembler {

    private final PasswordHasher passwordHasher;
    private final ClockHolder clock;

    public UserCommandAssembler(PasswordHasher passwordHasher, ClockHolder clock) {
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    /**
     * CreateUserCommand를 User Domain으로 변환
     *
     * @param command CreateUserCommand DTO
     * @return User 도메인 객체
     */
    public User toUser(CreateUserCommand command) {
        Objects.requireNonNull(command, "CreateUserCommand는 null일 수 없습니다");

        String hashedPassword = passwordHasher.hash(command.rawPassword());

        Credential credential =
                Credential.of(command.identifier(), Password.ofHashed(hashedPassword));

        UserProfile profile = UserProfile.of(command.name(), command.phoneNumber());

        UserType userType =
                command.userType() != null ? UserType.valueOf(command.userType()) : null;

        return User.forNew(
                TenantId.of(command.tenantId()),
                OrganizationId.of(command.organizationId()),
                userType,
                credential,
                profile,
                clock.clock());
    }
}
