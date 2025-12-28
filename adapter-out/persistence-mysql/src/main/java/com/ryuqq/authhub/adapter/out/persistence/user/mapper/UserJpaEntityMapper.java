package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

/**
 * UserJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 User 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>User → UserJpaEntity (저장용)
 *   <li>UserJpaEntity → User (조회용)
 * </ul>
 *
 * <p><strong>시간 변환:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC 기준)
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong>
 *
 * <ul>
 *   <li>Adapter Layer의 책임
 *   <li>Domain과 Infrastructure 기술 분리
 *   <li>Domain은 JPA 의존성 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserJpaEntityMapper {

    /**
     * Domain → Entity 변환 (신규 생성용)
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 User 저장 (ID가 null)
     * </ul>
     *
     * @param domain User 도메인
     * @return UserJpaEntity (JPA internal ID = null)
     */
    public UserJpaEntity toEntity(User domain) {
        return UserJpaEntity.of(
                null,
                domain.userIdValue(),
                domain.tenantIdValue(),
                domain.organizationIdValue(),
                domain.getIdentifier(),
                domain.getHashedPassword(),
                domain.getUserStatus(),
                toLocalDateTime(domain.getCreatedAt()),
                toLocalDateTime(domain.getUpdatedAt()));
    }

    /**
     * 기존 Entity 업데이트 (UPDATE용)
     *
     * <p>기존 Entity의 JPA internal ID를 유지하면서 Domain 값으로 업데이트합니다.
     *
     * @param existing 기존 JPA Entity (ID 유지용)
     * @param domain 업데이트할 User 도메인
     * @return UserJpaEntity (기존 JPA internal ID 유지)
     */
    public UserJpaEntity updateEntity(UserJpaEntity existing, User domain) {
        return UserJpaEntity.of(
                existing.getId(),
                domain.userIdValue(),
                domain.tenantIdValue(),
                domain.organizationIdValue(),
                domain.getIdentifier(),
                domain.getHashedPassword(),
                domain.getUserStatus(),
                existing.getCreatedAt(),
                toLocalDateTime(domain.getUpdatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환
     *   <li>Application Layer로 전달
     * </ul>
     *
     * @param entity UserJpaEntity
     * @return User 도메인
     */
    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getUserId()),
                TenantId.of(entity.getTenantId()),
                OrganizationId.of(entity.getOrganizationId()),
                entity.getIdentifier(),
                entity.getHashedPassword(),
                entity.getStatus(),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    /** Instant → LocalDateTime 변환 (UTC 기준) */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    /** LocalDateTime → Instant 변환 (UTC 기준) */
    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}
