package com.ryuqq.authhub.application.identity.port.out;

import com.ryuqq.authhub.domain.identity.profile.UserProfile;

/**
 * SaveUserProfile Port - UserProfile 저장 Out Port.
 *
 * <p>UserProfile Aggregate를 영속화하는 Out Port입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴을 따르며, Application Layer가 Persistence Layer에 의존하지 않도록 합니다.</p>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>Interface: {@code application/identity/port/out/} (Application Layer)</li>
 *   <li>Adapter: {@code adapter-out-persistence/identity/adapter/} (Persistence Layer)</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>신규 사용자 등록 시 Profile 생성 및 저장</li>
 *   <li>프로필 정보 수정 (nickname, bio 등) 후 저장</li>
 *   <li>프로필 이미지 업데이트 후 저장</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface SaveUserProfilePort {

    /**
     * UserProfile Aggregate를 영속화합니다.
     *
     * <p>새로운 Profile 생성 또는 기존 Profile 수정 후 데이터베이스에 저장합니다.
     * JPA의 save() 메서드를 사용하여 INSERT 또는 UPDATE를 수행합니다.</p>
     *
     * @param profile 저장할 Domain UserProfile (null 불가)
     * @return 영속화된 UserProfile (JPA ID 포함)
     * @throws IllegalArgumentException profile이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    UserProfile save(UserProfile profile);
}
