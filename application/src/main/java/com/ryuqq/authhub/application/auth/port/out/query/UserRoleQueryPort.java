package com.ryuqq.authhub.application.auth.port.out.query;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Set;

/**
 * UserRoleQueryPort - UserRole 조회 포트 (Query)
 *
 * <p>사용자의 Role 정보를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>조회 메서드만 제공 (findByUserId, existsByUserId)</li>
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)</li>
 *   <li>Value Object 파라미터 (UserId)</li>
 *   <li>빈 Set 반환 (Role이 없을 경우, null 대신)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserRoleQueryPort {

    /**
     * UserId로 Role 목록 조회
     *
     * <p>Role이 없으면 빈 Set 반환 (null 반환 금지)
     *
     * @param userId 사용자 ID (Value Object)
     * @return Role 이름 세트 (예: "ROLE_USER", "ROLE_ADMIN")
     */
    Set<String> findByUserId(UserId userId);

    /**
     * UserId로 Role 존재 여부 확인
     *
     * @param userId 사용자 ID (Value Object)
     * @return Role 존재 여부
     */
    boolean existsByUserId(UserId userId);
}
