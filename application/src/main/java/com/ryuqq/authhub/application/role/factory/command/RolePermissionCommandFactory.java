package com.ryuqq.authhub.application.role.factory.command;

import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * RolePermissionCommandFactory - 역할 권한 Command Factory
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
public class RolePermissionCommandFactory {

    private final ClockHolder clockHolder;

    public RolePermissionCommandFactory(ClockHolder clockHolder) {
        this.clockHolder = clockHolder;
    }

    /**
     * GrantRolePermissionCommand를 RolePermission 도메인 객체로 변환합니다.
     *
     * @param command 권한 부여 커맨드
     * @return 역할 권한 도메인 객체
     */
    public RolePermission create(GrantRolePermissionCommand command) {
        return RolePermission.of(
                RoleId.of(command.roleId()),
                PermissionId.of(command.permissionId()),
                Instant.now(clockHolder.clock()));
    }
}
