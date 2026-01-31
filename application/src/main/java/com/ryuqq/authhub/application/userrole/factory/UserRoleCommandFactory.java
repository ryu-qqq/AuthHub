package com.ryuqq.authhub.application.userrole.factory;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserRoleCommandFactory - 사용자-역할 관계 Command Factory
 *
 * <p>UserRole 도메인 객체를 생성하는 Factory입니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>UserId + RoleId 목록 → UserRole 목록 생성
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
public class UserRoleCommandFactory {

    private final TimeProvider timeProvider;

    public UserRoleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * UserRole 다건 생성
     *
     * <p>이미 검증/필터링된 roleIds로 UserRole 목록을 생성합니다. AssignUserRoleCoordinator에서 호출됩니다.
     *
     * @param userId 사용자 ID (VO)
     * @param roleIds 역할 ID 목록 (VO 리스트)
     * @return UserRole 목록
     */
    public List<UserRole> createAll(UserId userId, List<RoleId> roleIds) {
        Instant now = timeProvider.now();
        return roleIds.stream().map(roleId -> UserRole.create(userId, roleId, now)).toList();
    }
}
