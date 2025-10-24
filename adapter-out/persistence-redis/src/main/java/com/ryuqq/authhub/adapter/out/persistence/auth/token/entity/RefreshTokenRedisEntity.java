package com.ryuqq.authhub.adapter.out.persistence.auth.token.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;
import java.util.UUID;

/**
 * Refresh Token Redis Entity.
 *
 * <p>Redis에 저장되는 Refresh Token 데이터를 표현하는 Entity입니다.
 * {@code @RedisHash}를 사용하여 Redis에 Hash 구조로 저장되며, TTL(Time To Live)은 14일(1,209,600초)로 설정됩니다.</p>
 *
 * <p><strong>Key Naming Convention:</strong></p>
 * <ul>
 *   <li>Redis Key 패턴: {@code refresh_token:{tokenId}}</li>
 *   <li>예시: {@code refresh_token:{UUID}}</li>
 * </ul>
 *
 * <p><strong>TTL 전략:</strong></p>
 * <ul>
 *   <li>14일(1,209,600초) 후 자동 만료</li>
 *   <li>Redis의 EXPIRE 메커니즘을 활용한 자동 삭제</li>
 *   <li>메모리 효율적 관리 (만료된 토큰 자동 제거)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 직접 구현</li>
 *   <li>✅ Long FK 전략 - userId를 String으로 저장 (UUID → String 변환)</li>
 *   <li>✅ 불변성 보장 - final 필드 사용 (Redis Entity는 mutable for framework)</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>필드 설명:</strong></p>
 * <ul>
 *   <li>{@code tokenId}: Token의 고유 식별자 (UUID String, Redis Key로 사용)</li>
 *   <li>{@code userId}: 사용자 식별자 (UUID String, 인덱싱됨)</li>
 *   <li>{@code jwtToken}: JWT 토큰 문자열 값</li>
 *   <li>{@code issuedAt}: 토큰 발급 시각 (Unix timestamp, milliseconds)</li>
 *   <li>{@code expiresAt}: 토큰 만료 시각 (Unix timestamp, milliseconds)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@RedisHash(value = "refresh_token", timeToLive = 1209600) // 14 days (60 * 60 * 24 * 14)
public class RefreshTokenRedisEntity {

    @Id
    private String tokenId;

    @Indexed
    private String userId;

    private String jwtToken;
    private Long issuedAt;
    private Long expiresAt;

    /**
     * 기본 생성자 (JPA/Redis 프레임워크용).
     * 외부에서 직접 사용하지 않으며, 팩토리 메서드를 통해 생성합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    protected RefreshTokenRedisEntity() {
    }

    /**
     * 모든 필드를 포함한 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param tokenId Token의 고유 식별자 (UUID String) (null 불가)
     * @param userId 사용자 식별자 (UUID String) (null 불가)
     * @param jwtToken JWT 토큰 문자열 값 (null 불가)
     * @param issuedAt 토큰 발급 시각 (Unix timestamp, milliseconds) (null 불가)
     * @param expiresAt 토큰 만료 시각 (Unix timestamp, milliseconds) (null 불가)
     */
    private RefreshTokenRedisEntity(
            final String tokenId,
            final String userId,
            final String jwtToken,
            final Long issuedAt,
            final Long expiresAt
    ) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.jwtToken = jwtToken;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    /**
     * 새로운 RefreshTokenRedisEntity를 생성하는 팩토리 메서드.
     *
     * @param tokenId Token의 고유 식별자 (UUID String) (null 불가)
     * @param userId 사용자 식별자 (UUID String) (null 불가)
     * @param jwtToken JWT 토큰 문자열 값 (null 불가)
     * @param issuedAt 토큰 발급 시각 (Unix timestamp, milliseconds) (null 불가)
     * @param expiresAt 토큰 만료 시각 (Unix timestamp, milliseconds) (null 불가)
     * @return 새로 생성된 RefreshTokenRedisEntity 인스턴스
     * @throws IllegalArgumentException 인자가 null이거나 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static RefreshTokenRedisEntity create(
            final String tokenId,
            final String userId,
            final String jwtToken,
            final Long issuedAt,
            final Long expiresAt
    ) {
        validateNotNull(tokenId, "tokenId");
        validateNotNull(userId, "userId");
        validateNotNull(jwtToken, "jwtToken");
        validateNotNull(issuedAt, "issuedAt");
        validateNotNull(expiresAt, "expiresAt");

        return new RefreshTokenRedisEntity(tokenId, userId, jwtToken, issuedAt, expiresAt);
    }

    /**
     * null 검증 헬퍼 메서드.
     *
     * @param value 검증할 값
     * @param fieldName 필드명 (에러 메시지용)
     * @throws IllegalArgumentException value가 null인 경우
     */
    private static void validateNotNull(final Object value, final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    /**
     * Token ID를 반환합니다.
     *
     * @return Token의 고유 식별자 (UUID String)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getTokenId() {
        return this.tokenId;
    }

    /**
     * User ID를 반환합니다.
     *
     * @return 사용자 식별자 (UUID String)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * JWT 토큰 문자열을 반환합니다.
     *
     * @return JWT 토큰 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getJwtToken() {
        return this.jwtToken;
    }

    /**
     * 토큰 발급 시각을 반환합니다.
     *
     * @return 발급 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getIssuedAt() {
        return this.issuedAt;
    }

    /**
     * 토큰 만료 시각을 반환합니다.
     *
     * @return 만료 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * Token ID를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param tokenId Token의 고유 식별자
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setTokenId(final String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * User ID를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param userId 사용자 식별자
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * JWT 토큰 문자열을 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param jwtToken JWT 토큰 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setJwtToken(final String jwtToken) {
        this.jwtToken = jwtToken;
    }

    /**
     * 토큰 발급 시각을 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param issuedAt 발급 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setIssuedAt(final Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    /**
     * 토큰 만료 시각을 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param expiresAt 만료 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setExpiresAt(final Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * 두 RefreshTokenRedisEntity 객체의 동등성을 비교합니다.
     * tokenId가 같으면 같은 Entity로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return tokenId가 같으면 true, 아니면 false
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
        RefreshTokenRedisEntity other = (RefreshTokenRedisEntity) obj;
        return Objects.equals(this.tokenId, other.tokenId);
    }

    /**
     * 해시 코드를 반환합니다.
     * tokenId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.tokenId);
    }

    /**
     * RefreshTokenRedisEntity의 문자열 표현을 반환합니다.
     * 보안을 위해 JWT 토큰 값은 일부만 표시합니다.
     *
     * @return "RefreshTokenRedisEntity{tokenId=..., userId=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "RefreshTokenRedisEntity{" +
                "tokenId='" + this.tokenId + '\'' +
                ", userId='" + this.userId + '\'' +
                ", jwtToken='[PROTECTED]'" +
                ", issuedAt=" + this.issuedAt +
                ", expiresAt=" + this.expiresAt +
                '}';
    }
}
