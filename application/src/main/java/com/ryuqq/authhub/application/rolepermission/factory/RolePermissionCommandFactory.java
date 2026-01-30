package com.ryuqq.authhub.application.rolepermission.factory;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RolePermissionCommandFactory - 역할-권한 관계 Command Factory
 *
 * <p>RolePermission 도메인 객체를 생성하는 Factory입니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>RoleId + PermissionId 목록 → RolePermission 목록 생성
 *   <li>TimeProvider를 통한 현재 시간 주입
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 금지 (단순 생성만)
 *   <li>검증 로직 금지 (Validator/Coordinator에서 수행)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionCommandFactory {

    private final TimeProvider timeProvider;

    public RolePermissionCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * RolePermission 다건 생성
     *
     * <p>이미 검증/필터링된 permissionIds로 RolePermission 목록을 생성합니다. GrantRolePermissionCoordinator에서
     * 호출됩니다.
     *
     * @param roleId 역할 ID (VO)
     * @param permissionIds 권한 ID 목록 (VO 리스트)
     * @return RolePermission 목록
     */
    public List<RolePermission> createAll(RoleId roleId, List<PermissionId> permissionIds) {
        Instant now = timeProvider.now();
        return permissionIds.stream()
                .map(permissionId -> RolePermission.create(roleId, permissionId, now))
                .toList();
    }
}
