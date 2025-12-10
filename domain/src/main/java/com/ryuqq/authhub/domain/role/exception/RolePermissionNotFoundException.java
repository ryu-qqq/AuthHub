package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * RolePermissionNotFoundException - 역할 권한을 찾을 수 없는 예외
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
public class RolePermissionNotFoundException extends DomainException {

    public RolePermissionNotFoundException(UUID roleId, UUID permissionId) {
        super(
                RoleErrorCode.ROLE_PERMISSION_NOT_FOUND,
                Map.of("roleId", roleId, "permissionId", permissionId));
    }
}
