package com.ryuqq.authhub.adapter.out.persistence.token.repository;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * RefreshTokenJpaRepository - RefreshToken JPA Repository (Command)
 *
 * <p>Command 작업 (저장/수정/삭제) 전용 Repository입니다.
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>refreshTokenId(UUID)를 PK로 사용
 *   <li>findById()로 UUID 직접 조회 가능
 * </ul>
 *
 * <p><strong>주요 기능:</strong>
 *
 * <ul>
 *   <li>save() - JpaRepository 기본 메서드 사용
 *   <li>deleteByUserId() - 사용자별 토큰 삭제
 *   <li>findByUserId() - 사용자별 토큰 조회 (UPDATE용)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 로직은 QueryDslRepository에서 처리
 *   <li>복잡한 조회는 금지 (CQRS 분리)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    /**
     * 사용자 ID로 RefreshToken 조회 (UPDATE 작업용)
     *
     * @param userId 사용자 ID (UUID)
     * @return RefreshToken Entity (없으면 Optional.empty())
     */
    Optional<RefreshTokenJpaEntity> findByUserId(UUID userId);

    /**
     * 사용자 ID로 RefreshToken 삭제
     *
     * @param userId 사용자 ID (UUID)
     */
    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
