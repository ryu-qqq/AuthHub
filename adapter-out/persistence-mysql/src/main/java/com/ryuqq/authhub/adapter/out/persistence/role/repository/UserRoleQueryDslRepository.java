package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.QUserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.UserRoleJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * UserRoleQueryDslRepository - User-Role 매핑 QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findByUserId(userId) - 사용자 ID로 역할 매핑 목록 조회
 *   <li>findRoleIdsByUserId(userId) - 사용자 ID로 역할 ID 목록만 조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JOIN 절대 금지 (Long FK 전략)
 *   <li>Role 정보 필요 시 별도 쿼리로 분리
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public class UserRoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QUserRoleJpaEntity userRole = QUserRoleJpaEntity.userRoleJpaEntity;

    public UserRoleQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자 ID로 User-Role 매핑 목록 조회
     *
     * @param userId 사용자 ID (UUID)
     * @return UserRoleJpaEntity 목록
     */
    public List<UserRoleJpaEntity> findByUserId(UUID userId) {
        return queryFactory.selectFrom(userRole).where(userRole.userId.eq(userId)).fetch();
    }

    /**
     * 사용자 ID로 Role ID 목록만 조회
     *
     * <p>Role 정보가 필요할 때 별도 쿼리로 Role을 조회하기 위한 메서드
     *
     * @param userId 사용자 ID (UUID)
     * @return Role ID 목록
     */
    public List<Long> findRoleIdsByUserId(UUID userId) {
        return queryFactory
                .select(userRole.roleId)
                .from(userRole)
                .where(userRole.userId.eq(userId))
                .fetch();
    }

    /**
     * 사용자-역할 매핑 존재 여부 확인
     *
     * @param userId 사용자 ID (UUID)
     * @param roleId 역할 ID
     * @return 존재 여부
     */
    public boolean existsByUserIdAndRoleId(UUID userId, Long roleId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userRole)
                        .where(userRole.userId.eq(userId).and(userRole.roleId.eq(roleId)))
                        .fetchFirst();
        return result != null;
    }
}
