package com.ryuqq.authhub.adapter.out.persistence.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * RefreshTokenJpaEntity - RefreshToken JPA Entity
 *
 * <p>RefreshToken 정보를 저장합니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>refreshTokenId(UUID)를 PK로 사용
 *   <li>UUIDv7은 시간순 정렬 가능하여 B-tree 인덱스 성능 우수
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 * </ul>
 *
 * <p><strong>Domain 매핑:</strong> RefreshToken은 별도 Domain 객체 없이 String으로 관리됩니다.
 *
 * <ul>
 *   <li>UUID refreshTokenId ← PK (UUIDv7)
 *   <li>UUID userId ← UserId.value() (UUID FK 전략)
 *   <li>String token ← RefreshToken 문자열
 *   <li>LocalDateTime createdAt ← 생성 시각
 *   <li>LocalDateTime updatedAt ← 갱신 시각
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 사용 금지
 *   <li>UUID FK 전략 (JPA 관계 매핑 금지)
 *   <li>of() 팩토리 메서드 사용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(
        name = "refresh_tokens",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_refresh_tokens_user_id",
                    columnNames = {"user_id"})
        },
        indexes = {@Index(name = "idx_refresh_tokens_user_id", columnList = "user_id")})
public class RefreshTokenJpaEntity {

    /** RefreshToken UUID - UUIDv7 (Primary Key) */
    @Id
    @Column(name = "refresh_token_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID refreshTokenId;

    /** 사용자 UUID */
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    /** RefreshToken 문자열 */
    @Column(name = "token", nullable = false, length = 2000)
    private String token;

    /** 생성 시각 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 갱신 시각 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected RefreshTokenJpaEntity() {}

    private RefreshTokenJpaEntity(
            UUID refreshTokenId,
            UUID userId,
            String token,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.refreshTokenId = refreshTokenId;
        this.userId = userId;
        this.token = token;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 RefreshToken Entity 생성
     *
     * @param refreshTokenId RefreshToken UUID (PK)
     * @param userId 사용자 ID (UUID)
     * @param token RefreshToken 문자열
     * @param createdAt 생성 시각
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity forNew(
            UUID refreshTokenId, UUID userId, String token, LocalDateTime createdAt) {
        return new RefreshTokenJpaEntity(refreshTokenId, userId, token, createdAt, createdAt);
    }

    /**
     * 기존 RefreshToken Entity 재구성
     *
     * @param refreshTokenId RefreshToken UUID (PK)
     * @param userId 사용자 ID (UUID)
     * @param token RefreshToken 문자열
     * @param createdAt 생성 시각
     * @param updatedAt 갱신 시각
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity of(
            UUID refreshTokenId,
            UUID userId,
            String token,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new RefreshTokenJpaEntity(refreshTokenId, userId, token, createdAt, updatedAt);
    }

    /**
     * RefreshToken 갱신
     *
     * @param newToken 새로운 토큰 문자열
     * @param updatedAt 갱신 시각
     */
    public void updateToken(String newToken, LocalDateTime updatedAt) {
        this.token = newToken;
        this.updatedAt = updatedAt;
    }

    public UUID getRefreshTokenId() {
        return refreshTokenId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefreshTokenJpaEntity that = (RefreshTokenJpaEntity) o;
        return Objects.equals(refreshTokenId, that.refreshTokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refreshTokenId);
    }
}
