package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Component;

/**
 * UserRoleJpaEntityMapper - 사용자 역할 Entity/Domain 변환기
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain ↔ Entity 양방향 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>매핑 테이블도 UUID PK 사용
 *   <li>UuidHolder를 통해 UUIDv7 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleJpaEntityMapper {

    private final UuidHolder uuidHolder;

    public UserRoleJpaEntityMapper(UuidHolder uuidHolder) {
        this.uuidHolder = uuidHolder;
    }

    /**
     * Domain 객체를 JPA Entity로 변환합니다.
     *
     * @param userRole 사용자 역할 도메인 객체
     * @return JPA Entity
     */
    public UserRoleJpaEntity toEntity(UserRole userRole) {
        return UserRoleJpaEntity.of(
                uuidHolder.random(),
                userRole.userIdValue(),
                userRole.roleIdValue(),
                userRole.getAssignedAt());
    }

    /**
     * JPA Entity를 Domain 객체로 변환합니다.
     *
     * @param entity JPA Entity
     * @return 사용자 역할 도메인 객체
     */
    public UserRole toDomain(UserRoleJpaEntity entity) {
        return UserRole.reconstitute(
                UserId.of(entity.getUserId()),
                RoleId.of(entity.getRoleId()),
                entity.getAssignedAt());
    }
}
