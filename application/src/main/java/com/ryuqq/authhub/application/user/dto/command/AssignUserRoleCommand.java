package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * AssignUserRoleCommand - 사용자 역할 할당 Command DTO
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
public record AssignUserRoleCommand(UUID userId, UUID roleId) {

    public AssignUserRoleCommand {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }
        if (roleId == null) {
            throw new IllegalArgumentException("roleId는 null일 수 없습니다");
        }
    }
}
