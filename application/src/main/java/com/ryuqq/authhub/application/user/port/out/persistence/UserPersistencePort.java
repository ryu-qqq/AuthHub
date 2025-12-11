package com.ryuqq.authhub.application.user.port.out.persistence;

import com.ryuqq.authhub.domain.user.aggregate.User;

/**
 * UserPersistencePort - 사용자 영속성 Port-Out
 *
 * <p>Persistence Layer에서 구현할 사용자 저장/수정/삭제 인터페이스입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*PersistencePort 또는 *Port 네이밍 규칙
 *   <li>Application Layer에서 정의
 *   <li>Adapter-Out에서 구현
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserPersistencePort {

    /**
     * 사용자 저장 (생성/수정)
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User persist(User user);

    /**
     * 사용자 삭제 (소프트 삭제)
     *
     * @param user 삭제할 사용자
     */
    void delete(User user);
}
