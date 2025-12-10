package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * DuplicateRolePermissionException - 역할 권한 중복 예외
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>DomainException 상속
 *   <li>ErrorCode 사용
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateRolePermissionException extends DomainException {

    public DuplicateRolePermissionException(UUID roleId, UUID permissionId) {
        super(
                RoleErrorCode.DUPLICATE_ROLE_PERMISSION,
                Map.of("roleId", roleId, "permissionId", permissionId));
    }
}
