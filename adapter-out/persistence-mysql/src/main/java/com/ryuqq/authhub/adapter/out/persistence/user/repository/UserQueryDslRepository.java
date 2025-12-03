package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * UserQueryDslRepository - User QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다. 표준 메서드만 제공합니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findById(id) - ID로 단일 조회
 *   <li>existsById(id) - 존재 여부 확인
 *   <li>existsByTenantIdAndPhoneNumberExcludingUser - Tenant 내 전화번호 중복 확인
 *   <li>existsActiveByOrganizationId - Organization 내 활성 User 존재 여부
 *   <li>findByTenantIdAndIdentifier - Tenant/Identifier로 User 조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JOIN 절대 금지 (Long FK 전략)
 *   <li>N+1 문제 발생 시 별도 쿼리로 분리
 *   <li>DTO Projection 사용 권장
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public class UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QUserJpaEntity user = QUserJpaEntity.userJpaEntity;

    public UserQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 User 조회
     *
     * @param id User ID (UUID)
     * @return UserJpaEntity (Optional)
     */
    public Optional<UserJpaEntity> findById(UUID id) {
        UserJpaEntity result = queryFactory.selectFrom(user).where(user.id.eq(id)).fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id User ID (UUID)
     * @return 존재 여부
     */
    public boolean existsById(UUID id) {
        Integer result = queryFactory.selectOne().from(user).where(user.id.eq(id)).fetchFirst();
        return result != null;
    }

    /**
     * Tenant 내 전화번호 중복 확인 (본인 제외)
     *
     * @param tenantId Tenant ID
     * @param phoneNumber 전화번호
     * @param excludeUserId 제외할 User ID (본인, null 가능)
     * @return 중복 존재 여부
     */
    public boolean existsByTenantIdAndPhoneNumberExcludingUser(
            Long tenantId, String phoneNumber, UUID excludeUserId) {
        var whereBuilder =
                queryFactory
                        .selectOne()
                        .from(user)
                        .where(user.tenantId.eq(tenantId), user.phoneNumber.eq(phoneNumber));

        if (excludeUserId != null) {
            whereBuilder = whereBuilder.where(user.id.ne(excludeUserId));
        }

        Integer result = whereBuilder.fetchFirst();
        return result != null;
    }

    /**
     * Organization 내 활성 User 존재 여부 확인
     *
     * @param organizationId Organization ID
     * @return 활성 User 존재 여부
     */
    public boolean existsActiveByOrganizationId(Long organizationId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(user)
                        .where(
                                user.organizationId.eq(organizationId),
                                user.status.eq(UserStatus.ACTIVE))
                        .fetchFirst();
        return result != null;
    }

    /**
     * Tenant/Identifier로 User 조회 (로그인용)
     *
     * @param tenantId Tenant ID
     * @param identifier 사용자 식별자 (email)
     * @return UserJpaEntity (Optional)
     */
    public Optional<UserJpaEntity> findByTenantIdAndIdentifier(Long tenantId, String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(user)
                        .where(user.tenantId.eq(tenantId), user.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }
}
