package com.ryuqq.authhub.domain.security.ratelimit;

import com.ryuqq.authhub.domain.security.ratelimit.exception.RateLimitExceededException;
import com.ryuqq.authhub.domain.security.ratelimit.vo.LimitCount;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitRuleId;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
import com.ryuqq.authhub.domain.security.ratelimit.vo.TimeWindow;

import java.time.Instant;
import java.util.Objects;

/**
 * RateLimitRule Aggregate Root.
 *
 * <p>Rate Limiting 규칙을 나타내는 Aggregate Root로서, 제한 정책과 검증 로직을 캡슐화합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Rate Limit 규칙 식별자(RateLimitRuleId) 관리</li>
 *   <li>제한 타입(RateLimitType) 관리 - IP_BASED, USER_BASED, ENDPOINT_BASED</li>
 *   <li>제한 횟수(LimitCount) 관리 - 시간 윈도우 내 허용 횟수</li>
 *   <li>시간 윈도우(TimeWindow) 관리 - 제한 적용 시간 범위</li>
 *   <li>제한 초과 여부 검증 - 비즈니스 로직의 핵심</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ 불변성 보장 - 상태 변경 시 새 인스턴스 반환</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ 도메인 로직 위치 - 제한 확인 로직은 Domain Layer에 위치</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>규칙은 반드시 고유한 RateLimitRuleId를 가져야 함</li>
 *   <li>제한 타입, 제한 횟수, 시간 윈도우는 변경 불가 (생성 시 결정)</li>
 *   <li>제한 횟수는 1 이상이어야 함</li>
 *   <li>시간 윈도우는 1초 이상이어야 함</li>
 *   <li>현재 요청 횟수가 제한 횟수 이상이면 초과로 판단</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // IP 기반 Rate Limit 규칙 생성 (60초에 100회)
 * RateLimitRule rule = RateLimitRule.create(
 *     RateLimitType.IP_BASED,
 *     LimitCount.of(100),
 *     TimeWindow.ofSeconds(60)
 * );
 *
 * // 제한 초과 여부 확인
 * if (rule.isExceeded(105)) {
 *     // 제한 초과 처리 (HTTP 429 응답 등)
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class RateLimitRule {

    private final RateLimitRuleId id;
    private final RateLimitType type;
    private final LimitCount limitCount;
    private final TimeWindow timeWindow;
    private final Instant createdAt;

    /**
     * RateLimitRule 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 규칙 식별자 (null 불가)
     * @param type 제한 타입 (null 불가)
     * @param limitCount 제한 횟수 (null 불가)
     * @param timeWindow 시간 윈도우 (null 불가)
     * @param createdAt 생성 시각 (null 불가)
     */
    private RateLimitRule(
            final RateLimitRuleId id,
            final RateLimitType type,
            final LimitCount limitCount,
            final TimeWindow timeWindow,
            final Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "RateLimitRuleId cannot be null");
        this.type = Objects.requireNonNull(type, "RateLimitType cannot be null");
        this.limitCount = Objects.requireNonNull(limitCount, "LimitCount cannot be null");
        this.timeWindow = Objects.requireNonNull(timeWindow, "TimeWindow cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    /**
     * 새로운 Rate Limit 규칙을 생성합니다 (Factory Method).
     *
     * @param type 제한 타입 (null 불가)
     * @param limitCount 제한 횟수 (null 불가)
     * @param timeWindow 시간 윈도우 (null 불가)
     * @return 새로 생성된 RateLimitRule 인스턴스
     * @throws NullPointerException type, limitCount 또는 timeWindow가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static RateLimitRule create(
            final RateLimitType type,
            final LimitCount limitCount,
            final TimeWindow timeWindow
    ) {
        return new RateLimitRule(
                RateLimitRuleId.newId(),
                type,
                limitCount,
                timeWindow,
                Instant.now()
        );
    }

    /**
     * 기존 데이터로부터 RateLimitRule을 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 규칙 식별자 (null 불가)
     * @param type 제한 타입 (null 불가)
     * @param limitCount 제한 횟수 (null 불가)
     * @param timeWindow 시간 윈도우 (null 불가)
     * @param createdAt 생성 시각 (null 불가)
     * @return 재구성된 RateLimitRule 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static RateLimitRule reconstruct(
            final RateLimitRuleId id,
            final RateLimitType type,
            final LimitCount limitCount,
            final TimeWindow timeWindow,
            final Instant createdAt
    ) {
        return new RateLimitRule(id, type, limitCount, timeWindow, createdAt);
    }

    /**
     * 규칙 식별자를 반환합니다.
     *
     * @return RateLimitRuleId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RateLimitRuleId getId() {
        return this.id;
    }

    /**
     * 제한 타입을 반환합니다.
     *
     * @return RateLimitType enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RateLimitType getType() {
        return this.type;
    }

    /**
     * 제한 횟수를 반환합니다.
     *
     * @return LimitCount 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LimitCount getLimitCount() {
        return this.limitCount;
    }

    /**
     * 시간 윈도우를 반환합니다.
     *
     * @return TimeWindow 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public TimeWindow getTimeWindow() {
        return this.timeWindow;
    }

    /**
     * 생성 시각을 반환합니다.
     *
     * @return 생성 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    /**
     * 규칙 ID를 문자열로 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - id.asString() 대신 사용.
     *
     * @return 규칙 ID 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getIdAsString() {
        return this.id.asString();
    }

    /**
     * 제한 타입 표시 이름을 반환합니다 (Law of Demeter 준수).
     *
     * @return 제한 타입 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getTypeDisplayName() {
        return this.type.getDisplayName();
    }

    /**
     * 제한 타입 설명을 반환합니다 (Law of Demeter 준수).
     *
     * @return 제한 타입 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getTypeDescription() {
        return this.type.getDescription();
    }

    /**
     * 제한 횟수 값을 반환합니다 (Law of Demeter 준수).
     *
     * @return 제한 횟수 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getLimitCountValue() {
        return this.limitCount.value();
    }

    /**
     * 시간 윈도우를 초 단위로 반환합니다 (Law of Demeter 준수).
     *
     * @return 시간 윈도우 (초)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long getTimeWindowSeconds() {
        return this.timeWindow.seconds();
    }

    /**
     * IP 기반 제한 타입인지 확인합니다.
     *
     * @return IP 기반이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isIpBased() {
        return this.type.isIpBased();
    }

    /**
     * 사용자 기반 제한 타입인지 확인합니다.
     *
     * @return 사용자 기반이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUserBased() {
        return this.type.isUserBased();
    }

    /**
     * 엔드포인트 기반 제한 타입인지 확인합니다.
     *
     * @return 엔드포인트 기반이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isEndpointBased() {
        return this.type.isEndpointBased();
    }

    /**
     * 현재 요청 횟수가 제한을 초과했는지 확인합니다 (핵심 비즈니스 로직).
     *
     * <p>이 메서드는 Rate Limiting의 핵심 도메인 로직으로,
     * 현재 요청 횟수와 규칙의 제한 횟수를 비교하여 초과 여부를 판단합니다.</p>
     *
     * <p><strong>판단 기준:</strong></p>
     * <ul>
     *   <li>currentCount >= limitCount → 제한 초과 (true)</li>
     *   <li>currentCount < limitCount → 제한 이내 (false)</li>
     * </ul>
     *
     * @param currentCount 현재 요청 횟수 (0 이상)
     * @return 제한을 초과했으면 true, 아니면 false
     * @throws IllegalArgumentException currentCount가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExceeded(final int currentCount) {
        if (currentCount < 0) {
            throw new IllegalArgumentException("Current count cannot be negative: " + currentCount);
        }
        return this.limitCount.isExceeded(currentCount);
    }

    /**
     * 현재 요청 횟수가 제한 이내인지 확인합니다.
     *
     * @param currentCount 현재 요청 횟수 (0 이상)
     * @return 제한 이내이면 true, 아니면 false
     * @throws IllegalArgumentException currentCount가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isWithinLimit(final int currentCount) {
        if (currentCount < 0) {
            throw new IllegalArgumentException("Current count cannot be negative: " + currentCount);
        }
        return this.limitCount.isWithinLimit(currentCount);
    }

    /**
     * 제한까지 남은 요청 횟수를 계산합니다.
     *
     * @param currentCount 현재 요청 횟수 (0 이상)
     * @return 남은 요청 횟수 (0 이상)
     * @throws IllegalArgumentException currentCount가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getRemainingCount(final int currentCount) {
        if (currentCount < 0) {
            throw new IllegalArgumentException("Current count cannot be negative: " + currentCount);
        }
        return this.limitCount.remainingCount(currentCount);
    }

    /**
     * 제한 초과 시 예외를 발생시킵니다.
     *
     * <p>Application Layer에서 제한 초과를 엄격히 처리할 때 사용합니다.</p>
     *
     * @param currentCount 현재 요청 횟수
     * @throws RateLimitExceededException 제한이 초과된 경우
     * @throws IllegalArgumentException currentCount가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void ensureNotExceeded(final int currentCount) {
        if (this.isExceeded(currentCount)) {
            throw new RateLimitExceededException(
                    currentCount,
                    this.getLimitCountValue(),
                    this.getTimeWindowSeconds()
            );
        }
    }

    /**
     * 두 RateLimitRule 객체의 동등성을 비교합니다.
     * RateLimitRuleId가 같으면 같은 규칙으로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return RateLimitRuleId가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RateLimitRule other = (RateLimitRule) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * RateLimitRuleId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * RateLimitRule의 문자열 표현을 반환합니다.
     *
     * @return "RateLimitRule{id=..., type=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "RateLimitRule{" +
                "id=" + this.id +
                ", type=" + this.type +
                ", limitCount=" + this.limitCount +
                ", timeWindow=" + this.timeWindow +
                ", createdAt=" + this.createdAt +
                '}';
    }
}
