package com.ryuqq.authhub.application.role.port.out.query;

import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.List;
import java.util.Optional;

/**
 * RoleQueryPort - Role 조회 포트 (Query)
 *
 * <p>Role 정보를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)
 *   <li>Value Object 파라미터 (UserId, RoleId)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RoleQueryPort {

    /**
     * RoleId로 Role 조회
     *
     * @param roleId 역할 ID
     * @return Role (Optional)
     */
    Optional<Role> findById(RoleId roleId);

    /**
     * UserId로 해당 사용자의 모든 Role 조회
     *
     * <p>Role이 없으면 빈 List 반환 (null 반환 금지)
     *
     * @param userId 사용자 ID
     * @return 사용자에게 할당된 Role 목록
     */
    List<Role> findByUserId(UserId userId);

    /**
     * Role 이름으로 조회
     *
     * @param roleName 역할 이름 (예: ROLE_USER)
     * @return Role (Optional)
     */
    Optional<Role> findByName(String roleName);
}
