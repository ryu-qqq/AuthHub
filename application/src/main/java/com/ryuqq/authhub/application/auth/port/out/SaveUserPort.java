package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.user.User;

/**
 * SaveUser Port Interface.
 *
 * <p>User Aggregate를 영속화하는 Out Port입니다.
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
 *   <li>신규 사용자 생성 후 저장 (User.create())</li>
 *   <li>로그인 시 User.login() 호출 후 lastLoginAt 갱신</li>
 *   <li>사용자 상태 변경 (activate(), suspend() 등) 후 저장</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface SaveUserPort {

    /**
     * User Aggregate를 영속화합니다.
     *
     * <p>새로운 User 생성 또는 기존 User 수정 후 데이터베이스에 저장합니다.
     * JPA의 save() 메서드를 사용하여 INSERT 또는 UPDATE를 수행합니다.</p>
     *
     * @param user 저장할 Domain User (null 불가)
     * @return 영속화된 User (JPA ID 포함)
     * @throws IllegalArgumentException user가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    User save(User user);
}
