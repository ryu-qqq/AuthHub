package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.UserRole;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

/**
 * UserRoleJpaEntityMapper - UserRole Domain ↔ Entity 변환
 *
 * <p>Domain 객체와 JPA Entity 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>시간 변환 전략:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC Zone 기준 변환)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserRoleJpaEntityMapper {

    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Domain → Entity 변환
     *
     * @param userRole UserRole Domain 객체
     * @return UserRoleJpaEntity
     */
    public UserRoleJpaEntity toEntity(UserRole userRole) {
        return UserRoleJpaEntity.of(
                userRole.getId(),
                userRole.getUserId().value(),
                userRole.getRoleId().value(),
                toLocalDateTime(userRole.getAssignedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p>Domain.reconstitute() 메서드를 사용하여 영속화된 데이터를 복원합니다.
     *
     * @param entity UserRoleJpaEntity
     * @return UserRole Domain 객체
     */
    public UserRole toDomain(UserRoleJpaEntity entity) {
        return UserRole.reconstitute(
                entity.getId(),
                UserId.of(entity.getUserId()),
                RoleId.of(entity.getRoleId()),
                toInstant(entity.getAssignedAt()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, UTC);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(UTC).toInstant();
    }
}
