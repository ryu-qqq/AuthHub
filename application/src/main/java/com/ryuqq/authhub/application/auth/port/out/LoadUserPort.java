package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.UserId;

import java.util.Optional;

/**
 * LoadUser Port Interface.
 *
 * <p>사용자 ID로 User Aggregate를 조회하는 Out Port입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴을 따르며, Application Layer가 Persistence Layer에 의존하지 않도록 합니다.</p>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>Interface: {@code application/auth/port/out/} (Application Layer)</li>
 *   <li>Adapter: {@code adapter-out-persistence/auth/adapter/} (Persistence Layer)</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그인 시 UserCredential에서 추출한 UserId로 User 상태 확인</li>
 *   <li>User의 상태(ACTIVE, SUSPENDED, DELETED)를 검증하여 로그인 허용 여부 결정</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface LoadUserPort {

    /**
     * 사용자 ID로 User Aggregate를 조회합니다.
     *
     * <p>로그인 시 UserCredential에서 추출한 UserId로 User를 조회하여
     * 사용자 상태(ACTIVE, SUSPENDED, DELETED)를 확인합니다.</p>
     *
     * @param userId 사용자 식별자 Value Object (null 불가)
     * @return Optional로 감싼 User (존재하지 않으면 Empty)
     * @throws IllegalArgumentException userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<User> load(UserId userId);
}
