package com.ryuqq.authhub.application.auth.port.out.command;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Set;

/**
 * UserRolePersistencePort - UserRole 영속화 포트 (Command)
 *
 * <p>사용자의 Role 정보를 영속화하는 Command 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>persist() 메서드로 전체 Role 세트 저장/갱신</li>
 *   <li>deleteByUserId() 메서드로 Role 삭제</li>
 *   <li>Value Object 파라미터 (UserId)</li>
 *   <li>조회 메서드 금지 (QueryPort로 분리)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserRolePersistencePort {

    /**
     * UserRole 저장 또는 갱신
     *
     * <p>동일한 UserId에 대해 기존 Role들을 대체하여 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param roles Role 이름 세트 (예: "ROLE_USER", "ROLE_ADMIN")
     */
    void persist(UserId userId, Set<String> roles);

    /**
     * UserId로 UserRole 삭제
     *
     * <p>사용자 삭제 또는 Role 초기화 시 사용
     *
     * @param userId 사용자 ID (Value Object)
     */
    void deleteByUserId(UserId userId);
}
