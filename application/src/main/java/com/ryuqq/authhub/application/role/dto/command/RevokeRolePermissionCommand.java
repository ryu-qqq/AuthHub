package com.ryuqq.authhub.application.role.dto.command;

import java.util.UUID;

/**
 * RevokeRolePermissionCommand - 역할에서 권한 해제 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>불변 객체
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record RevokeRolePermissionCommand(UUID roleId, UUID permissionId) {

    public RevokeRolePermissionCommand {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId는 null일 수 없습니다");
        }
        if (permissionId == null) {
            throw new IllegalArgumentException("permissionId는 null일 수 없습니다");
        }
    }
}
