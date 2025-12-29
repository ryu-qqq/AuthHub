package com.ryuqq.authhub.application.role.validator;

import com.ryuqq.authhub.application.role.manager.query.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.exception.DuplicateRolePermissionException;
import com.ryuqq.authhub.domain.role.exception.RolePermissionNotFoundException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import org.springframework.stereotype.Component;

/**
 * RolePermissionValidator - 역할 권한 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code validate*()} 메서드 네이밍
 *   <li>ReadManager만 의존 (Port 직접 호출 금지)
 *   <li>검증 실패 시 Domain Exception throw
 *   <li>{@code @Transactional} 금지 (ReadManager 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionValidator {

    private final RolePermissionReadManager readManager;

    public RolePermissionValidator(RolePermissionReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 역할에 권한이 이미 부여되지 않았는지 검증 (Grant용)
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @throws DuplicateRolePermissionException 이미 부여된 경우
     */
    public void validateNotAlreadyGranted(RoleId roleId, PermissionId permissionId) {
        if (readManager.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new DuplicateRolePermissionException(roleId.value(), permissionId.value());
        }
    }

    /**
     * 역할에 권한이 부여되어 있는지 검증 (Revoke용)
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @throws RolePermissionNotFoundException 부여되지 않은 경우
     */
    public void validateExists(RoleId roleId, PermissionId permissionId) {
        if (!readManager.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new RolePermissionNotFoundException(roleId.value(), permissionId.value());
        }
    }
}
