package com.ryuqq.authhub.domain.security.blacklist;

import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistedTokenId;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import com.ryuqq.authhub.domain.security.blacklist.vo.ExpiresAt;
import com.ryuqq.authhub.domain.security.blacklist.vo.Jti;

import java.time.Instant;
import java.util.Objects;

/**
 * BlacklistedToken Aggregate Root.
 *
 * <p>무효화된 JWT 토큰을 관리하는 Aggregate Root로서, 블랙리스트에 등록된 토큰의
 * 식별자(JTI), 만료 시간, 등록 사유 등을 추적합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>블랙리스트 토큰 식별자(BlacklistedTokenId) 관리</li>
 *   <li>JWT ID(Jti) 관리 - 토큰의 고유 식별자 (RFC 7519)</li>
 *   <li>만료 시간(ExpiresAt) 관리 - 블랙리스트 자동 정리 기준</li>
 *   <li>블랙리스트 등록 사유(BlacklistReason) 관리</li>
 *   <li>블랙리스트 등록 시각(blacklistedAt) 관리</li>
 *   <li>토큰 만료 여부 판단 - 자동 정리 대상 판별</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ 불변성 보장 - 모든 필드 final, 생성 후 변경 불가</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ Factory Method 패턴 - create() 및 reconstruct() 메서드 제공</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>블랙리스트 토큰은 반드시 고유한 BlacklistedTokenId를 가져야 함</li>
 *   <li>모든 필드는 생성 시 결정되며 변경 불가 (Immutable)</li>
 *   <li>JTI는 null일 수 없음 (JWT의 필수 클레임)</li>
 *   <li>만료 시간은 null일 수 없음</li>
 *   <li>블랙리스트 등록 사유는 null일 수 없음</li>
 *   <li>블랙리스트 등록 시각은 생성 시각을 기록하며 null일 수 없음</li>
 *   <li>만료된 토큰도 블랙리스트에 등록 가능 (일정 기간 유지 후 정리)</li>
 * </ul>
 *
 * <p><strong>생명주기:</strong></p>
 * <ul>
 *   <li><strong>생성</strong>: create() - 토큰 블랙리스트 등록 시</li>
 *   <li><strong>재구성</strong>: reconstruct() - 영속성 계층에서 복원 시</li>
 *   <li><strong>만료 확인</strong>: isExpired() - 자동 정리 대상 판별</li>
 *   <li><strong>정리</strong>: 만료 후 일정 기간(예: 7일) 경과 시 삭제</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 로그아웃 시 토큰 블랙리스트 등록
 * BlacklistedToken token = BlacklistedToken.create(
 *     Jti.of("unique-jwt-id-123"),
 *     ExpiresAt.fromEpochSeconds(1735689600L),
 *     BlacklistReason.LOGOUT
 * );
 *
 * // 보안 침해로 인한 토큰 무효화
 * BlacklistedToken breachedToken = BlacklistedToken.create(
 *     Jti.of("suspicious-jwt-id-456"),
 *     ExpiresAt.fromEpochSeconds(1735689600L),
 *     BlacklistReason.SECURITY_BREACH
 * );
 *
 * // 만료 확인
 * if (token.isExpired()) {
 *     // 블랙리스트에서 제거 가능
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class BlacklistedToken {

    private final BlacklistedTokenId id;
    private final Jti jti;
    private final ExpiresAt expiresAt;
    private final BlacklistReason reason;
    private final Instant blacklistedAt;

    /**
     * BlacklistedToken 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 블랙리스트 토큰 식별자 (null 불가)
     * @param jti JWT ID (null 불가)
     * @param expiresAt 만료 시간 (null 불가)
     * @param reason 블랙리스트 등록 사유 (null 불가)
     * @param blacklistedAt 블랙리스트 등록 시각 (null 불가)
     */
    private BlacklistedToken(
            final BlacklistedTokenId id,
            final Jti jti,
            final ExpiresAt expiresAt,
            final BlacklistReason reason,
            final Instant blacklistedAt
    ) {
        this.id = Objects.requireNonNull(id, "BlacklistedToken ID cannot be null");
        this.jti = Objects.requireNonNull(jti, "JTI cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");
        this.reason = Objects.requireNonNull(reason, "BlacklistReason cannot be null");
        this.blacklistedAt = Objects.requireNonNull(blacklistedAt, "BlacklistedAt cannot be null");
    }

    /**
     * 새로운 BlacklistedToken을 생성합니다 (Factory Method).
     * 블랙리스트 등록 시각은 현재 시각으로 자동 설정됩니다.
     *
     * @param jti JWT ID (null 불가)
     * @param expiresAt 만료 시간 (null 불가)
     * @param reason 블랙리스트 등록 사유 (null 불가)
     * @return 생성된 BlacklistedToken 인스턴스
     * @throws NullPointerException 필수 파라미터가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static BlacklistedToken create(
            final Jti jti,
            final ExpiresAt expiresAt,
            final BlacklistReason reason
    ) {
        return create(jti, expiresAt, reason, Instant.now());
    }

    /**
     * 새로운 BlacklistedToken을 생성합니다 (Factory Method).
     * 테스트 용이성을 위해 블랙리스트 등록 시각을 외부에서 주입받을 수 있도록 합니다.
     *
     * @param jti JWT ID (null 불가)
     * @param expiresAt 만료 시간 (null 불가)
     * @param reason 블랙리스트 등록 사유 (null 불가)
     * @param blacklistedAt 블랙리스트 등록 시각 (null 불가)
     * @return 생성된 BlacklistedToken 인스턴스
     * @throws NullPointerException 필수 파라미터가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static BlacklistedToken create(
            final Jti jti,
            final ExpiresAt expiresAt,
            final BlacklistReason reason,
            final Instant blacklistedAt
    ) {
        return new BlacklistedToken(
                BlacklistedTokenId.newId(),
                jti,
                expiresAt,
                reason,
                blacklistedAt
        );
    }

    /**
     * 기존 데이터로부터 BlacklistedToken을 재구성합니다 (Reconstruct Pattern).
     * 영속성 계층에서 데이터를 복원할 때 사용됩니다.
     *
     * @param id 블랙리스트 토큰 식별자 (null 불가)
     * @param jti JWT ID (null 불가)
     * @param expiresAt 만료 시간 (null 불가)
     * @param reason 블랙리스트 등록 사유 (null 불가)
     * @param blacklistedAt 블랙리스트 등록 시각 (null 불가)
     * @return 재구성된 BlacklistedToken 인스턴스
     * @throws NullPointerException 필수 파라미터가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static BlacklistedToken reconstruct(
            final BlacklistedTokenId id,
            final Jti jti,
            final ExpiresAt expiresAt,
            final BlacklistReason reason,
            final Instant blacklistedAt
    ) {
        return new BlacklistedToken(id, jti, expiresAt, reason, blacklistedAt);
    }

    /**
     * 현재 시간 기준으로 토큰이 만료되었는지 확인합니다.
     * 만료된 토큰은 블랙리스트에서 제거할 수 있습니다.
     *
     * @return 만료되었으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired() {
        return this.expiresAt.isExpired();
    }

    /**
     * 특정 시간 기준으로 토큰이 만료되었는지 확인합니다.
     * 테스트 용이성을 위해 시간을 외부에서 주입받을 수 있도록 합니다.
     *
     * @param now 기준 시간 (null 불가)
     * @return 만료되었으면 true, 아니면 false
     * @throws IllegalArgumentException now가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired(final Instant now) {
        return this.expiresAt.isExpired(now);
    }

    /**
     * 블랙리스트 토큰 식별자를 반환합니다.
     *
     * @return BlacklistedTokenId
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistedTokenId getId() {
        return this.id;
    }

    /**
     * JWT ID를 반환합니다.
     *
     * @return Jti
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Jti getJti() {
        return this.jti;
    }

    /**
     * 만료 시간을 반환합니다.
     *
     * @return ExpiresAt
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ExpiresAt getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * 블랙리스트 등록 사유를 반환합니다.
     *
     * @return BlacklistReason
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistReason getReason() {
        return this.reason;
    }

    /**
     * 블랙리스트 등록 시각을 반환합니다.
     *
     * @return 블랙리스트 등록 시각
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getBlacklistedAt() {
        return this.blacklistedAt;
    }

    /**
     * 두 BlacklistedToken이 동일한지 비교합니다.
     * 식별자(id)가 같으면 동일한 객체로 간주합니다.
     *
     * @param o 비교 대상 객체
     * @return 동일하면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BlacklistedToken that = (BlacklistedToken) o;
        return Objects.equals(this.id, that.id);
    }

    /**
     * 해시코드를 반환합니다.
     * 식별자(id)의 해시코드를 사용합니다.
     *
     * @return 해시코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * BlacklistedToken의 문자열 표현을 반환합니다.
     *
     * @return 문자열 표현
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "BlacklistedToken{" +
                "id=" + id +
                ", jti=" + jti +
                ", expiresAt=" + expiresAt +
                ", reason=" + reason +
                ", blacklistedAt=" + blacklistedAt +
                '}';
    }
}
