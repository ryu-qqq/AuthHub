package com.ryuqq.authhub.application.user.port.out.persistence;

import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;

/**
 * UserRolePersistencePort - 사용자 역할 영속성 Port-Out
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
public interface UserRolePersistencePort {

    /**
     * 사용자 역할을 저장합니다.
     *
     * @param userRole 저장할 사용자 역할
     * @return 저장된 사용자 역할
     */
    UserRole save(UserRole userRole);

    /**
     * 사용자 역할을 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    void delete(UserId userId, RoleId roleId);
}
