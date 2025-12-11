package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * UserRoleQueryDslRepository - 사용자 역할 QueryDSL Repository (Query 전용)
 *
 * <p>사용자-역할 연결 테이블의 QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByUserIdAndRoleId() - 사용자 ID와 역할 ID로 단건 조회
 *   <li>findAllByUserId() - 사용자 ID로 할당된 모든 역할 조회
 *   <li>existsByUserIdAndRoleId() - 사용자-역할 연결 존재 여부 확인
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: UserRoleJpaRepository (JPA)
 *   <li>Query: UserRoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class UserRoleQueryDslRepository {

    private static final QUserRoleJpaEntity USER_ROLE = QUserRoleJpaEntity.userRoleJpaEntity;

    private final JPAQueryFactory queryFactory;

    public UserRoleQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자 ID와 역할 ID로 단건 조회
     *
     * @param userId 사용자 UUID
     * @param roleId 역할 UUID
     * @return Optional<UserRoleJpaEntity>
     */
    public Optional<UserRoleJpaEntity> findByUserIdAndRoleId(UUID userId, UUID roleId) {
        UserRoleJpaEntity result =
                queryFactory
                        .selectFrom(USER_ROLE)
                        .where(USER_ROLE.userId.eq(userId), USER_ROLE.roleId.eq(roleId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 사용자 ID로 할당된 모든 역할 조회
     *
     * @param userId 사용자 UUID
     * @return UserRoleJpaEntity 목록
     */
    public List<UserRoleJpaEntity> findAllByUserId(UUID userId) {
        return queryFactory
                .selectFrom(USER_ROLE)
                .where(USER_ROLE.userId.eq(userId))
                .orderBy(USER_ROLE.assignedAt.desc())
                .fetch();
    }

    /**
     * 사용자-역할 연결 존재 여부 확인
     *
     * @param userId 사용자 UUID
     * @param roleId 역할 UUID
     * @return 존재 여부
     */
    public boolean existsByUserIdAndRoleId(UUID userId, UUID roleId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(USER_ROLE)
                        .where(USER_ROLE.userId.eq(userId), USER_ROLE.roleId.eq(roleId))
                        .fetchFirst();
        return result != null;
    }
}
