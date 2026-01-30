package com.ryuqq.authhub.application.rolepermission.validator;

import com.ryuqq.authhub.application.permission.internal.PermissionUsageChecker;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.rolepermission.exception.PermissionInUseException;
import org.springframework.stereotype.Component;

/**
 * RolePermissionValidator - 역할-권한 관계 Validator
 *
 * <p>역할-권한 관계 관련 비즈니스 검증을 수행합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Permission 삭제 시 사용 여부 검증 (PermissionUsageChecker 구현)
 * </ul>
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>Role 존재 여부는 RoleValidator에서 검증
 *   <li>Permission 존재 여부는 PermissionValidator에서 검증
 *   <li>중복 부여 필터링은 GrantRolePermissionCoordinator에서 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionValidator implements PermissionUsageChecker {

    private final RolePermissionReadManager readManager;

    public RolePermissionValidator(RolePermissionReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 권한이 어떤 역할에라도 부여되어 있는지 검증
     *
     * <p>Permission 삭제 시 사용 여부를 확인합니다. PermissionDeleteCoordinator에서 호출됩니다.
     *
     * @param permissionId 권한 ID
     * @throws PermissionInUseException 권한이 역할에 부여되어 있는 경우
     */
    @Override
    public void validateNotInUse(PermissionId permissionId) {
        if (readManager.existsByPermissionId(permissionId)) {
            throw new PermissionInUseException(permissionId);
        }
    }
}
