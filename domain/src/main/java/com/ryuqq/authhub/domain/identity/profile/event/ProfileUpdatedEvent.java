package com.ryuqq.authhub.domain.identity.profile.event;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.profile.vo.UserProfileId;

import java.time.Instant;
import java.util.Objects;

/**
 * 프로필 업데이트 도메인 이벤트.
 *
 * <p>UserProfile Aggregate의 상태 변경 시 발행되는 도메인 이벤트입니다.
 * 프로필 변경에 따른 후속 처리(캐시 무효화, 알림 발송 등)를 비동기로 처리하기 위해 사용됩니다.</p>
 *
 * <p><strong>이벤트 발행 시점:</strong></p>
 * <ul>
 *   <li>닉네임 변경 시</li>
 *   <li>프로필 이미지 변경 시</li>
 *   <li>자기소개 변경 시</li>
 *   <li>프로필 전체 업데이트 시</li>
 * </ul>
 *
 * <p><strong>이벤트 처리 흐름:</strong></p>
 * <pre>
 * 1. UserProfile.updateXXX() 호출
 * 2. registerEvent(ProfileUpdatedEvent) 호출
 * 3. Repository.save() 호출
 * 4. 트랜잭션 커밋 시 Spring Data가 Event 자동 발행
 * 5. @EventListener 또는 @TransactionalEventListener가 이벤트 수신
 * 6. 후속 처리 실행 (캐시 무효화, 알림 등)
 * </pre>
 *
 * <p><strong>Enterprise Pattern 준수:</strong></p>
 * <ul>
 *   <li>✅ AbstractAggregateRoot.registerEvent() 사용</li>
 *   <li>✅ 트랜잭션 커밋 시 자동 발행 (즉시 발행 아님)</li>
 *   <li>✅ Repository.save() 호출 시 Spring Data가 이벤트 발행</li>
 *   <li>✅ 비동기 처리 가능 (@Async 결합)</li>
 *   <li>✅ 도메인 로직과 후속 처리 분리 (SRP)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Javadoc 완비</li>
 * </ul>
 *
 * @param profileId 변경된 프로필의 식별자 (null 불가)
 * @param userId 프로필 소유자의 사용자 식별자 (null 불가)
 * @param occurredAt 이벤트 발생 시각 (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record ProfileUpdatedEvent(
        UserProfileId profileId,
        UserId userId,
        Instant occurredAt
) {

    /**
     * Compact constructor - 필드 유효성 검증을 수행합니다.
     *
     * @throws NullPointerException 필드 중 하나라도 null인 경우
     */
    public ProfileUpdatedEvent {
        Objects.requireNonNull(profileId, "ProfileId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }

    /**
     * ProfileUpdatedEvent를 생성합니다.
     * 이벤트 발생 시각은 현재 시각으로 자동 설정됩니다.
     *
     * @param profileId 변경된 프로필의 식별자 (null 불가)
     * @param userId 프로필 소유자의 사용자 식별자 (null 불가)
     * @return ProfileUpdatedEvent 인스턴스
     * @throws NullPointerException profileId 또는 userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ProfileUpdatedEvent(final UserProfileId profileId, final UserId userId) {
        this(profileId, userId, Instant.now());
    }

    /**
     * 프로필 식별자를 문자열로 반환합니다.
     *
     * @return 프로필 식별자 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getProfileIdAsString() {
        return this.profileId.asString();
    }

    /**
     * 사용자 식별자를 문자열로 반환합니다.
     *
     * @return 사용자 식별자 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUserIdAsString() {
        return this.userId.asString();
    }

    /**
     * 이벤트 발생 시각을 문자열로 반환합니다.
     *
     * @return 이벤트 발생 시각 문자열 (ISO-8601 형식)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getOccurredAtAsString() {
        return this.occurredAt.toString();
    }
}
