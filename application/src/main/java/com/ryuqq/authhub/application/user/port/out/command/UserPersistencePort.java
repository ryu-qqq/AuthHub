package com.ryuqq.authhub.application.user.port.out.command;

import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;

/**
 * UserPersistencePort - User Aggregate 영속화 포트 (Command)
 *
 * <p>Domain Aggregate를 영속화하는 쓰기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>persist() 메서드 하나만 제공 (INSERT/UPDATE 통합)</li>
 *   <li>save/update/delete 메서드 금지</li>
 *   <li>조회 메서드 금지 (QueryPort로 분리)</li>
 *   <li>Domain Aggregate 파라미터, Value Object 반환</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserPersistencePort {

    /**
     * User 저장 (신규 생성 또는 수정)
     *
     * <p>신규 생성 (ID 없음) → INSERT</p>
     * <p>기존 수정 (ID 있음) → UPDATE (JPA 더티체킹)</p>
     *
     * @param user 저장할 User (Domain Aggregate)
     * @return 저장된 User의 ID (Value Object)
     */
    UserId persist(User user);
}
