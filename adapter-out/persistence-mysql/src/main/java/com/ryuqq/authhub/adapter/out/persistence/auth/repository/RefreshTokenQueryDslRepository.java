package com.ryuqq.authhub.adapter.out.persistence.auth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.auth.entity.QRefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.auth.entity.RefreshTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * RefreshTokenQueryDslRepository - RefreshToken QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findByUserId(userId) - 사용자 ID로 조회
 *   <li>existsByUserId(userId) - 존재 여부 확인
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JOIN 절대 금지 (Long FK 전략)
 *   <li>DTO Projection 사용 권장
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public class RefreshTokenQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QRefreshTokenJpaEntity refreshToken =
            QRefreshTokenJpaEntity.refreshTokenJpaEntity;

    public RefreshTokenQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자 ID로 RefreshToken Entity 조회
     *
     * @param userId 사용자 ID (UUID)
     * @return RefreshTokenJpaEntity (Optional)
     */
    public Optional<RefreshTokenJpaEntity> findByUserId(UUID userId) {
        RefreshTokenJpaEntity result =
                queryFactory
                        .selectFrom(refreshToken)
                        .where(refreshToken.userId.eq(userId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 사용자 ID로 RefreshToken 존재 여부 확인
     *
     * @param userId 사용자 ID (UUID)
     * @return 존재 여부
     */
    public boolean existsByUserId(UUID userId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(refreshToken)
                        .where(refreshToken.userId.eq(userId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 토큰 문자열로 RefreshToken Entity 조회
     *
     * @param token RefreshToken 문자열
     * @return RefreshTokenJpaEntity (Optional)
     */
    public Optional<RefreshTokenJpaEntity> findByToken(String token) {
        RefreshTokenJpaEntity result =
                queryFactory
                        .selectFrom(refreshToken)
                        .where(refreshToken.token.eq(token))
                        .fetchOne();
        return Optional.ofNullable(result);
    }
}
