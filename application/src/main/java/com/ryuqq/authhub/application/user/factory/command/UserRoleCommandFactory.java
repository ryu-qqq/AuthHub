package com.ryuqq.authhub.application.user.factory.command;

import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * UserRoleCommandFactory - 사용자 역할 Command Factory
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Command → Domain 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleCommandFactory {

    private final ClockHolder clockHolder;

    public UserRoleCommandFactory(ClockHolder clockHolder) {
        this.clockHolder = clockHolder;
    }

    /**
     * AssignUserRoleCommand를 UserRole 도메인 객체로 변환합니다.
     *
     * @param command 역할 할당 커맨드
     * @return 사용자 역할 도메인 객체
     */
    public UserRole create(AssignUserRoleCommand command) {
        return UserRole.of(
                UserId.of(command.userId()),
                RoleId.of(command.roleId()),
                Instant.now(clockHolder.clock()));
    }
}
