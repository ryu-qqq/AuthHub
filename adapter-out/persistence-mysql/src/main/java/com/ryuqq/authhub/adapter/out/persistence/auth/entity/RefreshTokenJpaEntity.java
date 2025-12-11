package com.ryuqq.authhub.adapter.out.persistence.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * RefreshTokenJpaEntity - RefreshToken JPA Entity
 *
 * <p>RefreshToken 정보를 저장합니다.
 *
 * <p><strong>Domain 매핑:</strong> RefreshToken은 별도 Domain 객체 없이 String으로 관리됩니다.
 *
 * <ul>
 *   <li>Long id ← PK (Auto-generated)
 *   <li>UUID userId ← UserId.value() (Long FK 전략)
 *   <li>String token ← RefreshToken 문자열
 *   <li>LocalDateTime createdAt ← 생성 시각
 *   <li>LocalDateTime updatedAt ← 갱신 시각
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 사용 금지
 *   <li>Long FK 전략 (JPA 관계 매핑 금지)
 *   <li>of() 팩토리 메서드 사용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected RefreshTokenJpaEntity() {}

    private RefreshTokenJpaEntity(
            Long id, UUID userId, String token, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 RefreshToken Entity 생성
     *
     * @param userId 사용자 ID (UUID)
     * @param token RefreshToken 문자열
     * @param createdAt 생성 시각
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity forNew(UUID userId, String token, LocalDateTime createdAt) {
        return new RefreshTokenJpaEntity(null, userId, token, createdAt, createdAt);
    }

    /**
     * 기존 RefreshToken Entity 재구성
     *
     * @param id Entity ID
     * @param userId 사용자 ID (UUID)
     * @param token RefreshToken 문자열
     * @param createdAt 생성 시각
     * @param updatedAt 갱신 시각
     * @return RefreshTokenJpaEntity
     */
    public static RefreshTokenJpaEntity of(
            Long id, UUID userId, String token, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new RefreshTokenJpaEntity(id, userId, token, createdAt, updatedAt);
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

    public Long getId() {
        return id;
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
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
