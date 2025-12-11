package com.ryuqq.authhub.application.user.manager.query;

import com.ryuqq.authhub.application.user.port.out.query.UserRoleQueryPort;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRoleReadManager - 사용자 역할 조회 관리자
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>{@code @Transactional(readOnly = true)} 책임
 *   <li>QueryPort를 통한 조회
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class UserRoleReadManager {

    private final UserRoleQueryPort queryPort;

    public UserRoleReadManager(UserRoleQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 사용자 역할을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 사용자 역할 (없으면 Optional.empty())
     */
    public Optional<UserRole> findByUserIdAndRoleId(UserId userId, RoleId roleId) {
        return queryPort.findByUserIdAndRoleId(userId, roleId);
    }

    /**
     * 사용자의 모든 역할을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 역할 목록
     */
    public List<UserRole> findAllByUserId(UserId userId) {
        return queryPort.findAllByUserId(userId);
    }

    /**
     * 사용자에게 특정 역할이 할당되어 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 할당 여부
     */
    public boolean existsByUserIdAndRoleId(UserId userId, RoleId roleId) {
        return queryPort.existsByUserIdAndRoleId(userId, roleId);
    }
}
