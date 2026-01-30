package com.ryuqq.authhub.adapter.out.persistence.userrole.mapper;

import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.id.UserRoleId;
import org.springframework.stereotype.Component;

/**
 * UserRoleJpaEntityMapper - 사용자-역할 관계 Entity-Domain 변환 Mapper
 *
 * <p>JPA Entity와 Domain 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>변환 규칙:</strong>
 *
 * <ul>
 *   <li>toEntity() - Domain → Entity 변환 (Command 작업용)
 *   <li>toDomain() - Entity → Domain 변환 (Query 작업용)
 *   <li>Lombok 금지 - Plain Java 사용
 * </ul>
 *
 * <p><strong>ID 변환:</strong>
 *
 * <ul>
 *   <li>userRoleId: Long (Entity) ↔ UserRoleId (Domain)
 *   <li>userId: String (Entity) ↔ UserId (Domain)
 *   <li>roleId: Long (Entity) ↔ RoleId (Domain)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleJpaEntityMapper {

    /**
     * Domain → Entity 변환 (Command 작업용)
     *
     * <p>저장을 위해 Domain을 Entity로 변환합니다. 신규 생성 시 forNew(), 기존 데이터 재구성 시 of() 사용
     *
     * @param domain UserRole Domain
     * @return UserRoleJpaEntity
     */
    public UserRoleJpaEntity toEntity(UserRole domain) {
        if (domain.isNew()) {
            return UserRoleJpaEntity.forNew(
                    domain.userIdValue(), domain.roleIdValue(), domain.createdAt());
        }
        return UserRoleJpaEntity.of(
                domain.userRoleIdValue(),
                domain.userIdValue(),
                domain.roleIdValue(),
                domain.createdAt(),
                domain.createdAt());
    }

    /**
     * Entity → Domain 변환 (Query 작업용)
     *
     * <p>조회 결과를 Domain으로 변환합니다. reconstitute 사용 (DB 재구성)
     *
     * @param entity UserRoleJpaEntity
     * @return UserRole Domain
     */
    public UserRole toDomain(UserRoleJpaEntity entity) {
        return UserRole.reconstitute(
                UserRoleId.of(entity.getUserRoleId()),
                UserId.of(entity.getUserId()),
                RoleId.of(entity.getRoleId()),
                entity.getCreatedAt());
    }
}
