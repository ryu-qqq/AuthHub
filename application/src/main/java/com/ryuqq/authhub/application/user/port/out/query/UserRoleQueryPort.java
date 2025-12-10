package com.ryuqq.authhub.application.user.port.out.query;

import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.util.List;
import java.util.Optional;

/**
 * UserRoleQueryPort - 사용자 역할 조회 Port-Out
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>도메인 객체 기반 인터페이스
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserRoleQueryPort {

    /**
     * 사용자 역할을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 사용자 역할 (없으면 Optional.empty())
     */
    Optional<UserRole> findByUserIdAndRoleId(UserId userId, RoleId roleId);

    /**
     * 사용자의 모든 역할을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 역할 목록
     */
    List<UserRole> findAllByUserId(UserId userId);

    /**
     * 사용자에게 특정 역할이 할당되어 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 할당 여부
     */
    boolean existsByUserIdAndRoleId(UserId userId, RoleId roleId);
}
