package com.ryuqq.authhub.application.userrole.port.out.command;

import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;

/**
 * UserRolePersistencePort - 사용자-역할 관계 영속화 포트 (Command)
 *
 * <p>UserRole Aggregate의 영속화 작업을 담당하는 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>저장/삭제 메서드만 제공 (persist, delete)
 *   <li>조회 메서드 금지 (QueryPort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 파라미터 (Entity 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserRoleCommandPort {

    /**
     * 사용자-역할 관계 저장
     *
     * @param userRole 저장할 사용자-역할 관계
     * @return 저장된 사용자-역할 관계
     */
    UserRole persist(UserRole userRole);

    /**
     * 사용자-역할 관계 다건 저장
     *
     * @param userRoles 저장할 사용자-역할 관계 목록
     * @return 저장된 사용자-역할 관계 목록
     */
    List<UserRole> persistAll(List<UserRole> userRoles);

    /**
     * 사용자-역할 관계 삭제
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    void delete(UserId userId, RoleId roleId);

    /**
     * 사용자의 모든 역할 관계 삭제 (User 삭제 시 Cascade)
     *
     * @param userId 사용자 ID
     */
    void deleteAllByUserId(UserId userId);

    /**
     * 사용자-역할 관계 다건 삭제
     *
     * @param userId 사용자 ID
     * @param roleIds 삭제할 역할 ID 목록
     */
    void deleteAll(UserId userId, List<RoleId> roleIds);
}
