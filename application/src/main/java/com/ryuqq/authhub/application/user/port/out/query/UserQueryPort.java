package com.ryuqq.authhub.application.user.port.out.query;

import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;

/**
 * UserQueryPort - User Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById)</li>
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)</li>
 *   <li>Value Object 파라미터 (원시 타입 금지)</li>
 *   <li>Domain 반환 (DTO/Entity 반환 금지)</li>
 *   <li>Optional 반환 (단건 조회 시 null 방지)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserQueryPort {

    /**
     * ID로 User 단건 조회
     *
     * @param id User ID (Value Object)
     * @return User Domain (Optional)
     */
    Optional<User> findById(UserId id);

    /**
     * ID로 User 존재 여부 확인
     *
     * @param id User ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(UserId id);
}
