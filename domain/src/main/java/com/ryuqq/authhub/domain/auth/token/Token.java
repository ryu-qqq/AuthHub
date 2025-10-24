package com.ryuqq.authhub.domain.auth.token;

import com.ryuqq.authhub.domain.auth.user.UserId;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Token Aggregate Root.
 *
 * <p>JWT 토큰 도메인의 Aggregate Root로서, 토큰의 핵심 정보와 행위를 캡슐화합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>토큰 식별자(TokenId) 관리</li>
 *   <li>사용자 식별자(UserId) 참조 (Long FK 전략 대신 Value Object 사용)</li>
 *   <li>토큰 타입(TokenType) 관리</li>
 *   <li>JWT 토큰 값(JwtToken) 보관</li>
 *   <li>발급/만료 시각 관리 및 유효성 검증</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ 불변성 보장 - 모든 필드 final, 상태 변경 시 새 인스턴스 반환</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ JWT 생성/검증 로직은 Application Layer Port로 분리</li>
 *   <li>✅ Domain은 순수 비즈니스 로직만 포함</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>토큰은 반드시 고유한 TokenId를 가져야 함</li>
 *   <li>토큰은 특정 사용자(UserId)에게 발급됨</li>
 *   <li>만료 시각이 현재 시각을 지나면 토큰은 무효</li>
 *   <li>토큰 타입에 따라 유효 기간이 다름 (ACCESS: 짧음, REFRESH: 김)</li>
 *   <li>발급 시각은 만료 시각보다 이전이어야 함</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class Token {

    private final TokenId id;
    private final UserId userId;
    private final TokenType type;
    private final JwtToken jwtToken;
    private final IssuedAt issuedAt;
    private final ExpiresAt expiresAt;

    /**
     * Token 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 토큰 식별자 (null 불가)
     * @param userId 사용자 식별자 (null 불가)
     * @param type 토큰 타입 (null 불가)
     * @param jwtToken JWT 토큰 값 (null 불가)
     * @param issuedAt 발급 시각 (null 불가)
     * @param expiresAt 만료 시각 (null 불가)
     */
    private Token(
            final TokenId id,
            final UserId userId,
            final TokenType type,
            final JwtToken jwtToken,
            final IssuedAt issuedAt,
            final ExpiresAt expiresAt
    ) {
        this.id = Objects.requireNonNull(id, "TokenId cannot be null");
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.type = Objects.requireNonNull(type, "TokenType cannot be null");
        this.jwtToken = Objects.requireNonNull(jwtToken, "JwtToken cannot be null");
        this.issuedAt = Objects.requireNonNull(issuedAt, "IssuedAt cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");

        // 도메인 불변식: 발급 시각은 만료 시각보다 이전이어야 함
        if (issuedAt.isAfter(expiresAt.value())) {
            throw new IllegalArgumentException(
                    "IssuedAt must be before ExpiresAt (issued=" + issuedAt.value() + ", expires=" + expiresAt.value() + ")"
            );
        }
    }

    /**
     * 새로운 Token을 생성합니다.
     * 발급 시각은 현재 시각(시스템 기본 Clock UTC)으로 설정되고, 만료 시각은 유효 기간을 더한 값으로 계산됩니다.
     *
     * @param userId 사용자 식별자 (null 불가)
     * @param type 토큰 타입 (null 불가)
     * @param jwtToken JWT 토큰 값 (null 불가)
     * @param validity 유효 기간 (null 불가, 양수)
     * @return 새로 생성된 Token 인스턴스
     * @throws NullPointerException 인자가 null인 경우
     * @throws IllegalArgumentException validity가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Token create(
            final UserId userId,
            final TokenType type,
            final JwtToken jwtToken,
            final Duration validity
    ) {
        return create(userId, type, jwtToken, validity, Clock.systemUTC());
    }

    /**
     * 새로운 Token을 생성합니다.
     * 지정된 Clock의 현재 시각을 발급 시각으로 사용하며, 테스트 시 시간 의존성을 제어할 수 있습니다.
     *
     * @param userId 사용자 식별자 (null 불가)
     * @param type 토큰 타입 (null 불가)
     * @param jwtToken JWT 토큰 값 (null 불가)
     * @param validity 유효 기간 (null 불가, 양수)
     * @param clock 시각 제공자 (null 불가)
     * @return 새로 생성된 Token 인스턴스
     * @throws NullPointerException 인자가 null인 경우
     * @throws IllegalArgumentException validity가 음수이거나 clock이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Token create(
            final UserId userId,
            final TokenType type,
            final JwtToken jwtToken,
            final Duration validity,
            final Clock clock
    ) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null");
        }

        final IssuedAt issuedAt = IssuedAt.now(clock);
        final ExpiresAt expiresAt = issuedAt.calculateExpiresAt(validity);

        return new Token(
                TokenId.newId(),
                userId,
                type,
                jwtToken,
                issuedAt,
                expiresAt
        );
    }

    /**
     * 기존 데이터로부터 Token을 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 토큰 식별자 (null 불가)
     * @param userId 사용자 식별자 (null 불가)
     * @param type 토큰 타입 (null 불가)
     * @param jwtToken JWT 토큰 값 (null 불가)
     * @param issuedAt 발급 시각 (null 불가)
     * @param expiresAt 만료 시각 (null 불가)
     * @return 재구성된 Token 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Token reconstruct(
            final TokenId id,
            final UserId userId,
            final TokenType type,
            final JwtToken jwtToken,
            final IssuedAt issuedAt,
            final ExpiresAt expiresAt
    ) {
        return new Token(id, userId, type, jwtToken, issuedAt, expiresAt);
    }

    /**
     * 토큰 식별자를 반환합니다.
     *
     * @return TokenId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public TokenId getId() {
        return this.id;
    }

    /**
     * 사용자 식별자를 반환합니다.
     *
     * @return UserId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId getUserId() {
        return this.userId;
    }

    /**
     * 토큰 타입을 반환합니다.
     *
     * @return TokenType enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public TokenType getType() {
        return this.type;
    }

    /**
     * JWT 토큰 값을 반환합니다.
     *
     * @return JwtToken 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public JwtToken getJwtToken() {
        return this.jwtToken;
    }

    /**
     * JWT 토큰의 문자열 값을 반환합니다.
     * Law of Demeter 준수 - 외부에서 getJwtToken().value() 체이닝을 방지
     *
     * @return JWT 토큰 문자열 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getJwtValue() {
        return this.jwtToken.value();
    }

    /**
     * 발급 시각을 반환합니다.
     *
     * @return IssuedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public IssuedAt getIssuedAt() {
        return this.issuedAt;
    }

    /**
     * 만료 시각을 반환합니다.
     *
     * @return ExpiresAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ExpiresAt getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * 토큰이 만료되었는지 확인합니다.
     * 시스템 기본 Clock(UTC)을 사용합니다.
     * Law of Demeter 준수 - expiresAt.isExpired()를 직접 호출하지 않고 Token에서 제공
     *
     * @return 만료되었으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired() {
        return this.expiresAt.isExpired();
    }

    /**
     * 토큰이 지정된 Clock의 현재 시각 기준으로 만료되었는지 확인합니다.
     * 테스트 시 Clock을 고정하여 시간 의존성을 제어할 수 있습니다.
     *
     * @param clock 현재 시각을 제공하는 Clock (null 불가)
     * @return 만료되었으면 true, 아니면 false
     * @throws IllegalArgumentException clock이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired(final Clock clock) {
        return this.expiresAt.isExpired(clock);
    }

    /**
     * 토큰이 유효한지 확인합니다 (만료되지 않았는지).
     *
     * @return 유효하면 true, 만료되었으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isValid() {
        return !isExpired();
    }

    /**
     * 액세스 토큰 타입인지 확인합니다.
     * Law of Demeter 준수 - type.isAccessToken()을 직접 호출하지 않고 Token에서 제공
     *
     * @return ACCESS 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isAccessToken() {
        return this.type.isAccessToken();
    }

    /**
     * 리프레시 토큰 타입인지 확인합니다.
     * Law of Demeter 준수 - type.isRefreshToken()을 직접 호출하지 않고 Token에서 제공
     *
     * @return REFRESH 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isRefreshToken() {
        return this.type.isRefreshToken();
    }

    /**
     * 토큰의 남은 유효 시간을 계산합니다.
     * 시스템 기본 Clock(UTC)을 사용합니다.
     *
     * @return 남은 시간 Duration (만료된 경우 음수)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration remainingValidity() {
        return this.expiresAt.remainingTime();
    }

    /**
     * 지정된 Clock의 현재 시각 기준으로 토큰의 남은 유효 시간을 계산합니다.
     * 테스트 시 Clock을 고정하여 시간 의존성을 제어할 수 있습니다.
     *
     * @param clock 현재 시각을 제공하는 Clock (null 불가)
     * @return 남은 시간 Duration (만료된 경우 음수)
     * @throws IllegalArgumentException clock이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration remainingValidity(final Clock clock) {
        return this.expiresAt.remainingTime(clock);
    }

    /**
     * 토큰 발급 후 경과 시간을 계산합니다.
     * 시스템 기본 Clock(UTC)을 사용합니다.
     *
     * @return 경과 시간 Duration (항상 양수)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration age() {
        return this.issuedAt.age();
    }

    /**
     * 지정된 Clock의 현재 시각 기준으로 토큰 발급 후 경과 시간을 계산합니다.
     * 테스트 시 Clock을 고정하여 시간 의존성을 제어할 수 있습니다.
     *
     * @param clock 현재 시각을 제공하는 Clock (null 불가)
     * @return 경과 시간 Duration
     * @throws IllegalArgumentException clock이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration age(final Clock clock) {
        return this.issuedAt.age(clock);
    }

    /**
     * 특정 사용자의 토큰인지 확인합니다.
     *
     * @param userId 확인할 사용자 식별자 (null 불가)
     * @return 동일한 사용자의 토큰이면 true, 아니면 false
     * @throws IllegalArgumentException userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean belongsTo(final UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return this.userId.equals(userId);
    }

    /**
     * 두 Token 객체의 동등성을 비교합니다.
     * TokenId가 같으면 같은 토큰으로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return TokenId가 같으면 true, 아니면 false
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
        Token other = (Token) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * TokenId를 기준으로 계산됩니다.
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
     * Token의 문자열 표현을 반환합니다.
     * 보안을 위해 JWT 토큰 값은 일부만 표시합니다.
     *
     * @return "Token{id=..., userId=..., type=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "Token{" +
                "id=" + this.id +
                ", userId=" + this.userId +
                ", type=" + this.type +
                ", jwtToken=[PROTECTED]" +
                ", issuedAt=" + this.issuedAt +
                ", expiresAt=" + this.expiresAt +
                '}';
    }
}
